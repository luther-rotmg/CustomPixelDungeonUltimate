package com.qsr.customspd.tools.packsmoke;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * CLI entry point for the pack boot-smoke harness.
 *
 * <p>Iterates every subdirectory of a marketplace root that contains a
 * {@code mod_info.json} manifest and runs {@link PackLoaderProbe#probe} on
 * it, printing a per-pack report line and a summary. Exits 0 if every pack
 * probed GREEN, 1 if any pack failed, so it can be wired into a build (see
 * the root {@code packSmoke} Gradle task) as a structural regression gate.
 */
public final class PackSmokeCli {

    static final String DEFAULT_MARKETPLACE_PATH = "marketplace/";
    static final String MANIFEST_FILE_NAME = "mod_info.json";

    private PackSmokeCli() {
    }

    public static void main(String[] args) {
        Args parsed;
        try {
            parsed = Args.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Usage: PackSmokeCli [--marketplace <path>]");
            System.err.println(e.getMessage());
            System.exit(2);
            return;
        }

        Path marketplaceRoot = Paths.get(parsed.marketplacePath);
        Result result;
        try {
            result = run(marketplaceRoot);
        } catch (IOException e) {
            System.err.println("Failed to scan marketplace directory " + marketplaceRoot + ": " + e.getMessage());
            System.exit(2);
            return;
        }

        print(marketplaceRoot, result);
        System.exit(result.failures() == 0 ? 0 : 1);
    }

    /**
     * Runs the boot-smoke sweep and returns the aggregate result, without
     * printing or exiting. Exposed at package visibility for testability.
     */
    static Result run(Path marketplaceRoot) throws IOException {
        if (!Files.isDirectory(marketplaceRoot)) {
            throw new IOException("marketplace root is not a directory: " + marketplaceRoot);
        }

        List<Path> packDirs = new ArrayList<>();
        try (DirectoryStream<Path> children = Files.newDirectoryStream(marketplaceRoot)) {
            for (Path child : children) {
                if (Files.isDirectory(child) && Files.isRegularFile(child.resolve(MANIFEST_FILE_NAME))) {
                    packDirs.add(child);
                }
            }
        }
        packDirs.sort(Comparator.comparing(p -> p.getFileName().toString(), String.CASE_INSENSITIVE_ORDER));

        List<PackResult> packResults = new ArrayList<>();
        int failures = 0;
        for (Path packDir : packDirs) {
            PackLoaderProbe.Result probeResult = PackLoaderProbe.probe(packDir);
            packResults.add(new PackResult(packDir.getFileName().toString(), probeResult));
            if (probeResult.status() != PackLoaderProbe.Status.GREEN) {
                failures++;
            }
        }

        return new Result(packResults, failures);
    }

    private static void print(Path marketplaceRoot, Result result) {
        System.out.println("Pack boot-smoke: " + marketplaceRoot);
        for (PackResult packResult : result.packResults()) {
            PackLoaderProbe.Result probeResult = packResult.result();
            if (probeResult.status() == PackLoaderProbe.Status.GREEN) {
                System.out.println(packResult.name() + ": GREEN");
            } else {
                System.out.println(packResult.name() + ": " + probeResult.status() + " - " + probeResult.detail());
            }
        }

        int total = result.packResults().size();
        int green = total - result.failures();
        System.out.println("Total: " + green + "/" + total + " GREEN");
        System.out.println(result.failures() == 0
                ? "RESULT: PASS (all packs GREEN)"
                : "RESULT: FAIL (" + result.failures() + " pack(s) failed structural boot-smoke)");
    }

    /** A single pack's name (directory name) paired with its probe result. */
    record PackResult(String name, PackLoaderProbe.Result result) {
    }

    /** Aggregate outcome of a boot-smoke sweep across every probed pack. */
    record Result(List<PackResult> packResults, int failures) {
    }

    private static final class Args {
        String marketplacePath = DEFAULT_MARKETPLACE_PATH;

        static Args parse(String[] args) {
            Args result = new Args();
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--marketplace" -> result.marketplacePath = requireValue(args, ++i, "--marketplace");
                    default -> throw new IllegalArgumentException("Unrecognized argument: " + args[i]);
                }
            }
            return result;
        }

        private static String requireValue(String[] args, int index, String flag) {
            if (index >= args.length) {
                throw new IllegalArgumentException("Missing value for " + flag);
            }
            return args[index];
        }
    }
}
