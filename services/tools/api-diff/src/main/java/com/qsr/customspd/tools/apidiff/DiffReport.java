package com.qsr.customspd.tools.apidiff;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The delta between two {@link JavaSurface} snapshots of the same file
 * (typically the same path at two git refs).
 */
public record DiffReport(List<Removed> removed, List<Added> added, List<SignatureChanged> signatureChanged) {

    public record Removed(JavaSurface.Symbol symbol) {
    }

    public record Added(JavaSurface.Symbol symbol) {
    }

    public record SignatureChanged(JavaSurface.Symbol before, JavaSurface.Symbol after) {
    }

    public static DiffReport compare(JavaSurface before, JavaSurface after) {
        Map<String, JavaSurface.Symbol> beforeByMember = indexByMember(before);
        Map<String, JavaSurface.Symbol> afterByMember = indexByMember(after);

        List<Removed> removedList = new ArrayList<>();
        List<Added> addedList = new ArrayList<>();
        List<SignatureChanged> changedList = new ArrayList<>();

        for (Map.Entry<String, JavaSurface.Symbol> entry : beforeByMember.entrySet()) {
            JavaSurface.Symbol beforeSymbol = entry.getValue();
            JavaSurface.Symbol afterSymbol = afterByMember.get(entry.getKey());
            if (afterSymbol == null) {
                removedList.add(new Removed(beforeSymbol));
            } else if (!beforeSymbol.equals(afterSymbol)) {
                changedList.add(new SignatureChanged(beforeSymbol, afterSymbol));
            }
        }

        for (Map.Entry<String, JavaSurface.Symbol> entry : afterByMember.entrySet()) {
            if (!beforeByMember.containsKey(entry.getKey())) {
                addedList.add(new Added(entry.getValue()));
            }
        }

        return new DiffReport(removedList, addedList, changedList);
    }

    /**
     * Indexes symbols by declaring type + member name (signature with the
     * parameter list stripped), so that a parameter-list or return-type edit
     * on the same member is matched up as a change rather than reported as
     * an unrelated removal/addition pair.
     */
    private static Map<String, JavaSurface.Symbol> indexByMember(JavaSurface surface) {
        Map<String, JavaSurface.Symbol> byMember = new LinkedHashMap<>();
        for (JavaSurface.Symbol symbol : surface.symbols()) {
            byMember.put(memberKey(symbol), symbol);
        }
        return byMember;
    }

    private static String memberKey(JavaSurface.Symbol symbol) {
        String signature = symbol.signature();
        int parenIndex = signature.indexOf('(');
        String memberName = parenIndex >= 0 ? signature.substring(0, parenIndex) : signature;
        return symbol.typeName() + "#" + memberName;
    }
}
