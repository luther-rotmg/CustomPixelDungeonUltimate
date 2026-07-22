package com.qsr.customspd.tools.apidiff;

import java.util.List;

/**
 * The public/protected surface of a Java source file: every symbol
 * (method or field) visible to consumers outside the declaring package.
 */
public record JavaSurface(List<Symbol> symbols) {

    /**
     * A single public or protected member.
     *
     * @param typeName    fully qualified declaring type, e.g. {@code com.example.Foo}
     * @param signature   member signature, e.g. {@code bar(int)} or a field name
     * @param visibility  {@code public} or {@code protected}
     * @param returnType  return type for methods, declared type for fields
     */
    public record Symbol(String typeName, String signature, String visibility, String returnType) {
    }
}
