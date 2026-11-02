package com.chk.blocnote;

import static org.junit.Assert.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UpdateVersionInstrumentedTest {
    @Test public void newerVersionIsDetected() {
        assertTrue(TestUpdateManager.isNewer("2.2.28", "2.2.27"));
        assertTrue(TestUpdateManager.isNewer("2.3.0", "2.2.999"));
        assertFalse(TestUpdateManager.isNewer("2.2.27", "2.2.27"));
        assertFalse(TestUpdateManager.isNewer("2.2.26", "2.2.27"));
        assertFalse(TestUpdateManager.isNewer("not-a-version", "2.2.27"));
    }
}
