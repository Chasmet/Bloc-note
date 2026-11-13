package com.chk.blocnote;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Consultation des notes existantes sans écriture SQLite.
 * La lecture reste disponible après l'essai, même si Billing et CHK Paiements sont hors ligne.
 * Les modifications restent protégées par le contrôle commercial de MainActivity/EditorActivity.
 */
public final class ReadOnlyNoteActivity extends Activity {
    private static final int BACKGROUND = Color.rgb(23, 13, 9);
    private static final int TEXT = Color.rgb(245, 231, 216);
    private static final int SUBTEXT = Color.rgb(211, 176, 147);

    private TextView label(String text, float size, int color) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTextIsSelectable(true);
        view.setPadding(0, 10, 0, 10);
        return view;
    }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        long noteId = getIntent().getLongExtra("note_id", -1L);
        NoteDatabase.Note note;
        try (NoteDatabase db = new NoteDatabase(this)) {
            note = noteId > 0 ? db.getNote(noteId) : null;
        }
        if (note == null) {
            finish();
            return;
        }

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BACKGROUND);
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        column.setPadding(pad, pad, pad, pad);
        scroll.addView(column);

        TextView notice = label("Lecture seule — vos notes restent accessibles gratuitement.", 14f, SUBTEXT);
        column.addView(notice);
        String title = note.title == null || note.title.trim().isEmpty() ? "Sans titre" : note.title;
        column.addView(label(title, 25f, TEXT));
        if (note.tags != null && !note.tags.trim().isEmpty()) {
            column.addView(label("Tags : " + note.tags, 14f, SUBTEXT));
        }
        TextView body = label(note.body == null ? "" : note.body, 17f, TEXT);
        body.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        column.addView(body, bodyParams);

        Button premium = new Button(this);
        premium.setAllCaps(false);
        premium.setText("Débloquer la création et la modification");
        premium.setOnClickListener(v -> startActivity(
                new Intent(this, PaywallActivity.class).putExtra("preview", true)));
        column.addView(premium, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button back = new Button(this);
        back.setAllCaps(false);
        back.setText("Retour à mes dossiers");
        back.setOnClickListener(v -> finish());
        column.addView(back, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(scroll);
    }
}
