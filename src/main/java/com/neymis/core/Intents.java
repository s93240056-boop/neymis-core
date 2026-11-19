package com.neymis.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/** Связи между приложениями линейки Neymis. */
public final class Intents {

    public static final String WEB = "com.neymis.web";
    public static final String CODEX = "com.neymis.codex";
    public static final String FILES = "com.neymis.files";
    public static final String TAIL = "com.neymis.tail";
    public static final String HUB = "com.neymis.hub";

    private Intents() {
    }

    public static boolean openApp(Context context, String pkg) {
        Intent launch = context.getPackageManager().getLaunchIntentForPackage(pkg);
        if (launch == null) return false;
        try {
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launch);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Открыть Neymis Hub — витрину приложений и обновлений. */
    public static boolean openHub(Context context) {
        return openApp(context, HUB);
    }

    public static void openWeb(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage(WEB);
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        } catch (Exception ignored) {
        }
        intent.setPackage(null);
        try {
            context.startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    /** Отправить текст в Neymis Codex: откроется поле ввода с этим текстом. */
    public static void askCodex(Context context, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setPackage(CODEX);
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        } catch (Exception ignored) {
        }
        intent.setPackage(null);
        try {
            context.startActivity(Intent.createChooser(intent, "Отправить в Neymis"));
        } catch (Exception ignored) {
        }
    }

    public static void share(Context context, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            context.startActivity(Intent.createChooser(intent, "Поделиться"));
        } catch (Exception ignored) {
        }
    }
}
