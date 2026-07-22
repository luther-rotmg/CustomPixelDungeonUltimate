package com.qsr.customspd.tools.apidiff;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parses raw Java source into a {@link JavaSurface}: the set of public and
 * protected methods/fields a file exposes to the rest of the codebase.
 */
public final class JavaSurfaceExtractor {

    private JavaSurfaceExtractor() {
    }

    /**
     * @param path   source-relative path, used only for context in callers; not
     *               currently embedded in the resulting symbols
     * @param source raw Java source text
     */
    public static JavaSurface extract(String path, String source) {
        CompilationUnit unit = StaticJavaParser.parse(source);
        String packageName = unit.getPackageDeclaration()
                .map(pd -> pd.getNameAsString())
                .orElse("");

        List<JavaSurface.Symbol> symbols = new ArrayList<>();
        for (TypeDeclaration<?> type : unit.getTypes()) {
            collectFromType(type, packageName, symbols);
        }
        return new JavaSurface(symbols);
    }

    private static void collectFromType(TypeDeclaration<?> type, String enclosingName, List<JavaSurface.Symbol> symbols) {
        String fqName = enclosingName.isEmpty()
                ? type.getNameAsString()
                : enclosingName + "." + type.getNameAsString();

        for (BodyDeclaration<?> member : type.getMembers()) {
            if (member instanceof MethodDeclaration method) {
                addMethodSymbol(method, fqName, symbols);
            } else if (member instanceof FieldDeclaration field) {
                addFieldSymbols(field, fqName, symbols);
            } else if (member instanceof TypeDeclaration<?> nested) {
                collectFromType(nested, fqName, symbols);
            }
        }
    }

    private static void addMethodSymbol(MethodDeclaration method, String fqName, List<JavaSurface.Symbol> symbols) {
        String visibility = visibilityOf(method.getModifiers());
        if (!isSurfaceVisibility(visibility)) {
            return;
        }
        String params = method.getParameters().stream()
                .map(p -> p.getType().asString())
                .collect(Collectors.joining(", "));
        String signature = method.getNameAsString() + "(" + params + ")";
        symbols.add(new JavaSurface.Symbol(fqName, signature, visibility, method.getType().asString()));
    }

    private static void addFieldSymbols(FieldDeclaration field, String fqName, List<JavaSurface.Symbol> symbols) {
        String visibility = visibilityOf(field.getModifiers());
        if (!isSurfaceVisibility(visibility)) {
            return;
        }
        for (VariableDeclarator variable : field.getVariables()) {
            symbols.add(new JavaSurface.Symbol(fqName, variable.getNameAsString(), visibility, variable.getType().asString()));
        }
    }

    private static boolean isSurfaceVisibility(String visibility) {
        return "public".equals(visibility) || "protected".equals(visibility);
    }

    private static String visibilityOf(NodeList<Modifier> modifiers) {
        for (Modifier modifier : modifiers) {
            if (modifier.getKeyword() == Modifier.Keyword.PUBLIC) {
                return "public";
            }
            if (modifier.getKeyword() == Modifier.Keyword.PROTECTED) {
                return "protected";
            }
            if (modifier.getKeyword() == Modifier.Keyword.PRIVATE) {
                return "private";
            }
        }
        return "package-private";
    }
}
