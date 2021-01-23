package com.applin.selmer.util;

public class StringUtils {

    public static char[] str_dup_escape(char[] orig, int pos, int amount, char escape_char) {
        char[] buffer = new char[amount];
        for (int i =0; i < amount; i++) {
            buffer[i] = orig[pos + i];
        }
        return buffer;
    }

    public static char[] str_dup(char[] orig, int pos, int amount) {
        char[] buffer = new char[amount];
        for (int i =0; i < amount; i++) {
            buffer[i] = orig[pos + i];
        }
        return buffer;
    }

    public static int str_escape_amount(char[] str, char escape_char) {
        int total_amout = 0;
        for (int i = 0; i < str.length; i++) {
            if (str[i] == escape_char) {
                int extra = 0;
                int j = i;
                while (j < str.length - 1 && str[++j] == escape_char) {
                    extra++;
                }
                i += extra;
                total_amout += extra == 0 ? extra : ((extra + 1) / 2);
            }
        }
        return total_amout;
    }

    public static boolean str_compare(char[] first, int offset_first, String second, int offest_second, int len) {
        for (int i = 0; i < len; i++) {
            if (first[i + offset_first] != second.charAt(i + offest_second)) {
                return false;
            }
        }
        return true;
    }

}
