
package com.baidu.zjl.utils;

public class Utils {

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null)
            return null;

        StringBuilder ret = new StringBuilder(2 * bytes.length);

        for (int i = 0; i < bytes.length; i++) {
            int b;

            b = 0x0f & (bytes[i] >> 4);

            ret.append("0123456789abcdef".charAt(b));

            b = 0x0f & bytes[i];

            ret.append("0123456789abcdef".charAt(b));
        }

        return ret.toString();
    }

    public static byte[] hexStringToBytes(String s) {
        byte[] ret;

        if (s == null)
            return null;

        int sz = s.length();

        ret = new byte[sz / 2];

        for (int i = 0; i < sz; i += 2) {
            ret[i / 2] = (byte) ((hexCharToInt(s.charAt(i)) << 4)
                    | hexCharToInt(s.charAt(i + 1)));
        }

        return ret;
    }

    private static int hexCharToInt(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        if ('A' <= c && c <= 'F') {
            return c - 'A' + 10;
        }
        if ('a' <= c && c <= 'f') {
            return c - 'a' + 10;
        }
        throw new RuntimeException("invalid hex char '" + c + "'");
    }
}
