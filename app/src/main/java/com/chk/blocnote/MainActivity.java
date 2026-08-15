package com.chk.blocnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
    private ListView list;
    private TextView title, breadcrumb, stats;
    private Button filterNotes, filterFav, filterArchive, filterTrash, btnBackFolder;
    private List<NoteDatabase.Item> items = new ArrayList<>();
    private NotesAdapter adapter;
    private SharedPreferences prefs;
    private boolean unlockedSession = false;
    private boolean pinShowing = false;

    @Override
    protected void onCreate(Bundle state) {
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        if (prefs.getBoolean("light", false)) setTheme(R.style.AppTheme_Light);
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        db = new NoteDatabase(this);
        bind();
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

        adapter = new NotesAdapter(this);
        list.setAdapter(adapter);

        findViewById(R.id.btnNew).setOnClickListener(v -> createNote());
        findViewById(R.id.btnFolder).setOnClickListener(v -> createFolder());
        findViewById(R.id.btnMenu).setOnClickListener(this::showMainMenu);
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

    private void setFilter(String value) {
        filter = value;
        reload();
    }

    private void reload() {
        if (db == null) return;
        items = db.listItems(currentFolder, filter, query);
        adapter.notifyDataSetChanged();
        title.setText("notes".equals(filter) ? db.getFolderName(currentFolder) : filterTitle());
        breadcrumb.setText(query.trim().isEmpty() ? db.getFolderPath(currentFolder) : "Recherche : “" + query + "”");
        int total = db.countActiveNotes();
        int fav = db.countFavorites();
        int trash = db.countTrash();
        stats.setText(total + " notes • " + fav + " favorites • " + trash + " dans la corbeille");
        btnBackFolder.setEnabled(currentFolder != 0 && "notes".equals(filter));
        filterNotes.setAlpha("notes".equals(filter) ? 1f : .55f);
        filterFav.setAlpha("fav".equals(filter) ? 1f : .55f);
        filterArchive.setAlpha("archive".equals(filter) ? 1f : .55f);
        filterTrash.setAlpha("trash".equals(filter) ? 1f : .55f);
    }

    private String filterTitle() {
        if ("fav".equals(filter)) return "Favoris";
        if ("archive".equals(filter)) return "Archive";
        if ("trash".equals(filter)) return "Corbeille";
        return "Bloc Note Ultra";
    }

    private void createNote() {
        long id = db.createNote(currentFolder);
        if (id > 0) openEditor(id);
    }

    private void createFolder() {
        final EditText input = new EditText(this);
        input.setHint("Nom du dossier");
        input.setSingleLine(true);
        new AlertDialog.Builder(this)
                .setTitle("Nouveau dossier")
                .setView(input)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Créer", (d, w) -> {
                    db.createFolder(input.getText().toString(), currentFolder);
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
            p.getMenu().add(0, 1, 0, "Renommer");
            p.getMenu().add(0, 2, 1, "Supprimer si vide");
        } else if (item.trashed) {
            p.getMenu().add(0, 10, 0, "Restaurer");
            p.getMenu().add(0, 11, 1, "Supprimer définitivement");
        } else {
            p.getMenu().add(0, 3, 0, item.favorite ? "Retirer des favoris" : "Ajouter aux favoris");
            p.getMenu().add(0, 4, 1, item.pinned ? "Désépingler" : "Épingler en haut");
            p.getMenu().add(0, 5, 2, item.archived ? "Sortir de l'archive" : "Archiver");
            p.getMenu().add(0, 6, 3, "Dupliquer");
            p.getMenu().add(0, 7, 4, "Déplacer vers un dossier");
            p.getMenu().add(0, 8, 5, "Mettre à la corbeille");
        }
        p.setOnMenuItemClickListener(m -> handleItemMenu(item, m));
        p.show();
    }

    private boolean handleItemMenu(NoteDatabase.Item item, MenuItem m) {
        int id = m.getItemId();
        if (item.folder) {
            if (id == 1) renameFolder(item);
            if (id == 2) {
                boolean ok = db.deleteFolderIfEmpty(item.id);
                toast(ok ? "Dossier supprimé" : "Dossier non vide : suppression bloquée");
                reload();
            }
            return true;
        }
        if (id == 10) db.setNoteFlag(item.id, "trashed", false);
        else if (id == 11) confirmDeleteForever(item.id);
        else if (id == 3) db.setNoteFlag(item.id, "favorite", !item.favorite);
        else if (id == 4) db.setNoteFlag(item.id, "pinned", !item.pinned);
        else if (id == 5) db.setNoteFlag(item.id, "archived", !item.archived);
        else if (id == 6) { db.duplicateNote(item.id); toast("Note dupliquée"); }
        else if (id == 7) chooseFolder(item.id);
        else if (id == 8) db.setNoteFlag(item.id, "trashed", true);
        reload();
        return true;
    }

    private void renameFolder(NoteDatabase.Item item) {
        EditText input = new EditText(this);
        input.setText(item.title);
        input.setSelection(input.length());
        new AlertDialog.Builder(this).setTitle("Renommer le dossier").setView(input)
                .setNegativeButton("Annuler", null)
                .setPositiveButton("Enregistrer", (d, w) -> { db.renameFolder(item.id, input.getText().toString()); reload(); }).show();
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
        names[0] = "Accueil";
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
        p.getMenu().add(0, 100, 0, "Exporter une sauvegarde JSON");
        p.getMenu().add(0, 101, 1, "Importer une sauvegarde JSON");
        p.getMenu().add(0, 102, 2, prefs.getString("pin_hash", "").isEmpty() ? "Activer verrouillage PIN" : "Modifier / désactiver le PIN");
        p.getMenu().add(0, 103, 3, prefs.getBoolean("light", false) ? "Passer en mode sombre" : "Passer en mode clair");
        p.getMenu().add(0, 104, 4, "Vider définitivement la corbeille");
        p.getMenu().add(0, 105, 5, "À propos");
        p.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 100) exportBackup();
            else if (item.getItemId() == 101) importBackup();
            else if (item.getItemId() == 102) pinSettings();
            else if (item.getItemId() == 103) toggleTheme();
            else if (item.getItemId() == 104) emptyTrash();
            else if (item.getItemId() == 105) new AlertDialog.Builder(this).setTitle("Bloc Note Ultra")
                    .setMessage("Bloc-notes Android natif • SQLite local • autosauvegarde • dossiers • favoris • archive • corbeille • tags • dictée • export/import.\n\nToutes les données restent sur l'appareil sauf lorsque vous les partagez ou les exportez.")
                    .setPositiveButton("OK", null).show();
            return true;
        });
        p.show();
    }

    private void exportBackup() {
        Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("application/json");
        i.putExtra(Intent.EXTRA_TITLE, "bloc-note-ultra-backup.json");
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

    private class NotesAdapter extends BaseAdapter {
        private final LayoutInflater inflater;
        private final DateFormat date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        NotesAdapter(Context c) { inflater = LayoutInflater.from(c); }
        @Override public int getCount() { return items.size(); }
        @Override public Object getItem(int position) { return items.get(position); }
        @Override public long getItemId(int position) { return items.get(position).id; }
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView == null ? inflater.inflate(R.layout.row_item, parent, false) : convertView;
            NoteDatabase.Item x = items.get(position);
            TextView icon = v.findViewById(R.id.icon);
            TextView t = v.findViewById(R.id.itemTitle);
            TextView preview = v.findViewById(R.id.itemPreview);
            TextView meta = v.findViewById(R.id.itemMeta);
            if (x.folder) {
                icon.setText("📁"); t.setText(x.title); preview.setText(x.preview); meta.setText("›");
            } else {
                icon.setText(x.trashed ? "🗑" : x.pinned ? "📌" : x.favorite ? "★" : "📝");
                t.setText(x.title);
                String p = x.preview;
                if (!x.tags.trim().isEmpty()) p = "#" + x.tags.replace(",", "  #") + "\n" + p;
                preview.setText(p);
                meta.setText(date.format(new Date(x.updatedAt)));
            }
            return v;
        }
    }
}
