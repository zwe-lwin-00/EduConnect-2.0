package com.educonnect.shared.logging;

import org.slf4j.Logger;

import java.util.regex.Pattern;

/**
 * Shared logging helper for traceable logs (method/line via Logback pattern) and credential redaction.
 * Use in API and Infrastructure layers to avoid logging passwords, tokens, or secrets.
 */
public final class LoggerExtensions {

    private static final Pattern CREDENTIAL_PATTERN = Pattern.compile(
            "(?i)(password|token|secret|authorization|bearer|api[_-]?key|credential)\\s*[:=]\\s*[^\\s,}\\]]+",
            Pattern.CASE_INSENSITIVE
    );
    private static final String REDACTED = "***";

    private LoggerExtensions() {}

    /**
     * Redact credential-like values from a message (e.g. "password=abc123" -> "password=***").
     */
    public static String redactCredentials(String message) {
        if (message == null) return null;
        return CREDENTIAL_PATTERN.matcher(message).replaceAll("$1=***");
    }

    public static void info(Logger log, String message) {
        if (log.isInfoEnabled()) log.info(redactCredentials(message));
    }

    public static void info(Logger log, String format, Object... args) {
        if (log.isInfoEnabled()) log.info(redactCredentials(format), redactArgs(args));
    }

    public static void warn(Logger log, String message) {
        if (log.isWarnEnabled()) log.warn(redactCredentials(message));
    }

    public static void warn(Logger log, String format, Object... args) {
        if (log.isWarnEnabled()) log.warn(redactCredentials(format), redactArgs(args));
    }

    public static void warn(Logger log, String message, Throwable t) {
        if (log.isWarnEnabled()) log.warn(redactCredentials(message), t);
    }

    public static void error(Logger log, String message) {
        if (log.isErrorEnabled()) log.error(redactCredentials(message));
    }

    public static void error(Logger log, String format, Object... args) {
        if (log.isErrorEnabled()) log.error(redactCredentials(format), redactArgs(args));
    }

    public static void error(Logger log, String message, Throwable t) {
        if (log.isErrorEnabled()) log.error(redactCredentials(message), t);
    }

    public static void debug(Logger log, String message) {
        if (log.isDebugEnabled()) log.debug(redactCredentials(message));
    }

    public static void debug(Logger log, String format, Object... args) {
        if (log.isDebugEnabled()) log.debug(redactCredentials(format), redactArgs(args));
    }

    /** Redact any argument that looks like a credential value (e.g. "password" key or raw secret). */
    private static Object[] redactArgs(Object[] args) {
        if (args == null || args.length == 0) return args;
        Object[] out = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object a = args[i];
            if (a != null && isCredentialLike(String.valueOf(a))) out[i] = REDACTED;
            else out[i] = a;
        }
        return out;
    }

    private static boolean isCredentialLike(String s) {
        String lower = s.toLowerCase();
        return lower.contains("password") || lower.contains("token") || lower.contains("secret")
                || lower.matches("^[A-Za-z0-9_-]{20,}$"); // long token-like string
    }
}
