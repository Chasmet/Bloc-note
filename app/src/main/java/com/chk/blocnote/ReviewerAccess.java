package com.chk.blocnote;

import android.content.Context;
import android.content.SharedPreferences;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Acces temporaire pour les examinateurs Google Play.
 * Le code prive n'est ni embarque en clair, ni publie dans le depot.
 * Cet acces de 72h ne cree JAMAIS de licence Google Play et ne fait aucun achat.
 */
public final class ReviewerAccess {
    public static final long DURATION_MS = 72L * 60L * 60L * 1000L;
    private static final String PREFS = "sync30_review_access";
    private static final String UNTIL = "review_until";
    private static final String CODE_SHA256 =
            "6fd472a326de8b30a25124560695343d98daea448d32059570a3850e2cf94239";

    private ReviewerAccess() { }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static byte[] hexBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    public static synchronized boolean activate(Context context, String code) {
        if (code == null || code.length() > 100 || code.length() < 30) return false;
        String normalized = code.trim().toUpperCase(Locale.ROOT);
        try {
            byte[] actual = MessageDigest.getInstance("SHA-256")
                    .digest(normalized.getBytes(StandardCharsets.US_ASCII));
            if (!MessageDigest.isEqual(actual, hexBytes(CODE_SHA256))) return false;
            long now = TrialAccess.effectiveNow(context);
            // 72 heures apres l'activation ; aucune modification de la licence Google.
            return prefs(context).edit().putLong(UNTIL, now + DURATION_MS).commit();
        } catch (NoSuchAlgorithmException ignored) {
            return false;
        }
    }

    public static long remaining(Context context) {
        long until = prefs(context).getLong(UNTIL, 0L);
        if (until == 0L) return 0L;
        return Math.max(0L, until - TrialAccess.effectiveNow(context));
    }

    public static boolean isActive(Context context) {
        return remaining(context) > 0L;
    }
}
