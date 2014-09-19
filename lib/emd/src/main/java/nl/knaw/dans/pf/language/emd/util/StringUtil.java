package nl.knaw.dans.pf.language.emd.util;

import java.util.*;

public final class StringUtil {

    public static String firstCharToUpper(final String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String commaSeparatedList(Collection<?> objects) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> iter = objects.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
            sb.append(", ");
        }
        if (sb.length() > 2) {
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String printStackTrace(StackTraceElement[] stes, String filter) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : stes) {
            if (filter == null || ste.getClassName().startsWith(filter)) {
                sb.append("\n\t").append("at ").append(ste.getClassName()).append(" (").append(ste.getFileName()).append(":").append(ste.getLineNumber())
                        .append(")");
            }

        }
        return sb.toString();
    }

}
