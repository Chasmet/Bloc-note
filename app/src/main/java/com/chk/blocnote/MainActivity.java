package com.chk.blocnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQ_EXPORT = 201;
    private static final int REQ_IMPORT = 202;

    private NoteDatabase db;
    private long currentFolder = 0;
    private String filter = "notes";
    private String query = "";
    private SearchView search;
    private GridView list;
    private TextView title, breadcrumb, stats;
    private Button filterNotes, filterFav, filterArchive, filterTrash;
    private Button btnBackFolder, btnNew, btnFolder, btnMenu;
    private List<NoteDatabase.Item> items = new ArrayList<>();
    private NotesAdapter adapter;
    private SharedPreferences prefs;
    private boolean unlockedSession = false;
    private boolean pinShowing = false;
    private boolean gridMode = false;
    private boolean compactMode = false;
    private String accentColor = Personalization.DEFAULT_COLOR;

    @Override
    protected void onCreate(Bundle state) {
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        if (prefs.getBoolean("light", false)) setTheme(R.style.AppTheme_Light);
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        db = new NoteDatabase(this);
        bind();
        loadPreferences();
        reload();
    }

    private void bind() {
        title = findViewById(R.id.title);
        breadcrumb = findViewById(R.id.breadcrumb);
        stats = findViewById(R.id.stats);
        search = findViewById(R.id.search);
        list = findViewById(R.id.list);
        filterNotes = findViewById(R.id.filterNotes);
        filterFav = findViewById(R.id.filterFav);
        filterArchive = findViewById(R.id.filterArchive);
        filterTrash = findViewById(R.id.filterTrash);
        btnBackFolder = findViewById(R.id.btnBackFolder);
        btnNew = findViewById(R.id.btnNew);
        btnFolder = findViewById(R.id.btnFolder);
        btnMenu = findViewById(R.id.btnMenu);

        adapter = new NotesAdapter(this);
        list.setAdapter(adapter);

        btnNew.setOnClickListener(v -> createNote());
        btnFolder.setOnClickListener(v -> showFolderEditor(null));
        btnMenu.setOnClickListener(this::showMainMenu);
        btnBackFolder.setOnClickListener(v -> goParent());
        filterNotes.setOnClickListener(v -> setFilter("notes"));
        filterFav.setOnClickListener(v -> setFilter("fav"));
        filterArchive.setOnClickListener(v -> setFilter("archive"));
        filterTrash.setOnClickListener(v -> setFilter("trash"));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { query = q; reload(); return true; }
            @Override public boolean onQueryTextChange(String q) { query = q; reload(); return true; }
        });

        list.setOnItemClickListener((parent, view, position, id) -> {
            NoteDatabase.Item item = items.get(position);
            if (item.folder) {
                currentFolder = item.id;
                filter = "notes";
                query = "";
                search.setQuery("", false);
                reload();
            } else {
                openEditor(item.id);
            }
        });

        list.setOnItemLongClickListener((parent, view, position, id) -> {
            showItemMenu(view, items.get(position));
            return true;
        });
    }

    private void loadPreferences() {
        gridMode = prefs.getBoolean("grid_mode", false);
        compactMode = prefs.getBoolean("compact_mode", false);
        accentColor = Personalization.normalizeHex(prefs.getString("accent_color", Personalization.DEFAULT_COLOR));
        applyDisplayMode();
        applyAccent();
    }

    private void setFilter(String value) {
        filter = value;
        reload();
    }

    private void reload() {
        if (db == null) return;
        items = db.listItems(currentFolder, filter, query);
        adapter.notifyDataSetChanged();

        if ("notes".equals(filter)) {
            if (currentFolder == 0) {
                title.setText("Mes notes");
                title.setTextColor(Personalization.parseColor(accentColor));
            } else {
                title.setText(db.getFolderEmoji(currentFolder) + " " + db.getFolderName(currentFolder));
                title.setTextColor(Personalization.parseColor(db.getFolderColor(currentFolder)));
            }
        } else {
            title.setText(filterTitle());
            title.setTextColor(Personalization.parseColor(accentColor));
        }

        breadcrumb.setText(query.trim().isEmpty() ? db.getFolderPath(currentFolder) : "Recherche : “" + query + "”");
        int total = db.countActiveNotes();
        int fav = db.countFavorites();
        int trash = db.countTrash();
        stats.setText(total + " notes • " + fav + " favorites • " + trash + " dans la corbeille");
        btnBackFolder.setEnabled(currentFolder != 0 && "notes".equals(filter));
        filterNotes.setAlpha("notes".equals(filter) ? 1f : .52f);
        filterFav.setAlpha("fav".equals(filter) ? 1f : .52f);
        filterArchive.setAlpha("archive".equals(filter) ? 1f : .52f);
        filterTrash.setAlpha("trash".equals(filter) ? 1f : .52f);
        applyAccent();
    }

    private String filterTitle() {
        if ("fav".equals(filter)) return "★ Favoris";
        if ("archive".equals(filter)) return "📦 Archive";
        if ("trash".equals(filter)) return "🗑 Corbeille";
        return "Bloc Note Ultra";
    }

    private void createNote() {
        long id = db.createNote(currentFolder);
        if (id > 0) openEditor(id);
    }

    private void showFolderEditor(NoteDatabase.Item existing) {
        final String[] selectedColor = {existing == null ? accentColor : existing.color};
        final String[] selectedEmoji = {existing == null ? Personalization.DEFAULT_FOLDER_EMOJI : existing.emoji};

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = Personalization.dp(this, 18);
        box.setPadding(pad, Personalization.dp(this, 8), pad, Personalization.dp(this, 4));

        EditText name = new EditText(this);
        name.setSingleLine(true);
        name.setHint("Nom du dossier");
        name.setText(existing == null ? "" : existing.title);
        if (existing != null) name.setSelection(name.length());
        box.addView(name, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Personalization.dp(this, 54)));

        TextView preview = new TextView(this);
        preview.setGravity(Gravity.CENTER_VERTICAL);
        preview.setTextSize(18f);
        preview.setPadding(Personalization.dp(this, 14), Personalization.dp(this, 10), Personalization.dp(this, 14), Personalization.dp(this, 10));
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Personalization.dp(this, 62));
        pp.topMargin = Personalization.dp(this, 10);
        box.addView(preview, pp);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        Button emoji = new Button(this);
        Button color = new Button(this);
        emoji.setText("😀 EMOJI");
        color.setText("🎨 COULEUR");
        actions.addView(emoji, new LinearLayout.LayoutParams(0, Personalization.dp(this, 52), 1f));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, Personalization.dp(this, 52), 1f);
        cp.leftMargin = Personalization.dp(this, 8);
        actions.addView(color, cp);
        box.addView(actions);

        final Runnable refreshPreview = () -> {
            String label = name.getText().toString().trim();
            if (label.isEmpty()) label = "Nouveau dossier";
            preview.setText(selectedEmoji[0] + "   " + label);
            preview.setBackground(Personalization.card(this, selectedColor[0], prefs.getBoolean("light", false)));
        };
        refreshPreview.run();

        name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { refreshPreview.run(); }
            @Override public void afterTextChanged(Editable s) { }
        });

        emoji.setOnClickListener(v -> Personalization.showEmojiPicker(this, selectedEmoji[0], value -> {
            selectedEmoji[0] = value;
            refreshPreview.run();
        }));
        color.setOnClickListener(v -> Personalization.showColorPicker(this, selectedColor[0], value -> {
            selectedColor[0] = value;
            refreshPreview.run();
        }));

        new AlertDialog.Builder(this)
                .setTitle(existing == null ? "Créer un dossier personnalisé" : "Personnaliser le dossier")
                .setMessage("Choisis son nom, sa couleur et son emoji.")
                .setView(box)
                .setNegativeButton("Annuler", null)
                .setPositiveButton(existing == null ? "Créer" : "Enregistrer", (d, w) -> {
                    if (existing == null) db.createFolder(name.getText().toString(), currentFolder, selectedColor[0], selectedEmoji[0]);
                    else db.updateFolderStyle(existing.id, name.getText().toString(), selectedColor[0], selectedEmoji[0]);
                    reload();
                }).show();
    }

    private void openEditor(long noteId) {
        Intent i = new Intent(this, EditorActivity.class);
        i.putExtra("note_id", noteId);
        startActivity(i);
    }

    private void goParent() {
        if (currentFolder != 0) {
            currentFolder = db.getParentFolder(currentFolder);
            reload();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentFolder != 0 && "notes".equals(filter)) goParent();
        else if (!"notes".equals(filter) || !query.isEmpty()) {
            filter = "notes";
            query = "";
            search.setQuery("", false);
            reload();
        } else super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (db != null) reload();
        maybeAskPin();
    }

    private void showItemMenu(View anchor, NoteDatabase.Item item) {
        PopupMenu p = new PopupMenu(this, anchor);
        if (item.folder) {
            p.getMenu().add(0, 1, 0, "🎨 Personnaliser : nom, couleur, emoji");
            p.getMenu().add(0, 2, 1, "Supprimer si vide");
        } else if (item.trashed) {
            p.getMenu().add(0, 10, 0, "Restaurer");
            p.getMenu().add(0, 11, 1, "Supprimer définitivement");
        } else {
            p.getMenu().add(0, 9, 0, "🎨 Personnaliser couleur et emoji");
            p.getMenu().add(0, 3, 1, item.favorite ? "Retirer des favoris" : "Ajouter aux favoris");
            p.getMenu().add(0, 4, 2, item.pinned ? "Désépingler" : "Épingler en haut");
            p.getMenu().add(0, 5, 3, item.archived ? "Sortir de l'archive" : "Archiver");
            p.getMenu().add(0, 6, 4, "Dupliquer");
            p.getMenu().add(0, 7, 5, "Déplacer vers un dossier");
            p.getMenu().add(0, 8, 6, "Mettre à la corbeille");
        }
        p.setOnMenuItemClickListener(m -> handleItemMenu(item, m));
        p.show();
    }

    private boolean handleItemMenu(NoteDatabase.Item item, MenuItem m) {
        int id = m.getItemId();
        if (item.folder) {
            if (id == 1) showFolderEditor(item);
            if (id == 2) {
                boolean ok = db.deleteFolderIfEmpty(item.id);
                toast(ok ? "Dossier supprimé" : "Dossier non vide : suppression bloquée");
                reload();
            }
            return true;
        }
        if (id == 10) db.setNoteFlag(item.id, "trashed", false);
        else if (id == 11) confirmDeleteForever(item.id);
        else if (id == 9) { showNoteStyleDialog(item.id); return true; }
        else if (id == 3) db.setNoteFlag(item.id, "favorite", !item.favorite);
        else if (id == 4) db.setNoteFlag(item.id, "pinned", !item.pinned);
        else if (id == 5) db.setNoteFlag(item.id, "archived", !item.archived);
        else if (id == 6) { db.duplicateNote(item.id); toast("Note dupliquée"); }
        else if (id == 7) chooseFolder(item.id);
        else if (id == 8) db.setNoteFlag(item.id, "trashed", true);
        reload();
        return true;
    }

    private void showNoteStyleDialog(long noteId) {
        NoteDatabase.Note note = db.getNote(noteId);
        if (note == null) return;
        final String[] selectedColor = {note.color};
        final String[] selectedEmoji = {note.emoji};

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = Personalization.dp(this, 18);
        box.setPadding(pad, Personalization.dp(this, 8), pad, Personalization.dp(this, 4));

        TextView preview = new TextView(this);
        preview.setTextSize(18f);
        preview.setGravity(Gravity.CENTER_VERTICAL);
        preview.setPadding(Personalization.dp(this, 14), Personalization.dp(this, 10), Personalization.dp(this, 14), Personalization.dp(this, 10));
        box.addView(preview, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Personalization.dp(this, 70)));

        LinearLayout actions = new LinearLayout(this);
        Button emoji = new Button(this);
        Button color = new Button(this);
        emoji.setText("😀 EMOJI");
        color.setText("🎨 COULEUR");
        actions.addView(emoji, new LinearLayout.LayoutParams(0, Personalization.dp(this, 54), 1f));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, Personalization.dp(this, 54), 1f);
        cp.leftMargin = Personalization.dp(this, 8);
        actions.addView(color, cp);
        box.addView(actions);

        final Runnable refresh = () -> {
            String label = note.title == null || note.title.trim().isEmpty() ? "Sans titre" : note.title;
            preview.setText(selectedEmoji[0] + "   " + label);
            preview.setBackground(Personalization.card(this, selectedColor[0], prefs.getBoolean("light", false)));
        };
        refresh.run();

        emoji.setOnClickListener(v -> Personalization.showEmojiPicker(this, selectedEmoji[0], value -> {
            selectedEmoji[0] = value;
            refresh.run();
        }));
        color.setOnClickListener(v -> Personalization.showColorPicker(this, selectedColor[0], value -> {
            selectedColor[0] = value;
            refresh.run();
        }));

        new AlertDialog.Builder(this)
                .setTitle("Personnaliser la note")
                .setView(box)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    db.setNoteStyle(noteId, selectedColor[0], selectedEmoji[0]);
                    reload();
                }).show();
    }

    private void confirmDeleteForever(long noteId) {
        new AlertDialog.Builder(this).setTitle("Supprimer définitivement ?")
                .setMessage("Cette action est irréversible.")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Supprimer", (d, w) -> { db.deleteNoteForever(noteId); reload(); }).show();
    }

    private void chooseFolder(long noteId) {
        List<NoteDatabase.Item> folders = db.getAllFolders();
        String[] names = new String[folders.size() + 1];
        names[0] = "🏠 Accueil";
        for (int i = 0; i < folders.size(); i++) names[i + 1] = folders.get(i).preview;
        new AlertDialog.Builder(this).setTitle("Déplacer vers…").setItems(names, (d, which) -> {
            long folderId = which == 0 ? 0 : folders.get(which - 1).id;
            db.moveNote(noteId, folderId);
            toast("Note déplacée");
            reload();
        }).show();
    }

    private void showMainMenu(View anchor) {
        PopupMenu p = new PopupMenu(this, anchor);
        p.getMenu().add(0, 106, 0, gridMode ? "☷ Passer en liste" : "▦ Passer en grille");
        p.getMenu().add(0, 107, 1, compactMode ? "↕ Mode confortable" : "↕ Mode compact");
        p.getMenu().add(0, 108, 2, "🎨 Couleur principale de l'app");
        p.getMenu().add(0, 109, 3, "Aa Taille du texte");
        p.getMenu().add(0, 103, 4, prefs.getBoolean("light", false) ? "🌙 Passer en mode sombre" : "☀️ Passer en mode clair");
        p.getMenu().add(0, 100, 5, "⬇ Exporter sauvegarde JSON");
        p.getMenu().add(0, 101, 6, "⬆ Importer sauvegarde JSON");
        p.getMenu().add(0, 102, 7, prefs.getString("pin_hash", "").isEmpty() ? "🔒 Activer verrouillage PIN" : "🔐 Modifier / désactiver le PIN");
        p.getMenu().add(0, 104, 8, "🗑 Vider définitivement la corbeille");
        p.getMenu().add(0, 105, 9, "À propos");
        p.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == 100) exportBackup();
            else if (id == 101) importBackup();
            else if (id == 102) pinSettings();
            else if (id == 103) toggleTheme();
            else if (id == 104) emptyTrash();
            else if (id == 106) toggleGridMode();
            else if (id == 107) toggleCompactMode();
            else if (id == 108) chooseAppAccent();
            else if (id == 109) chooseTextSize();
            else if (id == 105) new AlertDialog.Builder(this).setTitle("Bloc Note Ultra V2")
                    .setMessage("Bloc-notes Android natif ultra personnalisable.\n\n• couleurs par dossier et note\n• emojis personnalisés\n• mode liste / grille\n• mode compact\n• couleur principale de l'app\n• taille du texte\n• SQLite local\n• autosauvegarde\n• favoris, archive, corbeille, tags et dictée\n• export/import JSON\n\nLes données restent sur l'appareil sauf partage ou export volontaire.")
                    .setPositiveButton("OK", null).show();
            return true;
        });
        p.show();
    }

    private void toggleGridMode() {
        gridMode = !gridMode;
        prefs.edit().putBoolean("grid_mode", gridMode).apply();
        applyDisplayMode();
        adapter.notifyDataSetChanged();
        toast(gridMode ? "Mode grille activé" : "Mode liste activé");
    }

    private void toggleCompactMode() {
        compactMode = !compactMode;
        prefs.edit().putBoolean("compact_mode", compactMode).apply();
        adapter.notifyDataSetChanged();
        toast(compactMode ? "Mode compact activé" : "Mode confortable activé");
    }

    private void applyDisplayMode() {
        if (list == null) return;
        list.setNumColumns(gridMode ? 2 : 1);
        list.setHorizontalSpacing(Personalization.dp(this, gridMode ? 8 : 0));
        list.setVerticalSpacing(Personalization.dp(this, 8));
    }

    private void chooseAppAccent() {
        Personalization.showColorPicker(this, accentColor, value -> {
            accentColor = value;
            prefs.edit().putString("accent_color", value).apply();
            applyAccent();
            reload();
        });
    }

    private void applyAccent() {
        if (btnNew == null) return;
        int color = Personalization.parseColor(accentColor);
        ColorStateList tint = ColorStateList.valueOf(color);
        int textColor = readableText(color);
        btnNew.setBackgroundTintList(tint);
        btnNew.setTextColor(textColor);
        btnFolder.setBackgroundTintList(tint);
        btnFolder.setTextColor(textColor);
        filterNotes.setBackgroundTintList(tint);
        filterFav.setBackgroundTintList(tint);
        filterArchive.setBackgroundTintList(tint);
        filterTrash.setBackgroundTintList(tint);
        filterNotes.setTextColor(textColor);
        filterFav.setTextColor(textColor);
        filterArchive.setTextColor(textColor);
        filterTrash.setTextColor(textColor);
    }

    private int readableText(int color) {
        int y = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        return y > 175 ? Color.BLACK : Color.WHITE;
    }

    private void chooseTextSize() {
        String[] choices = {"Petit", "Normal", "Grand", "Très grand"};
        int current = prefs.getInt("text_size", 1);
        new AlertDialog.Builder(this).setTitle("Taille du texte")
                .setSingleChoiceItems(choices, current, (d, which) -> {
                    prefs.edit().putInt("text_size", which).apply();
                    d.dismiss();
                    adapter.notifyDataSetChanged();
                    toast("Taille du texte : " + choices[which]);
                }).show();
    }

    private float textScale() {
        int value = prefs.getInt("text_size", 1);
        if (value == 0) return .90f;
        if (value == 2) return 1.15f;
        if (value == 3) return 1.30f;
        return 1f;
    }

    private void exportBackup() {
        Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("application/json");
        i.putExtra(Intent.EXTRA_TITLE, "bloc-note-ultra-v2-backup.json");
        startActivityForResult(i, REQ_EXPORT);
    }

    private void importBackup() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("application/json");
        startActivityForResult(i, REQ_IMPORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        if (requestCode == REQ_EXPORT) {
            try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                if (out == null) throw new Exception("Impossible d'ouvrir le fichier");
                out.write(db.exportJson().toString(2).getBytes(StandardCharsets.UTF_8));
                toast("Sauvegarde exportée");
            } catch (Exception e) { toast("Échec export : " + e.getMessage()); }
        } else if (requestCode == REQ_IMPORT) {
            try {
                String json = readAll(getContentResolver().openInputStream(uri));
                JSONObject root = new JSONObject(json);
                new AlertDialog.Builder(this).setTitle("Remplacer les données ?")
                        .setMessage("L'import remplacera les notes actuellement stockées dans l'application.")
                        .setNegativeButton("Annuler", null)
                        .setPositiveButton("Importer", (d, w) -> {
                            try { db.importJson(root); currentFolder = 0; reload(); toast("Sauvegarde importée"); }
                            catch (Exception e) { toast("Import impossible : " + e.getMessage()); }
                        }).show();
            } catch (Exception e) { toast("Fichier invalide : " + e.getMessage()); }
        }
    }

    private String readAll(InputStream in) throws Exception {
        if (in == null) throw new Exception("Fichier inaccessible");
        StringBuilder b = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) b.append(line).append('\n');
        }
        return b.toString();
    }

    private void toggleTheme() {
        prefs.edit().putBoolean("light", !prefs.getBoolean("light", false)).apply();
        recreate();
    }

    private void emptyTrash() {
        new AlertDialog.Builder(this).setTitle("Vider la corbeille ?")
                .setMessage("Toutes les notes de la corbeille seront supprimées définitivement.")
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Vider", (d, w) -> { int n = db.emptyTrash(); toast(n + " note(s) supprimée(s)"); reload(); }).show();
    }

    private void pinSettings() {
        String hash = prefs.getString("pin_hash", "");
        if (hash.isEmpty()) showSetPinDialog();
        else new AlertDialog.Builder(this).setTitle("Verrouillage PIN")
                .setItems(new String[]{"Changer le PIN", "Désactiver le verrouillage"}, (d, which) -> {
                    if (which == 0) showSetPinDialog();
                    else {
                        prefs.edit().remove("pin_hash").apply();
                        unlockedSession = true;
                        toast("Verrouillage désactivé");
                    }
                }).show();
    }

    private void showSetPinDialog() {
        EditText input = pinInput();
        new AlertDialog.Builder(this).setTitle("Définir un PIN")
                .setMessage("Choisissez 4 à 8 chiffres. Le PIN est stocké sous forme d'empreinte locale.")
                .setView(input)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (d, w) -> {
                    String pin = input.getText().toString();
                    if (pin.matches("\\d{4,8}")) {
                        prefs.edit().putString("pin_hash", hash(pin)).apply();
                        unlockedSession = true;
                        toast("Verrouillage PIN activé");
                    } else toast("PIN invalide : 4 à 8 chiffres");
                }).show();
    }

    private void maybeAskPin() {
        final String expected = prefs.getString("pin_hash", "");
        if (expected.isEmpty() || unlockedSession || pinShowing) return;
        pinShowing = true;
        final EditText input = pinInput();
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Bloc Note Ultra verrouillé")
                .setMessage("Entrez votre PIN pour ouvrir vos notes.")
                .setView(input)
                .setCancelable(false)
                .setNegativeButton("Quitter", (d, w) -> finish())
                .setPositiveButton("Déverrouiller", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (expected.equals(hash(input.getText().toString()))) {
                unlockedSession = true;
                pinShowing = false;
                dialog.dismiss();
            } else {
                input.setError("PIN incorrect");
                input.setText("");
            }
        }));
        dialog.setOnDismissListener(d -> pinShowing = false);
        dialog.show();
    }

    private EditText pinInput() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setHint("PIN");
        return input;
    }

    private static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder b = new StringBuilder();
            for (byte x : bytes) b.append(String.format(Locale.US, "%02x", x));
            return b.toString();
        } catch (Exception e) { return text; }
    }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }

    @Override
    protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }

    private class NotesAdapter extends BaseAdapter {
        private final LayoutInflater inflater;
        private final DateFormat date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        NotesAdapter(Context c) { inflater = LayoutInflater.from(c); }
        @Override public int getCount() { return items.size(); }
        @Override public Object getItem(int position) { return items.get(position); }
        @Override public long getItemId(int position) { return items.get(position).id; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String mode = gridMode ? "grid" : "list";
            if (convertView == null || !mode.equals(convertView.getTag())) {
                convertView = inflater.inflate(gridMode ? R.layout.row_item_grid : R.layout.row_item, parent, false);
                convertView.setTag(mode);
            }

            NoteDatabase.Item x = items.get(position);
            View root = convertView.findViewById(R.id.rowRoot);
            TextView icon = convertView.findViewById(R.id.icon);
            TextView t = convertView.findViewById(R.id.itemTitle);
            TextView preview = convertView.findViewById(R.id.itemPreview);
            TextView meta = convertView.findViewById(R.id.itemMeta);

            boolean light = prefs.getBoolean("light", false);
            root.setBackground(Personalization.card(MainActivity.this, x.color, light));
            icon.setBackground(Personalization.iconBubble(MainActivity.this, x.color, light));
            icon.setText(x.emoji == null || x.emoji.trim().isEmpty() ? (x.folder ? "📁" : "📝") : x.emoji);

            float scale = textScale();
            t.setTextSize((gridMode ? 17f : 18f) * scale);
            preview.setTextSize((gridMode ? 12f : 13f) * scale);
            meta.setTextSize((gridMode ? 10f : 11f) * scale);

            if (compactMode) {
                preview.setVisibility(View.GONE);
                root.setMinimumHeight(Personalization.dp(MainActivity.this, gridMode ? 118 : 66));
            } else {
                preview.setVisibility(View.VISIBLE);
                root.setMinimumHeight(Personalization.dp(MainActivity.this, gridMode ? 164 : 86));
            }

            if (x.folder) {
                t.setText(x.title);
                preview.setText(x.preview);
                meta.setText(gridMode ? "Ouvrir ›" : "›");
            } else {
                String state = x.trashed ? "🗑 " : x.pinned ? "📌 " : x.favorite ? "★ " : "";
                t.setText(state + x.title);
                String p = x.preview;
                if (!x.tags.trim().isEmpty()) p = "#" + x.tags.replace(",", "  #") + "\n" + p;
                preview.setText(p);
                meta.setText(date.format(new Date(x.updatedAt)));
            }
            return convertView;
        }
    }
}
