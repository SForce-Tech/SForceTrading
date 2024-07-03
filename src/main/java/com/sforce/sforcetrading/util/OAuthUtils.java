package com.sforce.sforcetrading.util;

import java.util.UUID;

public class OAuthUtils {

    public static String generateNonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static long generateTimestamp() {
        return System.currentTimeMillis() / 1000;
    }
}
