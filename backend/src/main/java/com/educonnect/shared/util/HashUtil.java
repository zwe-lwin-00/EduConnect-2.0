package com.educonnect.shared.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Shared hashing for refresh tokens (store hash in DB, not raw token). */
public final class HashUtil {

    private static final String SHA_256 = "SHA-256";

    private HashUtil() {}

    public static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA_256);
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(SHA_256 + " not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
