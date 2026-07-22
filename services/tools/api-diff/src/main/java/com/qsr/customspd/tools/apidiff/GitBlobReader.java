package com.qsr.customspd.tools.apidiff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Reads a file's content at a given git ref via {@code git show <ref>:<path>}.
 *
 * <p>Note: git resolves {@code <path>} relative to the repository root
 * regardless of the process's current working directory, so this reader
 * works correctly no matter where the JVM was launched from within the
 * repository.
 */
public final class GitBlobReader {

    private GitBlobReader() {
    }

    /**
     * @param gitRef   a git ref (branch, tag, or commit SHA)
     * @param filePath repository-root-relative path to the file
     * @return the file's content at {@code gitRef}
     * @throws IOException if {@code git show} exits non-zero (e.g. the path
     *                      does not exist at that ref) or the process cannot
     *                      be started
     */
    public static String read(String gitRef, String filePath) throws IOException {
        String blobSpec = gitRef + ":" + filePath;
        ProcessBuilder builder = new ProcessBuilder("git", "show", blobSpec);
        Process process = builder.start();

        StringBuilder stderrBuffer = new StringBuilder();
        Thread stderrDrain = new Thread(() -> {
            try {
                stderrBuffer.append(readStream(process.getErrorStream()));
            } catch (IOException ignored) {
                // best-effort: stderr is only used for the error message
            }
        });
        stderrDrain.start();

        String stdout = readStream(process.getInputStream());

        int exitCode;
        try {
            exitCode = process.waitFor();
            stderrDrain.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for: git show " + blobSpec, e);
        }

        if (exitCode != 0) {
            throw new IOException("git show " + blobSpec + " failed with exit code "
                    + exitCode + ": " + stderrBuffer.toString().trim());
        }
        return stdout;
    }

    private static String readStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, read);
            }
        }
        return builder.toString();
    }
}
