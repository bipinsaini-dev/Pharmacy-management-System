package com.pharmacare.util;

import java.util.Collection;

/** Tiny JSON array builder used to feed Chart.js (strings are quoted/escaped, numbers are raw). */
public final class JsonUtil {
    private JsonUtil() { }

    public static String array(Collection<?> values) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object v : values) {
            if (!first) sb.append(',');
            first = false;
            if (v instanceof Number) {
                sb.append(v);
            } else {
                sb.append('"').append(escape(String.valueOf(v))).append('"');
            }
        }
        return sb.append(']').toString();
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (char ch : s.toCharArray()) {
            switch (ch) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '<':  sb.append("\\u003c"); break;
                case '>':  sb.append("\\u003e"); break;
                case '&':  sb.append("\\u0026"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': break;
                default:   sb.append(ch);
            }
        }
        return sb.toString();
    }
}
