package com.chk.blocnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Controle facultatif des APK de test GitHub. Aucun APK n'est installe sans action de l'utilisateur.
 * En production Google Play, utiliser Google Play In-App Updates a la place.
 */
public final class TestUpdateManager {
    private static final String RELEASES_API =
            "https://api.github.com/repos/Chasmet/Bloc-note/releases?per_page=5";
    private static final String RELEASES_PAGE =
            "https://github.com/Chasmet/Bloc-note/releases";
    private static final long CHECK_INTERVAL_MS = 24L * 60L * 60L * 1000L;

    private TestUpdateManager() { }

    public static void check(Activity activity, boolean manual) {
        final SharedPreferences prefs = activity.getSharedPreferences("test_updates", Activity.MODE_PRIVATE);
        long now = System.currentTimeMillis();
        if (!manual && now - prefs.getLong("last_check", 0L) < CHECK_INTERVAL_MS) return;
        prefs.edit().putLong("last_check", now).apply();
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(RELEASES_API).openConnection();
                connection.setConnectTimeout(7000);
                connection.setReadTimeout(7000);
                connection.setRequestProperty("Accept", "application/vnd.github+json");
                connection.setRequestProperty("User-Agent", "BlocNoteUltra-TestUpdater");
                if (connection.getResponseCode() != 200) throw new IllegalStateException("GitHub indisponible");
                StringBuilder response = new StringBuilder();
                try (InputStream in = connection.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                }
                JSONArray releases = new JSONArray(response.toString());
                if (releases.length() == 0) {
                    if (manual) activity.runOnUiThread(() ->
                        Toast.makeText(activity, "Aucune version de test publiee", Toast.LENGTH_LONG).show());
                    return;
                }
                JSONObject latest = releases.getJSONObject(0);
                final String latestVersion = latest.optString("tag_name", "").replaceFirst("^v", "");
                final String releaseUrl = latest.optString("html_url", RELEASES_PAGE);
                final String installedVersion = activity.getPackageManager()
                        .getPackageInfo(activity.getPackageName(), 0).versionName;
                if (!isNewer(latestVersion, installedVersion)) {
                    if (manual) activity.runOnUiThread(() ->
                        Toast.makeText(activity, "Application a jour (" + installedVersion + ")", Toast.LENGTH_LONG).show());
                    return;
                }
                activity.runOnUiThread(() -> {
                    if (activity.isFinishing() || activity.isDestroyed()) return;
                    new AlertDialog.Builder(activity)
                            .setTitle("Mise a jour de test disponible")
                            .setMessage("Version installee : " + installedVersion + "\nNouvelle version : " +
                                    latestVersion + "\n\nLa mise a jour se telecharge sur GitHub. " +
                                    "Android demandera votre accord pour l'installation. Vos notes restent conservees " +
                                    "si la signature de l'APK est identique.")
                            .setNegativeButton("Plus tard", null)
                            .setPositiveButton("Ouvrir la version", (dialog, which) -> {
                                String safeUrl = releaseUrl.startsWith(RELEASES_PAGE + "/tag/")
                                        ? releaseUrl : RELEASES_PAGE;
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl)));
                            }).show();
                });
            } catch (Exception error) {
                if (manual) activity.runOnUiThread(() ->
                    Toast.makeText(activity, "Verification impossible : connexion necessaire", Toast.LENGTH_LONG).show());
                // Une erreur reseau ne retarde pas une nouvelle verification manuelle.
                prefs.edit().remove("last_check").apply();
            } finally {
                if (connection != null) connection.disconnect();
            }
        }, "blocnote-update-check").start();
    }

    static boolean isNewer(String remote, String local) {
        try {
            String[] a = remote.split("\\.");
            String[] b = local.split("\\.");
            for (int i = 0; i < Math.max(a.length, b.length); i++) {
                int x = i < a.length ? Integer.parseInt(a[i]) : 0;
                int y = i < b.length ? Integer.parseInt(b[i]) : 0;
                if (x != y) return x > y;
            }
        } catch (NumberFormatException ignored) { }
        return false;
    }
}
