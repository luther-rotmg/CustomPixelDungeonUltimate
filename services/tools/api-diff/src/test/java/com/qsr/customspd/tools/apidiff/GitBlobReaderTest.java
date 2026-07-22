package com.qsr.customspd.tools.apidiff;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitBlobReaderTest {

    @Test
    void readsFileContentAtGivenRef() throws IOException {
        String content = GitBlobReader.read("HEAD", "README.md");

        assertTrue(content.startsWith("<p align=\"center\">"));
        assertTrue(content.contains("Lutherverse"));
    }

    @Test
    void throwsIOExceptionWhenPathDoesNotExistAtRef() {
        assertThrows(IOException.class,
                () -> GitBlobReader.read("HEAD", "definitely/does/not/exist/Nope.java"));
    }
}
