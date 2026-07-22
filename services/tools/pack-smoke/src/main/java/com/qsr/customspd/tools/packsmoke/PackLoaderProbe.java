package com.qsr.customspd.tools.packsmoke;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Structural boot-smoke probe for marketplace packs.
 *
 * <p>This is deliberately NOT a runtime probe: it never invokes the CPD game
 * runtime (that requires the full game classpath, out of scope for Sub-B
 * Slice 0 — see Slice 7 for the runtime-boot upgrade). It only verifies that
 * a pack's {@code mod_info.json} manifest parses, declares the required
 * fields, and that every asset path the manifest declares actually exists
 * on disk under the pack directory.
 */
public final class PackLoaderProbe {

    private static final String MANIFEST_FILE_NAME = "mod_info.json";
    private static final String ASSETS_FIELD_NAME = "assets";
    private static final String[] REQUIRED_FIELDS = {"name", "version", "min_cpd_version"};

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private PackLoaderProbe() {
    }

    /** Outcome of a {@link #probe(Path)} run. */
    public enum Status {
        /** Manifest parsed, all required fields present, all declared assets found on disk. */
        GREEN,
        /** {@code mod_info.json} is missing, unparseable, or missing a required field. */
        FAILED_MANIFEST_INVALID,
        /** An unexpected exception occurred while loading/validating the pack. */
        FAILED_LOAD_EXCEPTION,
        /** The manifest parsed fine, but a declared asset path doesn't exist on disk. */
        FAILED_ASSET_MISSING
    }

    /**
     * Result of probing a single pack. {@code detail} carries a human-readable
     * explanation for any non-{@link Status#GREEN} outcome; it is {@code null}
     * on a clean {@link Status#GREEN}.
     */
    public record Result(Status status, String detail) {

        public static Result green() {
            return new Result(Status.GREEN, null);
        }

        public static Result manifestInvalid(String detail) {
            return new Result(Status.FAILED_MANIFEST_INVALID, detail);
        }

        public static Result loadException(String detail) {
            return new Result(Status.FAILED_LOAD_EXCEPTION, detail);
        }

        public static Result assetMissing(String detail) {
            return new Result(Status.FAILED_ASSET_MISSING, detail);
        }
    }

    /**
     * Structurally validates the pack rooted at {@code packPath}: does its
     * manifest parse and declare the required fields, and do all assets it
     * declares exist on disk. Never throws — any unexpected failure is
     * reported as {@link Status#FAILED_LOAD_EXCEPTION} in the returned
     * {@link Result} rather than propagated to the caller.
     */
    public static Result probe(Path packPath) {
        try {
            Path manifestPath = packPath.resolve(MANIFEST_FILE_NAME);

            if (!Files.isRegularFile(manifestPath)) {
                return Result.manifestInvalid("manifest not found at " + manifestPath);
            }

            JsonNode manifest;
            try {
                manifest = MAPPER.readTree(manifestPath.toFile());
            } catch (IOException e) {
                return Result.manifestInvalid("manifest failed to parse: " + e.getMessage());
            }

            if (manifest == null || !manifest.isObject()) {
                return Result.manifestInvalid("manifest root is not a JSON object: " + manifestPath);
            }

            List<String> missingFields = new ArrayList<>();
            for (String field : REQUIRED_FIELDS) {
                JsonNode value = manifest.get(field);
                if (value == null || value.isNull()) {
                    missingFields.add(field);
                }
            }
            if (!missingFields.isEmpty()) {
                return Result.manifestInvalid(
                        "missing required field(s): " + String.join(", ", missingFields));
            }

            JsonNode assets = manifest.get(ASSETS_FIELD_NAME);
            if (assets != null && assets.isArray()) {
                for (JsonNode assetNode : assets) {
                    String relativePath = assetNode.asText();
                    Path assetPath = packPath.resolve(relativePath);
                    if (!Files.exists(assetPath)) {
                        return Result.assetMissing(
                                "declared asset not found on disk: " + relativePath);
                    }
                }
            }

            return Result.green();
        } catch (RuntimeException e) {
            return Result.loadException(e.toString());
        }
    }
}
