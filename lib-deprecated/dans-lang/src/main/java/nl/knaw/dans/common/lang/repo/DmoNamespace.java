package nl.knaw.dans.common.lang.repo;

import java.io.Serializable;
import java.util.regex.Pattern;

public class DmoNamespace implements Serializable, Comparable<DmoNamespace> {

    private static final long serialVersionUID = -2392955560272944207L;
    private static final String REG_EX = "[a-zA-Z0-9-]*";
    private static final Pattern PATTERN = Pattern.compile(REG_EX);

    private final String value;

    public static boolean isValidNamespace(String value) {
        if (value == null || "".equals(value)) {
            return false;
        }
        return PATTERN.matcher(value).matches();
    }

    public DmoNamespace(String value) {
        if (!isValidNamespace(value)) {
            throw new IllegalArgumentException("Invallid namespace: " + value);
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DmoNamespace) {
            return value.equals(((DmoNamespace) obj).value);
        }
        return false;
    }

    public boolean sameAs(DmoNamespace ns) {
        return value.equals(ns.value);
    }

    @Override
    public int hashCode() {
        return value.length() + value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(DmoNamespace other) {
        return this.value.compareTo(other.value);
    }

}
