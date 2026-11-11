package com.chk.blocnote;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.io.FileOutputStream;
import static org.junit.Assert.*;

/** Vraies captures de l'interface Android dans un emulateur, avec uniquement des notes fictives. */
@RunWith(AndroidJUnit4.class)
public class StoreScreenshotInstrumentedTest {
    private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
    private final Context context = instrumentation.getTargetContext();
    private long featureId;
    private void seed() {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
                .putBoolean("light", false)
                .putBoolean("grid_mode", true)
                .putString("pin_hash", "")
                .commit();
        context.getSharedPreferences("test_updates", Context.MODE_PRIVATE).edit()
                .putLong("last_check", System.currentTimeMillis()).commit();
        TrialAccess.startIfNeeded(context);
        context.deleteDatabase("bloc_note_ultra.db");
        try (NoteDatabase db = new NoteDatabase(context)) {
            long work = db.createFolder("Travail", 0, "#D7832B", "📁");
            long personal = db.createFolder("Personnel", 0, "#9C7247", "🏡");
            long ideas = db.createFolder("Idées", 0, "#B46A2B", "💡");
            add(db, work, "Réunion de projet", "Ordre du jour\n• Objectifs et priorités\n• Organisation de la semaine\n• Prochaines étapes", "travail, projet", true, false);
            featureId = add(db, 0, "Mes objectifs", "✓ Organiser mes idées\n✓ Préparer les prochaines étapes\n☐ Faire le point vendredi\n☐ Noter les nouvelles inspirations", "organisation, personnel", true, false);
            add(db, 0, "Liste de courses", "☐ Fruits et légumes\n☐ Pain complet\n☐ Riz et pâtes\n☐ Eau et café", "quotidien", false, false);
            add(db, personal, "Idées pour le week-end", "Une balade en famille\nUn bon film\nPréparer la semaine suivante", "famille", false, false);
            add(db, ideas, "Projet créatif", "Concept\nUne application simple, rapide et élégante.\n\nInspiration\nNoter les idées dès qu'elles arrivent.", "création", false, false);
            add(db, 0, "Lectures à découvrir", "Une sélection de livres et d'articles à explorer.", "lecture", false, true);
        }
    }
    private long add(NoteDatabase db, long folder, String title, String body, String tags, boolean fav, boolean archived) {
        long id = db.createNote(folder);
        NoteDatabase.Note note = db.getNote(id);
        note.title = title; note.body = body; note.tags = tags;
        note.favorite = fav; note.archived = archived;
        db.saveNote(note);
        return id;
    }
    private void screenshot(String name) throws Exception {
        instrumentation.waitForIdleSync();
        Thread.sleep(500);
        Bitmap bitmap = instrumentation.getUiAutomation().takeScreenshot();
        assertNotNull("La capture Android doit provenir de l'ecran reel", bitmap);
        assertTrue(bitmap.getWidth() >= 1080);
        assertTrue(bitmap.getHeight() >= 1920);
        File parent = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "play-store");
        assertTrue(parent.exists() || parent.mkdirs());
        File out = new File(parent, name + ".png");
        try (FileOutputStream stream = new FileOutputStream(out)) {
            assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream));
            stream.flush();
        }
        bitmap.recycle();
    }
    @Test public void captureFiveRealScreens() throws Exception {
        seed();
        try (ActivityScenario<MainActivity> scene = ActivityScenario.launch(MainActivity.class)) {
            screenshot("01-accueil-notes");
            scene.onActivity(a -> a.findViewById(R.id.filterFav).performClick());
            screenshot("02-favoris");
            scene.onActivity(a -> a.findViewById(R.id.navFolders).performClick());
            screenshot("03-dossiers");
            scene.onActivity(a -> a.findViewById(R.id.filterArchive).performClick());
            screenshot("04-archives");
        }
        Intent editor = new Intent(context, EditorActivity.class);
        editor.putExtra("note_id", featureId);
        try (ActivityScenario<EditorActivity> scene = ActivityScenario.launch(editor)) {
            screenshot("05-editeur");
        }
    }
}
