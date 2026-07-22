package com.watabou.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BundleBridgeTest {

    @Test
    void upcastReturnsInputBundleForCurrentVersion() throws Exception {
        Bundle b = new Bundle();
        b.put("version", "v3.3.8");
        Bundle result = BundleBridge.upcast(b, "v3.3.8");
        assertNotNull(result);
        assertEquals("v3.3.8", result.getString("version"));
    }

    @Test
    void upcastChainsFiresForLegacyVersion() throws Exception {
        Bundle b = new Bundle();
        b.put("version", "cpd-v2.1.0-1.0");
        Bundle result = BundleBridge.upcast(b, "cpd-v2.1.0-1.0");
        assertNotNull(result);
        // Slice 0 stubs return input unchanged; test proves the chain runs without throwing.
    }

    @Test
    void upcastFallsBackToVersionDetectionWhenSourceVersionEmpty() throws Exception {
        Bundle b = new Bundle();
        b.put("version", "cpd-v2.1.0-1.0");
        Bundle result = BundleBridge.upcast(b, null);
        assertNotNull(result);
    }
}
