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

/** Tests du mode examinateur sans divulguer le code prive dans le depot. */
@RunWith(AndroidJUnit4.class)
public class ReviewerAccessInstrumentedTest {
    private Context context;
    private SharedPreferences access;
    private SharedPreferences reviewer;

    @Before public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        access = context.getSharedPreferences("blocnote_access", Context.MODE_PRIVATE);
        reviewer = context.getSharedPreferences("sync30_review_access", Context.MODE_PRIVATE);
        access.edit().clear().commit();
        reviewer.edit().clear().commit();
        long now = System.currentTimeMillis();
        access.edit()
            .putLong("trial_start", now - TrialAccess.DURATION_MS - 100000L)
            .putLong("last_wall", now)
            .putLong("last_elapsed", SystemClock.elapsedRealtime())
            .putBoolean("premium", false).commit();
    }

    @After public void tearDown() {
        access.edit().clear().commit();
        reviewer.edit().clear().commit();
    }

    @Test public void invalidReviewerCodeCannotUnlockOrGrantPurchase() {
        assertFalse(ReviewerAccess.activate(context, "S30-INVALID-INVALID-INVALID-INVALID"));
        assertFalse(ReviewerAccess.isActive(context));
        assertFalse(TrialAccess.hasAccess(context));
        assertFalse(TrialAccess.purchased(context));
    }

    @Test public void activeReviewAllowsEditingWithoutFakePurchase() {
        reviewer.edit().putLong("review_until", System.currentTimeMillis() + 60000L).commit();
        assertTrue(ReviewerAccess.isActive(context));
        assertTrue(TrialAccess.hasAccess(context));
        assertFalse(TrialAccess.purchased(context));
        assertEquals("Accès examinateur Google Play temporaire", TrialAccess.status(context));
    }

    @Test public void expiredReviewDoesNotProvideLifetimeAccess() {
        reviewer.edit().putLong("review_until", System.currentTimeMillis() - 60000L).commit();
        assertFalse(ReviewerAccess.isActive(context));
        assertFalse(TrialAccess.hasAccess(context));
        assertFalse(TrialAccess.purchased(context));
    }
}
