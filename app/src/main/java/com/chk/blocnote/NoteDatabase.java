package com.chk.blocnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NoteDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "bloc_note_ultra.db";
    private static final int DB_VERSION = 1;

    public static class Item {
        public long id;
        public boolean folder;
        public String title = "";
        public String preview = "";
        public String tags = "";
        public long updatedAt;
        public boolean favorite;
        public boolean pinned;
        public boolean archived;
        public boolean trashed;
    }

    public static class Note {
        public long id;
        public long folderId;
        public String title = "";
        public String body = "";
        public String tags = "";
        public boolean favorite;
        public boolean pinned;
        public boolean archived;
        public boolean trashed;
        public long createdAt;
        public long updatedAt;
    }

    public NoteDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE folders (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, parent_id INTEGER NOT NULL DEFAULT 0, created_at INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, folder_id INTEGER NOT NULL DEFAULT 0, title TEXT NOT NULL DEFAULT '', body TEXT NOT NULL DEFAULT '', tags TEXT NOT NULL DEFAULT '', favorite INTEGER NOT NULL DEFAULT 0, pinned INTEGER NOT NULL DEFAULT 0, archived INTEGER NOT NULL DEFAULT 0, trashed INTEGER NOT NULL DEFAULT 0, created_at INTEGER NOT NULL, updated_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX idx_notes_folder ON notes(folder_id, trashed, archived)");
        db.execSQL("CREATE INDEX idx_notes_updated ON notes(updated_at DESC)");
        db.execSQL("CREATE INDEX idx_notes_favorite ON notes(favorite, trashed)");
        db.execSQL("CREATE INDEX idx_folders_parent ON folders(parent_id)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 : aucune migration nécessaire.
    }

    public long createFolder(String name, long parentId) {
        ContentValues v = new ContentValues();
        v.put("name", safe(name).trim().isEmpty() ? "Nouveau dossier" : safe(name).trim());
        v.put("parent_id", parentId);
        v.put("created_at", System.currentTimeMillis());
        return getWritableDatabase().insert("folders", null, v);
    }

    public boolean renameFolder(long id, String name) {
        ContentValues v = new ContentValues();
        v.put("name", safe(name).trim());
        return getWritableDatabase().update("folders", v, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteFolderIfEmpty(long id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM folders WHERE parent_id=?", new String[]{String.valueOf(id)});
        c1.moveToFirst();
        int sub = c1.getInt(0);
        c1.close();
        Cursor c2 = db.rawQuery("SELECT COUNT(*) FROM notes WHERE folder_id=?", new String[]{String.valueOf(id)});
        c2.moveToFirst();
        int notes = c2.getInt(0);
        c2.close();
        if (sub > 0 || notes > 0) return false;
        return db.delete("folders", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public long getParentFolder(long id) {
        if (id == 0) return 0;
        Cursor c = getReadableDatabase().rawQuery("SELECT parent_id FROM folders WHERE id=?", new String[]{String.valueOf(id)});
        long result = 0;
        if (c.moveToFirst()) result = c.getLong(0);
        c.close();
        return result;
    }

    public String getFolderName(long id) {
        if (id == 0) return "Mes notes";
        Cursor c = getReadableDatabase().rawQuery("SELECT name FROM folders WHERE id=?", new String[]{String.valueOf(id)});
        String result = "Dossier";
        if (c.moveToFirst()) result = c.getString(0);
        c.close();
        return result;
    }

    public String getFolderPath(long id) {
        if (id == 0) return "Accueil";
        List<String> parts = new ArrayList<>();
        long cur = id;
        int guard = 0;
        while (cur != 0 && guard++ < 30) {
            Cursor c = getReadableDatabase().rawQuery("SELECT name,parent_id FROM folders WHERE id=?", new String[]{String.valueOf(cur)});
            if (!c.moveToFirst()) { c.close(); break; }
            parts.add(0, c.getString(0));
            cur = c.getLong(1);
            c.close();
        }
        StringBuilder b = new StringBuilder("Accueil");
        for (String p : parts) b.append(" › ").append(p);
        return b.toString();
    }

    public List<Item> getAllFolders() {
        ArrayList<Item> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT id,name,parent_id FROM folders ORDER BY name COLLATE NOCASE", null);
        while (c.moveToNext()) {
            Item x = new Item();
            x.id = c.getLong(0);
            x.folder = true;
            x.title = c.getString(1);
            x.preview = getFolderPath(x.id);
            out.add(x);
        }
        c.close();
        return out;
    }

    public long createNote(long folderId) {
        long now = System.currentTimeMillis();
        ContentValues v = new ContentValues();
        v.put("folder_id", folderId);
        v.put("title", "");
        v.put("body", "");
        v.put("tags", "");
        v.put("created_at", now);
        v.put("updated_at", now);
        return getWritableDatabase().insert("notes", null, v);
    }

    public Note getNote(long id) {
        Cursor c = getReadableDatabase().rawQuery("SELECT id,folder_id,title,body,tags,favorite,pinned,archived,trashed,created_at,updated_at FROM notes WHERE id=?", new String[]{String.valueOf(id)});
        Note n = null;
        if (c.moveToFirst()) n = noteFromCursor(c);
        c.close();
        return n;
    }

    public void saveNote(Note n) {
        if (n == null) return;
        n.updatedAt = System.currentTimeMillis();
        ContentValues v = new ContentValues();
        v.put("folder_id", n.folderId);
        v.put("title", safe(n.title));
        v.put("body", safe(n.body));
        v.put("tags", safe(n.tags));
        v.put("favorite", n.favorite ? 1 : 0);
        v.put("pinned", n.pinned ? 1 : 0);
        v.put("archived", n.archived ? 1 : 0);
        v.put("trashed", n.trashed ? 1 : 0);
        v.put("updated_at", n.updatedAt);
        getWritableDatabase().update("notes", v, "id=?", new String[]{String.valueOf(n.id)});
    }

    public long duplicateNote(long id) {
        Note n = getNote(id);
        if (n == null) return -1;
        long now = System.currentTimeMillis();
        ContentValues v = new ContentValues();
        v.put("folder_id", n.folderId);
        v.put("title", (safe(n.title).trim().isEmpty() ? "Sans titre" : n.title) + " — copie");
        v.put("body", n.body);
        v.put("tags", n.tags);
        v.put("favorite", 0);
        v.put("pinned", 0);
        v.put("archived", 0);
        v.put("trashed", 0);
        v.put("created_at", now);
        v.put("updated_at", now);
        return getWritableDatabase().insert("notes", null, v);
    }

    public void setNoteFlag(long id, String flag, boolean value) {
        if (!(flag.equals("favorite") || flag.equals("pinned") || flag.equals("archived") || flag.equals("trashed"))) return;
        ContentValues v = new ContentValues();
        v.put(flag, value ? 1 : 0);
        v.put("updated_at", System.currentTimeMillis());
        getWritableDatabase().update("notes", v, "id=?", new String[]{String.valueOf(id)});
    }

    public void moveNote(long id, long folderId) {
        ContentValues v = new ContentValues();
        v.put("folder_id", folderId);
        v.put("updated_at", System.currentTimeMillis());
        getWritableDatabase().update("notes", v, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteNoteForever(long id) {
        getWritableDatabase().delete("notes", "id=?", new String[]{String.valueOf(id)});
    }

    public int emptyTrash() {
        return getWritableDatabase().delete("notes", "trashed=1", null);
    }

    public List<Item> listItems(long folderId, String filter, String query) {
        ArrayList<Item> out = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String q = safe(query).trim();

        if (q.isEmpty() && "notes".equals(filter)) {
            Cursor f = db.rawQuery("SELECT id,name,(SELECT COUNT(*) FROM notes n WHERE n.folder_id=folders.id AND n.trashed=0) FROM folders WHERE parent_id=? ORDER BY name COLLATE NOCASE", new String[]{String.valueOf(folderId)});
            while (f.moveToNext()) {
                Item x = new Item();
                x.id = f.getLong(0);
                x.folder = true;
                x.title = f.getString(1);
                int count = f.getInt(2);
                x.preview = count + (count > 1 ? " notes" : " note");
                out.add(x);
            }
            f.close();
        }

        String where;
        ArrayList<String> args = new ArrayList<>();
        if (!q.isEmpty()) {
            where = "trashed=0 AND (title LIKE ? OR body LIKE ? OR tags LIKE ?)";
            String like = "%" + q + "%";
            args.add(like); args.add(like); args.add(like);
        } else if ("fav".equals(filter)) {
            where = "favorite=1 AND trashed=0";
        } else if ("archive".equals(filter)) {
            where = "archived=1 AND trashed=0";
        } else if ("trash".equals(filter)) {
            where = "trashed=1";
        } else {
            where = "folder_id=? AND archived=0 AND trashed=0";
            args.add(String.valueOf(folderId));
        }

        Cursor c = db.query("notes", new String[]{"id","title","body","tags","favorite","pinned","archived","trashed","updated_at"}, where, args.toArray(new String[0]), null, null, "pinned DESC, updated_at DESC", "1000");
        while (c.moveToNext()) {
            Item x = new Item();
            x.id = c.getLong(0);
            x.folder = false;
            x.title = safe(c.getString(1)).trim().isEmpty() ? "Sans titre" : c.getString(1);
            x.preview = compact(c.getString(2));
            x.tags = safe(c.getString(3));
            x.favorite = c.getInt(4) == 1;
            x.pinned = c.getInt(5) == 1;
            x.archived = c.getInt(6) == 1;
            x.trashed = c.getInt(7) == 1;
            x.updatedAt = c.getLong(8);
            out.add(x);
        }
        c.close();
        return out;
    }

    public int countActiveNotes() { return count("SELECT COUNT(*) FROM notes WHERE trashed=0"); }
    public int countFavorites() { return count("SELECT COUNT(*) FROM notes WHERE favorite=1 AND trashed=0"); }
    public int countTrash() { return count("SELECT COUNT(*) FROM notes WHERE trashed=1"); }

    private int count(String sql) {
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        c.moveToFirst();
        int x = c.getInt(0);
        c.close();
        return x;
    }

    public JSONObject exportJson() throws Exception {
        JSONObject root = new JSONObject();
        root.put("app", "Bloc Note Ultra");
        root.put("version", 1);
        root.put("exportedAt", System.currentTimeMillis());
        JSONArray folders = new JSONArray();
        Cursor f = getReadableDatabase().rawQuery("SELECT id,name,parent_id,created_at FROM folders ORDER BY id", null);
        while (f.moveToNext()) {
            JSONObject o = new JSONObject();
            o.put("id", f.getLong(0)); o.put("name", f.getString(1)); o.put("parentId", f.getLong(2)); o.put("createdAt", f.getLong(3));
            folders.put(o);
        }
        f.close();
        JSONArray notes = new JSONArray();
        Cursor c = getReadableDatabase().rawQuery("SELECT id,folder_id,title,body,tags,favorite,pinned,archived,trashed,created_at,updated_at FROM notes ORDER BY id", null);
        while (c.moveToNext()) {
            JSONObject o = new JSONObject();
            o.put("id", c.getLong(0)); o.put("folderId", c.getLong(1)); o.put("title", c.getString(2)); o.put("body", c.getString(3)); o.put("tags", c.getString(4));
            o.put("favorite", c.getInt(5) == 1); o.put("pinned", c.getInt(6) == 1); o.put("archived", c.getInt(7) == 1); o.put("trashed", c.getInt(8) == 1);
            o.put("createdAt", c.getLong(9)); o.put("updatedAt", c.getLong(10)); notes.put(o);
        }
        c.close();
        root.put("folders", folders); root.put("notes", notes);
        return root;
    }

    public void importJson(JSONObject root) throws Exception {
        JSONArray folders = root.optJSONArray("folders");
        JSONArray notes = root.optJSONArray("notes");
        if (folders == null || notes == null) throw new IllegalArgumentException("Sauvegarde invalide");
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("notes", null, null);
            db.delete("folders", null, null);
            for (int i = 0; i < folders.length(); i++) {
                JSONObject o = folders.getJSONObject(i);
                ContentValues v = new ContentValues();
                v.put("id", o.getLong("id")); v.put("name", o.optString("name", "Dossier")); v.put("parent_id", o.optLong("parentId", 0)); v.put("created_at", o.optLong("createdAt", System.currentTimeMillis()));
                db.insertOrThrow("folders", null, v);
            }
            for (int i = 0; i < notes.length(); i++) {
                JSONObject o = notes.getJSONObject(i);
                ContentValues v = new ContentValues();
                v.put("id", o.getLong("id")); v.put("folder_id", o.optLong("folderId", 0)); v.put("title", o.optString("title", "")); v.put("body", o.optString("body", "")); v.put("tags", o.optString("tags", ""));
                v.put("favorite", o.optBoolean("favorite") ? 1 : 0); v.put("pinned", o.optBoolean("pinned") ? 1 : 0); v.put("archived", o.optBoolean("archived") ? 1 : 0); v.put("trashed", o.optBoolean("trashed") ? 1 : 0);
                v.put("created_at", o.optLong("createdAt", System.currentTimeMillis())); v.put("updated_at", o.optLong("updatedAt", System.currentTimeMillis()));
                db.insertOrThrow("notes", null, v);
            }
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }

    private Note noteFromCursor(Cursor c) {
        Note n = new Note();
        n.id = c.getLong(0); n.folderId = c.getLong(1); n.title = c.getString(2); n.body = c.getString(3); n.tags = c.getString(4);
        n.favorite = c.getInt(5) == 1; n.pinned = c.getInt(6) == 1; n.archived = c.getInt(7) == 1; n.trashed = c.getInt(8) == 1;
        n.createdAt = c.getLong(9); n.updatedAt = c.getLong(10); return n;
    }

    private static String safe(String s) { return s == null ? "" : s; }
    private static String compact(String s) {
        String x = safe(s).replace('\n', ' ').replace('\r', ' ').trim().replaceAll("\\s+", " ");
        if (x.isEmpty()) return "Note vide";
        return x.length() > 115 ? x.substring(0, 115) + "…" : x;
    }
}
