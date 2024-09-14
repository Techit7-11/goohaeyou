package com.ll.goohaeyou.global.standard.base.util;

public class FcmTokenUtil {

    public static String parseFcmToken(String token) {
        int startIndex = token.indexOf("\"token\":\"") + "\"token\":\"".length();
        int endIndex = token.lastIndexOf("\"");

        return token.substring(startIndex, endIndex);
    }
}
