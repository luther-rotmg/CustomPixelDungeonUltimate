package com.watabou.utils;

/**
 * Test-scope helper that constructs synthetic Bundle instances resembling
 * historic CPD/SPD save formats. Slice 0 ships only a v2.1.0-1.0 stub;
 * later slices (3a/5b/6c) extend this with per-version fixtures that
 * translator implementations validate against.
 *
 * The builder deliberately avoids depending on live playtest saves so
 * Slice 0 can close atomically without manual-playtest coordination.
 * Real .dat-file fixtures land in the slice whose translator needs them.
 */
public final class BundleFixtureBuilder {

    private BundleFixtureBuilder() {}

    /**
     * Minimal synthetic Bundle shaped like a CPD v2.1.0-1.0 save.
     * Contains only enough fields to exercise BundleBridge's version
     * detection and translator-chain routing. Not intended as a
     * realistic playtest save (that comes in Slice 3a).
     */
    public static Bundle cpdV21Sample() {
        Bundle b = new Bundle();
        b.put("version", "cpd-v2.1.0-1.0");
        b.put("depth", 1);
        b.put("seed", 12345L);
        b.put("gold", 0);
        return b;
    }
}
