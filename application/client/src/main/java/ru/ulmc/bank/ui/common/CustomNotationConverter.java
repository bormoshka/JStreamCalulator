package ru.ulmc.bank.ui.common;

/**
 * Утилита для перевода в "альтернативные" системы счисления. Предполагаемое использование -
 * генерация коротких кодов.
 */
public class CustomNotationConverter {
    /**
     * Base64 символы
     */
    private static final char[] base64Order = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789+/").toCharArray();
    /**
     * Буквенно-численные символы. Для безопасной передачи в URL
     */
    private static final char[] urlSafeAlphanum = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789").toCharArray();

    public static String toAlphaNumUsingBase64(int i) {
        return intToCustomNotationString(i, base64Order);
    }

    public static String toAlphaNumOnly(int i) {
        return intToCustomNotationString(i, urlSafeAlphanum);
    }

    public static String toAlphaNumUsingBase64(long i) {
        return longToCustomNotationString(i, base64Order);
    }

    public static String toAlphaNumOnly(long i) {
        return longToCustomNotationString(i, urlSafeAlphanum);
    }

    private static String intToCustomNotationString(int i, char[] alphabet) {
        if (i < 0) {
            i = i * -1;
            if (i == Integer.MIN_VALUE) {
                i = Integer.MAX_VALUE;
            }
        }
        int radix = alphabet.length;
        char buf[] = new char[6];
        int charPos = 5;
        while (i >= radix) {
            buf[charPos--] = alphabet[(i % radix)];
            i = i / radix;
        }
        buf[charPos] = alphabet[i];
        return new String(buf, charPos, (6 - charPos));
    }

    private static String longToCustomNotationString(long i, char[] alphabet) {
        if (i < 0) {
            i = i * -1;
            if (i == Long.MIN_VALUE) {
                i = Long.MAX_VALUE;
            }
        }
        int radix = alphabet.length;
        char buf[] = new char[11];
        int charPos = 10;

        while (i >= radix) {
            buf[charPos--] = alphabet[(int) (i % radix)];
            i = i / radix;
        }
        buf[charPos] = alphabet[(int) i];
        return new String(buf, charPos, (11 - charPos));
    }
}
