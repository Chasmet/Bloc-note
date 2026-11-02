package com.chk.blocnote;

import static org.junit.Assert.*;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class NoteDatabaseInstrumentedTest {
    private Context app;
    private NoteDatabase db;

    @Before public void before() {
        app = InstrumentationRegistry.getInstrumentation().getTargetContext();
        app.deleteDatabase("bloc_note_ultra.db");
        db = new NoteDatabase(app);
    }

    @After public void after() {
        if (db != null) db.close();
        app.deleteDatabase("bloc_note_ultra.db");
    }

    @Test public void createAndEditNotePersists() {
        long folder = db.createFolder("Travail", 0);
        assertTrue(folder > 0);
        long id = db.createNote(folder);
        assertTrue(id > 0);
        NoteDatabase.Note n = db.getNote(id);
        assertNotNull(n);
        n.title = "Réunion";
        n.body = "Ligne A\nLigne B";
        n.tags = "projet, suivi";
        db.saveNote(n);
        NoteDatabase.Note loaded = db.getNote(id);
        assertEquals("Réunion", loaded.title);
        assertEquals("Ligne A\nLigne B", loaded.body);
        assertEquals("projet, suivi", loaded.tags);
        assertEquals(folder, loaded.folderId);
    }

    @Test public void flagsArchiveTrashRestoreWork() {
        long id = db.createNote(0);
        db.setNoteFlag(id, "favorite", true);
        db.setNoteFlag(id, "pinned", true);
        assertTrue(db.getNote(id).favorite);
        assertTrue(db.getNote(id).pinned);
        db.setNoteFlag(id, "archived", true);
        assertTrue(db.getNote(id).archived);
        assertEquals(1, db.listItems(0, "archive", "").size());
        db.setNoteFlag(id, "trashed", true);
        assertEquals(1, db.listItems(0, "trash", "").size());
        db.setNoteFlag(id, "trashed", false);
        assertEquals(0, db.listItems(0, "trash", "").size());
    }

    @Test public void nestedFolderCannotBeDeletedWhileNonempty() {
        long parent = db.createFolder("Parent", 0);
        long child = db.createFolder("Enfant", parent);
        assertFalse(db.deleteFolderIfEmpty(parent));
        long note = db.createNote(child);
        assertFalse(db.deleteFolderIfEmpty(child));
        db.deleteNoteForever(note);
        assertTrue(db.deleteFolderIfEmpty(child));
        assertTrue(db.deleteFolderIfEmpty(parent));
    }

    @Test public void exportImportRecoversSavedData() throws Exception {
        long folder = db.createFolder("Voyage", 0);
        long id = db.createNote(folder);
        NoteDatabase.Note note = db.getNote(id);
        note.title = "Test export";
        note.body = "Ne pas perdre";
        db.saveNote(note);
        JSONObject backup = db.exportJson();
        assertEquals(1, backup.getJSONArray("notes").length());
        db.createNote(0);
        assertEquals(2, db.countActiveNotes());
        db.importJson(backup);
        assertEquals(1, db.countActiveNotes());
        assertEquals("Ne pas perdre", db.getNote(id).body);
    }

    @Test public void malformedBackupDoesNotEraseExistingNotes() throws Exception {
        long id = db.createNote(0);
        try {
            db.importJson(new JSONObject("{\"notes\":[]}"));
            fail("Une sauvegarde sans dossiers doit etre refusee");
        } catch (IllegalArgumentException expected) { }
        assertNotNull(db.getNote(id));
    }

    @Test public void sqlInjectionStringIsTreatedAsSearchText() {
        long id = db.createNote(0);
        NoteDatabase.Note n = db.getNote(id);
        n.title = "Important";
        db.saveNote(n);
        assertEquals(0, db.listItems(0, "notes", "' OR 1=1 --").size());
        assertEquals(1, db.listItems(0, "notes", "Important").size());
    }

    @Test public void trashDeletionPreservesRegularNotes() {
        long keep = db.createNote(0);
        long throwAway = db.createNote(0);
        db.setNoteFlag(throwAway, "trashed", true);
        assertEquals(1, db.emptyTrash());
        assertNull(db.getNote(throwAway));
        assertNotNull(db.getNote(keep));
    }

    @Test public void searchRespectsSelectedCollection() {
        long activeId = db.createNote(0);
        NoteDatabase.Note a = db.getNote(activeId);
        a.title = "Rapport unique";
        db.saveNote(a);
        long trashId = db.createNote(0);
        NoteDatabase.Note t = db.getNote(trashId);
        t.title = "Rapport supprimé";
        db.saveNote(t);
        db.setNoteFlag(trashId, "trashed", true);
        List<NoteDatabase.Item> trash = db.listItems(0, "trash", "Rapport");
        assertEquals("La recherche en corbeille doit rester dans la corbeille", 1, trash.size());
        assertEquals("Le resultat de la corbeille doit etre la note supprimee", trashId, trash.get(0).id);
        assertEquals("La recherche dans les favoris ne doit pas inclure les notes ordinaires",
                0, db.listItems(0, "fav", "Rapport").size());
        assertEquals("La recherche dans les archives doit exclure les notes actives",
                0, db.listItems(0, "archive", "Rapport").size());
        assertEquals(1, db.listItems(0, "notes", "Rapport unique").size());
    }

    @Test public void longNoteRoundTrip() {
        long id = db.createNote(0);
        NoteDatabase.Note n = db.getNote(id);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100000; i++) builder.append('x');
        n.body = builder.toString();
        db.saveNote(n);
        assertEquals(100000, db.getNote(id).body.length());
    }
}
