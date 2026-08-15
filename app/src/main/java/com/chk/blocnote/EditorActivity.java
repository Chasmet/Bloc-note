package com.chk.blocnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditorActivity extends Activity {
    private static final int REQ_VOICE = 301;
    private NoteDatabase db;
    private NoteDatabase.Note note;
    private EditText title, body, tags;
    private TextView saveState, stats;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean loading = true;
    private boolean dirty = false;

    private final Runnable autosave = new Runnable() {
        @Override public void run() { saveNow(); }
    };

    @Override
    protected void onCreate(Bundle state) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        if (prefs.getBoolean("light", false)) setTheme(R.style.AppTheme_Light);
        super.onCreate(state);
        setContentView(R.layout.activity_editor);
        db = new NoteDatabase(this);
        long id = getIntent().getLongExtra("note_id", -1);
        note = db.getNote(id);
        if (note == null) { finish(); return; }
        bind();
        load();
    }

    private void bind() {
        title = findViewById(R.id.noteTitle);
        body = findViewById(R.id.noteBody);
        tags = findViewById(R.id.noteTags);
        saveState = findViewById(R.id.saveState);
        stats = findViewById(R.id.editorStats);
        findViewById(R.id.editorBack).setOnClickListener(v -> { saveNow(); finish(); });
        findViewById(R.id.editorMenu).setOnClickListener(this::showMenu);
        findViewById(R.id.toolBullet).setOnClickListener(v -> insertAtCursor("• "));
        findViewById(R.id.toolCheck).setOnClickListener(v -> insertAtCursor("☐ "));
        findViewById(R.id.toolDate).setOnClickListener(v -> insertDate());
        findViewById(R.id.toolVoice).setOnClickListener(v -> startVoice());
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!loading) markDirty();
                updateStats();
            }
            @Override public void afterTextChanged(Editable s) { }
        };
        title.addTextChangedListener(watcher);
        body.addTextChangedListener(watcher);
        tags.addTextChangedListener(watcher);
    }

    private void load() {
        loading = true;
        title.setText(note.title);
        body.setText(note.body);
        tags.setText(note.tags);
        title.setSelection(title.length());
        loading = false;
        updateStats();
        saveState.setText(note.trashed ? "Dans la corbeille" : "Sauvegardé");
    }

    private void markDirty() {
        dirty = true;
        saveState.setText("Modification…");
        handler.removeCallbacks(autosave);
        handler.postDelayed(autosave, 550);
    }

    private void saveNow() {
        handler.removeCallbacks(autosave);
        if (!dirty || note == null) return;
        note.title = title.getText().toString();
        note.body = body.getText().toString();
        note.tags = normalizeTags(tags.getText().toString());
        db.saveNote(note);
        dirty = false;
        saveState.setText("Sauvegardé automatiquement");
    }

    private String normalizeTags(String value) {
        String x = value == null ? "" : value.trim();
        x = x.replace("#", "").replace(";", ",");
        x = x.replaceAll("\\s*,\\s*", ", ").replaceAll(",+", ",");
        return x;
    }

    private void updateStats() {
        String text = body == null ? "" : body.getText().toString().trim();
        int chars = body == null ? 0 : body.length();
        int words = text.isEmpty() ? 0 : text.split("\\s+").length;
        int lines = text.isEmpty() ? 0 : text.split("\\n", -1).length;
        stats.setText(words + " mots • " + chars + " caractères • " + lines + " lignes");
    }

    private void insertAtCursor(String prefix) {
        int start = Math.max(0, body.getSelectionStart());
        int lineStart = body.getText().toString().lastIndexOf('\n', Math.max(0, start - 1)) + 1;
        body.getText().insert(lineStart, prefix);
        body.requestFocus();
    }

    private void insertDate() {
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(new Date());
        int start = Math.max(0, body.getSelectionStart());
        body.getText().insert(start, date);
        body.requestFocus();
    }

    private void startVoice() {
        try {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictez votre note");
            startActivityForResult(i, REQ_VOICE);
        } catch (Exception e) {
            toast("La dictée vocale n'est pas disponible sur cet appareil");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_VOICE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                int pos = Math.max(0, body.getSelectionStart());
                String before = pos > 0 && !Character.isWhitespace(body.getText().charAt(pos - 1)) ? " " : "";
                body.getText().insert(pos, before + result.get(0));
            }
        }
    }

    private void showMenu(View anchor) {
        saveNow();
        note = db.getNote(note.id);
        if (note == null) return;
        PopupMenu p = new PopupMenu(this, anchor);
        if (note.trashed) {
            p.getMenu().add(0, 20, 0, "Restaurer la note");
            p.getMenu().add(0, 21, 1, "Supprimer définitivement");
        } else {
            p.getMenu().add(0, 10, 0, note.favorite ? "★ Retirer des favoris" : "☆ Ajouter aux favoris");
            p.getMenu().add(0, 11, 1, note.pinned ? "Désépingler" : "Épingler en haut");
            p.getMenu().add(0, 12, 2, note.archived ? "Sortir de l'archive" : "Archiver");
            p.getMenu().add(0, 13, 3, "Déplacer vers un dossier");
            p.getMenu().add(0, 14, 4, "Dupliquer");
            p.getMenu().add(0, 15, 5, "Partager en texte");
            p.getMenu().add(0, 16, 6, "Mettre à la corbeille");
        }
        p.setOnMenuItemClickListener(this::handleMenu);
        p.show();
    }

    private boolean handleMenu(MenuItem item) {
        int id = item.getItemId();
        if (id == 10) { note.favorite = !note.favorite; db.saveNote(note); toast(note.favorite ? "Ajouté aux favoris" : "Retiré des favoris"); }
        else if (id == 11) { note.pinned = !note.pinned; db.saveNote(note); toast(note.pinned ? "Note épinglée" : "Note désépinglée"); }
        else if (id == 12) { note.archived = !note.archived; db.saveNote(note); toast(note.archived ? "Note archivée" : "Note restaurée de l'archive"); }
        else if (id == 13) chooseFolder();
        else if (id == 14) { long copy = db.duplicateNote(note.id); toast(copy > 0 ? "Copie créée" : "Erreur de duplication"); }
        else if (id == 15) share();
        else if (id == 16) {
            db.setNoteFlag(note.id, "trashed", true);
            toast("Note placée dans la corbeille");
            finish();
        } else if (id == 20) {
            db.setNoteFlag(note.id, "trashed", false);
            toast("Note restaurée");
            finish();
        } else if (id == 21) confirmDeleteForever();
        return true;
    }

    private void chooseFolder() {
        final List<NoteDatabase.Item> folders = db.getAllFolders();
        String[] names = new String[folders.size() + 1];
        names[0] = "Accueil";
        for (int i = 0; i < folders.size(); i++) names[i + 1] = folders.get(i).preview;
        new AlertDialog.Builder(this).setTitle("Déplacer vers…").setItems(names, (d, which) -> {
            note.folderId = which == 0 ? 0 : folders.get(which - 1).id;
            db.saveNote(note);
            toast("Note déplacée");
        }).show();
    }

    private void share() {
        saveNow();
        String heading = title.getText().toString().trim();
        if (heading.isEmpty()) heading = "Note";
        String text = heading + "\n\n" + body.getText().toString();
        String tagText = tags.getText().toString().trim();
        if (!tagText.isEmpty()) text += "\n\nTags : " + tagText;
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_SUBJECT, heading);
        send.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(send, "Partager la note"));
    }

    private void confirmDeleteForever() {
        new AlertDialog.Builder(this).setTitle("Suppression définitive")
                .setMessage("Cette note ne pourra plus être récupérée.")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Supprimer", (d, w) -> { db.deleteNoteForever(note.id); finish(); }).show();
    }

    @Override
    protected void onPause() {
        saveNow();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(autosave);
        if (db != null) db.close();
        super.onDestroy();
    }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}
