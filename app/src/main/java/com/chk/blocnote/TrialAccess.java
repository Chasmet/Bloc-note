package com.chk.blocnote;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

/**
 * Essai local de 24 heures. Le paiement est toujours volontaire et non reconduit.
 * Le chronometre local ne prouve pas l'eligibilite globale d'un compte Google :
 * avant la production, synchroniser la date du premier essai via un backend.
 */
public final class TrialAccess {
    public static final long DURATION_MS = 24L * 60L * 60L * 1000L;
    private static final String FILE = "blocnote_access";
    private TrialAccess() { }
    private static SharedPreferences p(Context c) {
        return c.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
    public static synchronized void startIfNeeded(Context c) {
        SharedPreferences p = p(c);
        if (p.contains("trial_start")) return;
        long now = System.currentTimeMillis();
        p.edit().putLong("trial_start", now)
                .putLong("last_wall", now)
                .putLong("last_elapsed", SystemClock.elapsedRealtime()).commit();
    }
    static synchronized long effectiveNow(Context c) {
        startIfNeeded(c);
        SharedPreferences prefs = p(c);
        long wall = System.currentTimeMillis();
        long lastWall = prefs.getLong("last_wall", wall);
        long elapsed = SystemClock.elapsedRealtime();
        long lastElapsed = prefs.getLong("last_elapsed", elapsed);
        long result = Math.max(lastWall, wall);
        // Le temps ecoule monotone empêche de reculer l'horloge sans redemarrer.
        if (elapsed >= lastElapsed) result = Math.max(result, lastWall + elapsed - lastElapsed);
        prefs.edit().putLong("last_wall", result).putLong("last_elapsed", elapsed).commit();
        return result;
    }
    public static long remaining(Context c) {
        long start = p(c).getLong("trial_start", 0L);
        if (start <= 0) { startIfNeeded(c); start = p(c).getLong("trial_start", 0L); }
        return Math.max(0L, DURATION_MS - Math.max(0L, effectiveNow(c) - start));
    }
    public static boolean purchased(Context c) { return p(c).getBoolean("premium", false); }
    // Seul le controleur Billing doit appeler cette methode apres resultat Play PURCHASED.
    static void updatePurchase(Context c, boolean valid) {
        p(c).edit().putBoolean("premium", valid).commit();
    }
    public static boolean hasAccess(Context c) { return purchased(c) || remaining(c) > 0L; }
    public static String status(Context c) {
        if (purchased(c)) return "Licence définitive active";
        long remaining = remaining(c);
        if (remaining == 0) return "Essai gratuit terminé";
        long minutes = (remaining + 59999L) / 60000L;
        long hours = minutes / 60L;
        if (hours > 0L) return "Essai gratuit : " + hours + " h " + (minutes % 60L) + " min restantes";
        return "Essai gratuit : " + minutes + " min restantes";
    }
}
