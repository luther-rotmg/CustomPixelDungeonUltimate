package com.qsr.customspd.tools.apidiff;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaSurfaceExtractorTest {

    @Test
    void extractsPublicMethodSignatures() {
        String source = """
            package com.example;
            public class Foo {
                public void bar(int x) {}
                public String baz() { return ""; }
                private void hidden() {}
            }
            """;
        JavaSurface surface = JavaSurfaceExtractor.extract("com/example/Foo.java", source);
        assertEquals(2, surface.symbols().size());
        assertTrue(surface.symbols().contains(new JavaSurface.Symbol("com.example.Foo", "bar(int)", "public", "void")));
        assertTrue(surface.symbols().contains(new JavaSurface.Symbol("com.example.Foo", "baz()", "public", "String")));
    }
}
