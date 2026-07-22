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

    @Test
    void upcastFromSyntheticCpdV21FixtureRoundtrips() throws Exception {
        Bundle b = BundleFixtureBuilder.cpdV21Sample();
        Bundle upcast = BundleBridge.upcast(b, "cpd-v2.1.0-1.0");
        assertNotNull(upcast);
        // Slice 0 stubs are passthroughs; deeper assertions land in Slices 3a/5b/6c
        // when translators are populated. For now, just prove non-null round-trip
        // through the full translator chain.
        assertEquals(b.getString("version"), upcast.getString("version"),
            "passthrough stubs preserve the version field verbatim");
    }
}
