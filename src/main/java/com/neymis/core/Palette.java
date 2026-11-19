package com.neymis.core;

/** Единая палитра линейки Neymis. */
public final class Palette {

    public static boolean dark = true;

    public static int bgStart;
    public static int bgEnd;
    public static int text;
    public static int textDim;
    public static int accent;
    public static int userBubble;
    public static int userText;
    public static int aiBubble;
    public static int aiText;
    public static int codeBg;
    public static int inputBg;
    public static int stroke;
    public static int meta;
    public static int error;
    public static int card;

    private Palette() {
    }

    public static void apply(boolean darkMode) {
        dark = darkMode;
        if (darkMode) {
            bgStart = 0xFF120A24;
            bgEnd = 0xFF0B2545;
            text = 0xFFE8EAF6;
            textDim = 0xFF9E9AC8;
            accent = 0xFFB388FF;
            userBubble = 0xFF4527A0;
            userText = 0xFFFFFFFF;
            aiBubble = 0xFF1B1830;
            aiText = 0xFFE8EAF6;
            codeBg = 0xFF0C0C16;
            inputBg = 0xFF1E1B33;
            stroke = 0xFF2A2640;
            meta = 0xFF7E7AA0;
            error = 0xFFFF8A80;
            card = 0xFF231F3B;
        } else {
            bgStart = 0xFFF7F4FF;
            bgEnd = 0xFFE8F1FF;
            text = 0xFF1A1A1F;
            textDim = 0xFF6B6B80;
            accent = 0xFF5E35B1;
            userBubble = 0xFF5E35B1;
            userText = 0xFFFFFFFF;
            aiBubble = 0xFFFFFFFF;
            aiText = 0xFF1A1A1F;
            codeBg = 0xFFEDEAF6;
            inputBg = 0xFFFFFFFF;
            stroke = 0xFFDED8F0;
            meta = 0xFF8A86A0;
            error = 0xFFC62828;
            card = 0xFFF1EDFB;
        }
    }
}
