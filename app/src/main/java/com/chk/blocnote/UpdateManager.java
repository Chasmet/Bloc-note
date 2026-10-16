package com.chk.blocnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/** Mise à jour GitHub Releases. Les données restent dans le même package Android. */
public final class UpdateManager {
    private static final String ENDPOINT =
            "https://api.github.com/repos/Chasmet/Bloc-note/releases/latest";
    private static final long INTERVAL = TimeUnit.HOURS.toMillis(24);
    private static Uri pendingInstallation;

    private UpdateManager() {}

    public static void check(Activity activity, boolean manual) {
        SharedPreferences pref = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
        long now = System.currentTimeMillis();
        if (!manual && now - pref.getLong("last_update_check", 0) < INTERVAL) return;
        pref.edit().putLong("last_update_check", now).apply();
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
                connection.setRequestProperty("Accept", "application/vnd.github+json");
                connection.setRequestProperty("User-Agent", "Sync30BlocNote-Android");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                int status = connection.getResponseCode();
                if (status == 404) {
                    if (manual) message(activity, "Aucune version publiée sur GitHub Releases.");
                    return;
                }
                if (status != 200) throw new Exception("HTTP " + status);
                StringBuilder json = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null && json.length() < 250000) {
                        json.append(line);
                    }
                }
                JSONObject release = new JSONObject(json.toString());
                String tag = release.optString("tag_name", "");
                String digits = tag.replaceFirst("^v", "");
                if (!digits.matches("[0-9]+")) {
                    if (manual) message(activity, "Version GitHub non reconnue : " + tag);
                    return;
                }
                int latestCode = Integer.parseInt(digits);
                int installedCode = activity.getPackageManager()
                        .getPackageInfo(activity.getPackageName(), 0).versionCode;
                if (latestCode <= installedCode) {
                    if (manual) message(activity, "Sync 30 Bloc-note est déjà à jour.");
                    return;
                }
                JSONArray assets = release.optJSONArray("assets");
                if (assets == null) return;
                String downloadUrl = "";
                for (int i = 0; i < assets.length(); i++) {
                    JSONObject asset = assets.getJSONObject(i);
                    if (asset.optString("name", "").endsWith(".apk")) {
                        downloadUrl = asset.optString("browser_download_url", "");
                        break;
                    }
                }
                if (!downloadUrl.startsWith(
                        "https://github.com/Chasmet/Bloc-note/releases/download/")) {
                    if (manual) message(activity, "Aucun APK officiel dans cette version.");
                    return;
                }
                final String apkUrl = downloadUrl;
                activity.runOnUiThread(() -> {
                    if (activity.isFinishing() || activity.isDestroyed()) return;
                    new AlertDialog.Builder(activity)
                            .setTitle("Mise à jour Sync 30")
                            .setMessage("Version " + tag +
                                    " disponible. Installer par-dessus la version actuelle conserve les notes et les dossiers, si la signature de l'APK est identique.")
                            .setNegativeButton("Plus tard", null)
                            .setPositiveButton("Télécharger", (d, w) ->
                                    download(activity, apkUrl, latestCode))
                            .show();
                });
            } catch (Exception ex) {
                if (manual) message(activity, "Vérification impossible : " + ex.getMessage());
            } finally {
                if (connection != null) connection.disconnect();
            }
        }, "sync30-update-check").start();
    }

    private static void download(Activity activity, String address, int code) {
        try {
            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            String fileName = "Sync-30-Bloc-note-v" + code + ".apk";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(address));
            request.setTitle("Sync 30 Bloc-note");
            request.setDescription("Version " + code + " — mise à jour");
            request.setMimeType("application/vnd.android.package-archive");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS, fileName);
            final long id = manager.enqueue(request);
            message(activity, "Téléchargement lancé. Les notes ne seront pas effacées.");
            new Thread(() -> {
                for (int attempt = 0; attempt < 300; attempt++) {
                    try {
                        Thread.sleep(1000L);
                        DownloadManager.Query q = new DownloadManager.Query().setFilterById(id);
                        try (Cursor cursor = manager.query(q)) {
                            if (!cursor.moveToFirst()) return;
                            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                Uri uri = manager.getUriForDownloadedFile(id);
                                activity.runOnUiThread(() -> install(activity, uri));
                                return;
                            }
                            if (status == DownloadManager.STATUS_FAILED) {
                                message(activity, "Téléchargement impossible. Réessaie depuis le menu.");
                                return;
                            }
                        }
                    } catch (Exception ex) {
                        return;
                    }
                }
            }, "sync30-update-download").start();
        } catch (Exception ex) {
            message(activity, "Téléchargement impossible : " + ex.getMessage());
        }
    }

    public static void resumePendingInstall(Activity activity) {
        if (pendingInstallation != null && (Build.VERSION.SDK_INT < 26 ||
                activity.getPackageManager().canRequestPackageInstalls())) {
            Uri uri = pendingInstallation;
            pendingInstallation = null;
            install(activity, uri);
        }
    }

    private static void install(Activity activity, Uri fileUri) {
        if (fileUri == null) return;
        if (Build.VERSION.SDK_INT >= 26 &&
                !activity.getPackageManager().canRequestPackageInstalls()) {
            pendingInstallation = fileUri;
            message(activity, "Autorise les installations depuis Sync 30, puis reviens.");
            Intent settings = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(settings);
            return;
        }
        try {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(fileUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(install);
        } catch (Exception ex) {
            message(activity, "Ouvre le fichier APK dans Téléchargements pour l'installer.");
        }
    }

    private static void message(Activity activity, String text) {
        activity.runOnUiThread(() -> {
            if (!activity.isFinishing() && !activity.isDestroyed())
                Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
        });
    }
}
