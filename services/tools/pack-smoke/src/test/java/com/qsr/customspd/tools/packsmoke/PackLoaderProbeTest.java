package com.qsr.customspd.tools.packsmoke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PackLoaderProbeTest {

    @TempDir
    Path tempDir;

    private Path validPackDir;
    private Path malformedManifestPackDir;

    @BeforeEach
    void setUp() throws IOException {
        // A structurally valid pack: manifest has all required fields and
        // its declared asset actually exists on disk.
        validPackDir = tempDir.resolve("valid-pack");
        Files.createDirectories(validPackDir.resolve("sprites"));
        Files.writeString(validPackDir.resolve("sprites").resolve("hero.png"), "not-really-a-png");
        Files.writeString(validPackDir.resolve("mod_info.json"), """
                {
                  "name": "Valid Pack",
                  "version": 1,
                  "min_cpd_version": 10,
                  "assets": ["sprites/hero.png"]
                }
                """);

        // A pack whose mod_info.json is not parseable JSON at all.
        malformedManifestPackDir = tempDir.resolve("malformed-manifest-pack");
        Files.createDirectories(malformedManifestPackDir);
        Files.writeString(malformedManifestPackDir.resolve("mod_info.json"), """
                {
                  "name": "Broken Pack",
                  "version": 1,
                  this is not valid JSON at all
                """);
    }

    @Test
    void validPackReturnsGreen() {
        PackLoaderProbe.Result result = PackLoaderProbe.probe(validPackDir);

        assertEquals(PackLoaderProbe.Status.GREEN, result.status());
    }

    @Test
    void malformedManifestReturnsFailedManifestInvalid() {
        PackLoaderProbe.Result result = PackLoaderProbe.probe(malformedManifestPackDir);

        assertEquals(PackLoaderProbe.Status.FAILED_MANIFEST_INVALID, result.status());
        assertNotNull(result.detail());
    }

    @Test
    void missingManifestFileReturnsFailedManifestInvalid() throws IOException {
        Path noManifestPackDir = tempDir.resolve("no-manifest-pack");
        Files.createDirectories(noManifestPackDir);

        PackLoaderProbe.Result result = PackLoaderProbe.probe(noManifestPackDir);

        assertEquals(PackLoaderProbe.Status.FAILED_MANIFEST_INVALID, result.status());
    }

    @Test
    void manifestMissingRequiredFieldReturnsFailedManifestInvalid() throws IOException {
        Path packDir = tempDir.resolve("missing-field-pack");
        Files.createDirectories(packDir);
        Files.writeString(packDir.resolve("mod_info.json"), """
                {
                  "name": "No Version Pack",
                  "min_cpd_version": 10
                }
                """);

        PackLoaderProbe.Result result = PackLoaderProbe.probe(packDir);

        assertEquals(PackLoaderProbe.Status.FAILED_MANIFEST_INVALID, result.status());
    }

    @Test
    void manifestReferencingMissingAssetReturnsFailedAssetMissing() throws IOException {
        Path packDir = tempDir.resolve("missing-asset-pack");
        Files.createDirectories(packDir);
        Files.writeString(packDir.resolve("mod_info.json"), """
                {
                  "name": "Missing Asset Pack",
                  "version": 1,
                  "min_cpd_version": 10,
                  "assets": ["sprites/does-not-exist.png"]
                }
                """);

        PackLoaderProbe.Result result = PackLoaderProbe.probe(packDir);

        assertEquals(PackLoaderProbe.Status.FAILED_ASSET_MISSING, result.status());
        assertNotNull(result.detail());
    }

    @Test
    void manifestWithUnresolvableAssetPathReturnsFailedLoadException() throws IOException {
        // '?' is not a legal Windows path character; resolving it throws
        // InvalidPathException — a genuine unexpected-exception condition
        // distinct from "file simply doesn't exist".
        Path packDir = tempDir.resolve("bad-asset-path-pack");
        Files.createDirectories(packDir);
        Files.writeString(packDir.resolve("mod_info.json"), """
                {
                  "name": "Bad Path Pack",
                  "version": 1,
                  "min_cpd_version": 10,
                  "assets": ["sprites/inval?d.png"]
                }
                """);

        PackLoaderProbe.Result result = PackLoaderProbe.probe(packDir);

        assertEquals(PackLoaderProbe.Status.FAILED_LOAD_EXCEPTION, result.status());
        assertNotNull(result.detail());
    }
}
