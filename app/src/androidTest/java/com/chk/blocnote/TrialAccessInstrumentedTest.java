package com.chk.blocnote;

import static org.junit.Assert.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TrialAccessInstrumentedTest {
    private Context context;
    private SharedPreferences prefs;
    @Before public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        prefs = context.getSharedPreferences("blocnote_access", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }
    @After public void clean() {
        prefs.edit().clear().commit();
    }
    @Test public void firstLaunchHas24HourTrial() {
        TrialAccess.startIfNeeded(context);
        long remaining = TrialAccess.remaining(context);
        assertTrue(remaining > TrialAccess.DURATION_MS - 60000L);
        assertFalse(TrialAccess.purchased(context));
    }
    @Test public void trialStartDoesNotResetOnSecondLaunch() {
        TrialAccess.startIfNeeded(context);
        long origin = prefs.getLong("trial_start", 0);
        TrialAccess.startIfNeeded(context);
        assertEquals(origin, prefs.getLong("trial_start", 0));
    }
    @Test public void expiredTrialNeedsPurchase() {
        long now = System.currentTimeMillis();
        prefs.edit().putLong("trial_start", now-TrialAccess.DURATION_MS-60000L)
          .putLong("last_wall", now).putLong("last_elapsed", SystemClock.elapsedRealtime()).commit();
        assertEquals(0L, TrialAccess.remaining(context));
        assertFalse(TrialAccess.hasAccess(context));
    }
    @Test public void purchasedLicensePermitsExpiredTrial() {
        long now = System.currentTimeMillis();
        prefs.edit().putLong("trial_start", now-TrialAccess.DURATION_MS-60000L)
          .putLong("last_wall", now).putLong("last_elapsed", SystemClock.elapsedRealtime())
          .putBoolean("premium", true).commit();
        assertTrue(TrialAccess.hasAccess(context));
        assertEquals("Licence définitive active", TrialAccess.status(context));
    }
    @Test public void rollbackClockCannotRestartFinishedTrial() {
        long now = System.currentTimeMillis();
        prefs.edit().putLong("trial_start", now-TrialAccess.DURATION_MS-1L)
          .putLong("last_wall", now+120000L)
          .putLong("last_elapsed", SystemClock.elapsedRealtime()).commit();
        assertFalse(TrialAccess.hasAccess(context));
    }
}
