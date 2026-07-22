package com.qsr.customspd.tools.apidiff;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiffReportTest {

    @Test
    void reportsRemovedAddedAndSignatureChangedSymbols() {
        JavaSurface before = new JavaSurface(List.of(
                new JavaSurface.Symbol("com.example.Foo", "bar(int)", "public", "void"),
                new JavaSurface.Symbol("com.example.Foo", "baz()", "public", "String"),
                new JavaSurface.Symbol("com.example.Foo", "qux()", "public", "void")
        ));
        JavaSurface after = new JavaSurface(List.of(
                new JavaSurface.Symbol("com.example.Foo", "bar(int)", "public", "void"),
                new JavaSurface.Symbol("com.example.Foo", "baz(String)", "public", "String"),
                new JavaSurface.Symbol("com.example.Foo", "quux()", "public", "void")
        ));

        DiffReport report = DiffReport.compare(before, after);

        assertEquals(1, report.removed().size());
        assertEquals(1, report.added().size());
        assertEquals(1, report.signatureChanged().size());

        assertTrue(report.removed().contains(
                new DiffReport.Removed(new JavaSurface.Symbol("com.example.Foo", "qux()", "public", "void"))));
        assertTrue(report.added().contains(
                new DiffReport.Added(new JavaSurface.Symbol("com.example.Foo", "quux()", "public", "void"))));
        assertTrue(report.signatureChanged().contains(new DiffReport.SignatureChanged(
                new JavaSurface.Symbol("com.example.Foo", "baz()", "public", "String"),
                new JavaSurface.Symbol("com.example.Foo", "baz(String)", "public", "String"))));
    }
}
