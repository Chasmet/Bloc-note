package com.chk.blocnote;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {
    private Context context() {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        c.getSharedPreferences("test_updates", Context.MODE_PRIVATE)
                .edit().putLong("last_check", System.currentTimeMillis()).commit();
        return c;
    }

    @Test public void mainOpensAndShowsNavigation() {
        context();
        try (ActivityScenario<MainActivity> scene = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.navHome)).check(matches(isDisplayed()));
            onView(withId(R.id.navFolders)).check(matches(isDisplayed()));
            onView(withId(R.id.navFavorites)).check(matches(isDisplayed()));
            onView(withId(R.id.btnNew)).check(matches(isDisplayed()));
            onView(withId(R.id.search)).check(matches(isDisplayed()));
        }
    }

    @Test public void folderAndFavoritesTabsRespond() {
        context();
        try (ActivityScenario<MainActivity> scene = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.navFolders)).perform(click());
            onView(withId(R.id.title)).check(matches(withText("▣ Dossiers")));
            onView(withId(R.id.navFavorites)).perform(click());
            onView(withId(R.id.title)).check(matches(withText("★ Favoris")));
            onView(withId(R.id.navHome)).perform(click());
            onView(withId(R.id.title)).check(matches(withText("Mes notes")));
        }
    }

    @Test public void editorSavesTextOnReturn() {
        context();
        final String unique = "Audit " + System.nanoTime();
        try (ActivityScenario<MainActivity> scene = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.btnNew)).perform(click());
            onView(withId(R.id.noteTitle)).perform(replaceText(unique));
            onView(withId(R.id.noteBody)).perform(replaceText("Essai de sauvegarde automatique"));
            onView(withId(R.id.editorBack)).perform(click());
        }
        try (NoteDatabase db = new NoteDatabase(context())) {
            assertEquals(1, db.listItems(0, "notes", unique).size());
        }
    }

    @Test public void lightModeLaunchDoesNotCrash() {
        Context c = context();
        c.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .edit().putBoolean("light", true).commit();
        try (ActivityScenario<MainActivity> scene = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.mainRoot)).check(matches(isDisplayed()));
        } finally {
            c.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .edit().putBoolean("light", false).commit();
        }
    }
}
