package com.neymis.core;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Общая система диагностики для всех приложений Neymis. */
public final class CrashLog {

    private static final Object LOCK = new Object();

    private CrashLog() {
    }

    public static File file(Context context) {
        return new File(context.getFilesDir(), "last_crash.txt");
    }

    public static File traceFile(Context context) {
        return new File(context.getFilesDir(), "debug_trace.txt");
    }

    public static String read(Context context) {
        return readFile(file(context));
    }

    public static String readTrace(Context context) {
        return readFile(traceFile(context));
    }

    private static String readFile(File file) {
        try {
            if (!file.exists()) return "";
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int count;
            while ((count = in.read(buffer)) > 0) out.write(buffer, 0, count);
            in.close();
            return new String(out.toByteArray(), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static void clear(Context context) {
        try {
            file(context).delete();
        } catch (Exception ignored) {
        }
    }

    public static void trace(Context context, String line) {
        synchronized (LOCK) {
            try {
                File file = traceFile(context);
                if (file.length() > 200000) file.delete();
                FileOutputStream out = new FileOutputStream(file, true);
                String time = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(new Date());
                out.write((time + "  " + line + "\n").getBytes("UTF-8"));
                out.close();
            } catch (Throwable ignored) {
            }
        }
    }

    public static void install(final Context context) {
        final Thread.UncaughtExceptionHandler previous =
                Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable error) {
                try {
                    StringWriter writer = new StringWriter();
                    error.printStackTrace(new PrintWriter(writer));
                    String header = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                            .format(new Date()) + "  [" + thread.getName() + "]\n\n";
                    FileOutputStream out = new FileOutputStream(file(context));
                    out.write((header + writer.toString()).getBytes("UTF-8"));
                    out.close();
                } catch (Throwable ignored) {
                }
                if (previous != null) previous.uncaughtException(thread, error);
            }
        });
    }

    public static void export(Context context, String folder, String fileName) {
        try {
            String crash = read(context);
            String trace = readTrace(context);
            if (crash.isEmpty() && trace.isEmpty()) return;
            String text = "=== CRASH ===\n" + crash + "\n\n=== TRACE ===\n" + trace;
            writeDownload(context, fileName, text, folder);
        } catch (Throwable ignored) {
        }
    }

    private static void writeDownload(Context context, String name, String text, String folder) {
        if (Build.VERSION.SDK_INT < 29) return;
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            String relative = Environment.DIRECTORY_DOWNLOADS + "/" + folder + "/";

            String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND "
                    + MediaStore.MediaColumns.RELATIVE_PATH + "=?";
            Cursor cursor = resolver.query(collection,
                    new String[]{MediaStore.MediaColumns._ID}, selection,
                    new String[]{name, relative}, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        Uri old = Uri.withAppendedPath(collection, String.valueOf(cursor.getLong(0)));
                        resolver.delete(old, null, null);
                    }
                } finally {
                    cursor.close();
                }
            }

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, relative);
            Uri uri = resolver.insert(collection, values);
            if (uri == null) return;
            OutputStream out = resolver.openOutputStream(uri);
            if (out != null) {
                out.write(text.getBytes("UTF-8"));
                out.close();
            }
        } catch (Throwable ignored) {
        }
    }
}
