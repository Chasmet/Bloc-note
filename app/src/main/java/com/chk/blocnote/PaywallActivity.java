package com.chk.blocnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.InputType;
import android.widget.EditText;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** L'utilisateur peut toujours exporter ses propres notes meme apres l'essai. */
public final class PaywallActivity extends Activity implements PlayBillingController.Listener {
    private static final int EXPORT_JSON = 620;
    private PlayBillingController controller;
    private TextView description, billingState;
    private Button buy;
    private boolean preview;
    private String price = "1,19 €";

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        preview = getIntent().getBooleanExtra("preview", false);
        TrialAccess.startIfNeeded(this);
        buildUi();
        controller = new PlayBillingController(this, this);
        controller.connect();
    }

    private TextView text(String content, int sp, int color) {
        TextView v = new TextView(this);
        v.setText(content);
        v.setTextSize(sp);
        v.setTextColor(color);
        v.setGravity(Gravity.CENTER_HORIZONTAL);
        v.setPadding(16, 15, 16, 15);
        return v;
    }
    private Button button(LinearLayout stack, String label) {
        Button b = new Button(this);
        b.setText(label); b.setAllCaps(false);
        b.setTextSize(16);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 62);
        lp.setMargins(0, 9, 0, 9);
        stack.addView(b, lp);
        return b;
    }

    private void buildUi() {
        ScrollView scroller = new ScrollView(this);
        scroller.setFillViewport(true);
        scroller.setBackgroundColor(Color.rgb(23, 13, 9));
        LinearLayout stack = new LinearLayout(this);
        stack.setOrientation(LinearLayout.VERTICAL);
        stack.setGravity(Gravity.CENTER);
        int pad = (int) (24 * getResources().getDisplayMetrics().density);
        stack.setPadding(pad, pad, pad, pad);
        scroller.addView(stack);
        stack.addView(text("Mes Notes & Dossiers", 27, Color.rgb(255, 196, 116)));
        stack.addView(text("24 heures gratuites, puis accès à vie", 21, Color.WHITE));
        description = text("", 17, Color.rgb(234, 203, 179));
        stack.addView(description);
        stack.addView(text("Vos notes et dossiers restent enregistrés. Aucun abonnement, aucun renouvellement automatique.", 15, Color.WHITE));
        buy = button(stack, "Acheter l'accès définitif");
        buy.setEnabled(false);
        buy.setOnClickListener(v -> controller.buy());
        Button restore = button(stack, "Restaurer mon achat Google Play");
        restore.setOnClickListener(v -> controller.restorePurchases());
        Button review = button(stack, "Accès examinateur Google Play");
        review.setOnClickListener(v -> requestReviewerCode());
        Button export = button(stack, "Exporter gratuitement toutes mes notes (JSON)");
        export.setOnClickListener(v -> openExport());
        if (preview) {
            Button back = button(stack, "Retour aux notes");
            back.setOnClickListener(v -> finish());
        }
        Button privacy = button(stack, "Politique de confidentialité");
        privacy.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://sync30.onrender.com/politique-confidentialite-blocnote.html"))));
        billingState = text("Connexion Google Play…", 13, Color.rgb(206, 167, 130));
        stack.addView(billingState);
        stack.addView(text("Montant définitif et moyen de paiement affichés par Google Play avant confirmation.", 12, Color.rgb(171, 141, 120)));
        setContentView(scroller);
        updateStatus();
    }

    private void updateStatus() {
        description.setText(TrialAccess.status(this));
        buy.setText("Acheter définitivement — " + price);
    }
    @Override protected void onResume() {
        super.onResume();
        updateStatus();
        if (controller != null && !preview && TrialAccess.purchased(this)) finish();
        else if (controller != null) controller.restorePurchases();
    }
    @Override public void onBillingStatus(String message, boolean ready, String formattedPrice) {
        price = formattedPrice;
        if (buy != null) {
            buy.setEnabled(ready && !TrialAccess.purchased(this));
            buy.setText("Acheter définitivement — " + formattedPrice);
        }
        if (billingState != null) billingState.setText(message);
        updateStatus();
    }
    @Override public void onAccessRestored() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Accès définitif Google Play retrouvé", Toast.LENGTH_LONG).show();
            if (!preview) finish();
            else updateStatus();
        });
    }

    private void requestReviewerCode() {
        final EditText field = new EditText(this);
        field.setSingleLine(true);
        field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        field.setHint("Code privé d'accès examinateur");
        new AlertDialog.Builder(this)
            .setTitle("Accès d'évaluation Google Play")
            .setMessage("Réservé aux examinateurs disposant du code fourni dans Play Console. Accès temporaire de 72 heures, sans achat.")
            .setView(field)
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Valider", (dialog, which) -> {
                boolean valid = ReviewerAccess.activate(this, field.getText().toString());
                field.getText().clear();
                if (valid) {
                    Toast.makeText(this, "Accès examinateur activé pour 72 heures", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Code examinateur invalide", Toast.LENGTH_LONG).show();
                }
            }).show();
    }

    private void openExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "sync30-blocnote-sauvegarde.json");
        startActivityForResult(intent, EXPORT_JSON);
    }
    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request != EXPORT_JSON || result != RESULT_OK || data == null || data.getData() == null) return;
        final Uri uri = data.getData();
        new Thread(() -> {
            try (NoteDatabase db = new NoteDatabase(getApplicationContext());
                 OutputStream out = getContentResolver().openOutputStream(uri, "w")) {
                if (out == null) throw new IllegalStateException("Accès au fichier impossible");
                out.write(db.exportJson().toString(2).getBytes(StandardCharsets.UTF_8));
                out.flush();
                runOnUiThread(() -> Toast.makeText(this, "Sauvegarde JSON exportée", Toast.LENGTH_LONG).show());
            } catch (Exception error) {
                runOnUiThread(() -> new AlertDialog.Builder(this)
                    .setTitle("Export impossible").setMessage(error.getMessage())
                    .setPositiveButton("OK", null).show());
            }
        }).start();
    }
    @Override public void onBackPressed() {
        if (preview || TrialAccess.hasAccess(this)) super.onBackPressed();
        else moveTaskToBack(true); // Les notes ne sont jamais effacées.
    }
    @Override protected void onDestroy() {
        if (controller != null) controller.close();
        super.onDestroy();
    }
}
