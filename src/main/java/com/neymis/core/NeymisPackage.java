package com.neymis.core;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Единый формат переноса данных между приложениями Neymis.
 * Один файл .neymis содержит JSON чатов Codex, закладок/истории Web
 * и при желании данные других приложений.
 */
public final class NeymisPackage {

    public static final String FORMAT = "neymis-package-v1";
    public static final String CODEX = "codex";
    public static final String WEB = "web";
    public static final String TAIL = "tail";
    public static final String FILES = "files";

    private NeymisPackage() {
    }

    public static String create(String codex, String web, String tail, String files) {
        try {
            JSONObject root = new JSONObject();
            root.put("format", FORMAT);
            root.put("exported", new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    .format(new Date()));
            JSONObject apps = new JSONObject();
            apps.put(CODEX, value(codex));
            apps.put(WEB, value(web));
            apps.put(TAIL, value(tail));
            apps.put(FILES, value(files));
            root.put("apps", apps);
            return root.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private static String value(String text) {
        return text == null ? "" : text;
    }

    public static boolean valid(String raw) {
        try {
            JSONObject root = new JSONObject(raw);
            return FORMAT.equals(root.optString("format", ""));
        } catch (Exception e) {
            return false;
        }
    }

    public static String get(String raw, String app) {
        try {
            JSONObject root = new JSONObject(raw);
            if (!FORMAT.equals(root.optString("format", ""))) return "";
            JSONObject apps = root.optJSONObject("apps");
            if (apps == null) return "";
            return apps.optString(app, "");
        } catch (Exception e) {
            return "";
        }
    }

    /** Человекочитаемое описаниеContents файла. */
    public static String describe(String raw) {
        try {
            JSONObject root = new JSONObject(raw);
            JSONObject apps = root.optJSONObject("apps");
            StringBuilder out = new StringBuilder();
            out.append("Формат: ").append(root.optString("format", "?")).append('\n');
            out.append("Дата: ").append(root.optString("exported", "?")).append("\n\n");
            if (apps != null) {
                String[] keys = {CODEX, WEB, TAIL, FILES};
                for (String key : keys) {
                    String data = apps.optString(key, "");
                    if (!data.isEmpty()) {
                        out.append("• ").append(key).append(" — ")
                                .append(data.length()).append(" симв.\n");
                    }
                }
            }
            return out.toString();
        } catch (Exception e) {
            return "Не удалось прочитать файл.";
        }
    }

    public static Uri saveToDownloads(Context context, String raw, String fileName, String folder) {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                ContentValues values = new ContentValues();
                values.put("_display_name", fileName);
                values.put("mime_type", "application/json");
                values.put("relative_path", Environment.DIRECTORY_DOWNLOADS + "/" + folder + "/");
                Uri uri = context.getContentResolver().insert(
                        Uri.parse("content://media/external/downloads"), values);
                if (uri == null) return null;
                OutputStream out = context.getContentResolver().openOutputStream(uri);
                if (out != null) {
                    out.write(raw.getBytes("UTF-8"));
                    out.close();
                }
                return uri;
            } catch (Exception e) {
                return null;
            }
        }
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), folder);
            if (!dir.exists() && !dir.mkdirs()) return null;
            File file = new File(dir, fileName);
            FileOutputStream out = new FileOutputStream(file);
            out.write(raw.getBytes("UTF-8"));
            out.close();
            return Uri.fromFile(file);
        } catch (Exception e) {
            return null;
        }
    }

    public static String read(Context context, Uri uri) {
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);
            if (in == null) return "";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) > 0) out.write(buffer, 0, count);
            in.close();
            return new String(out.toByteArray(), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static Intent openPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }
}
