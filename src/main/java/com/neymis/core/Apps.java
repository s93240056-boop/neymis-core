package com.neymis.core;

/** Единый список приложений линейки. */
public final class Apps {

    public static final String[] NAMES = {
            "Neymis Files", "Neymis Tail", "Neymis Codex", "Neymis Web", "Neymis Hub"
    };

    public static final String[] PACKAGES = {
            Intents.FILES, Intents.TAIL, Intents.CODEX, Intents.WEB, Intents.HUB
    };

    public static final String[] REPOS = {
            "https://github.com/s93240056-boop/neymis-files",
            "https://github.com/s93240056-boop/neymis-tail",
            "https://github.com/s93240056-boop/neymis-codex",
            "https://github.com/s93240056-boop/neymis-web",
            "https://github.com/s93240056-boop/neymis-hub"
    };

    private Apps() {
    }

    /** Текст для окна «О приложении» в любом приложении Neymis. */
    public static String about(String current, String version) {
        StringBuilder text = new StringBuilder();
        text.append(current).append(" ").append(version).append("\n\n");
        text.append("Приложения Neymis:\n");
        for (String name : NAMES) text.append("• ").append(name).append("\n");
        text.append("\nОткрытый код, MIT, без рекламы.\n");
        text.append("github.com/s93240056-boop");
        return text.toString();
    }
}
