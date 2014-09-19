package nl.knaw.dans.common.lang.util;

public class Pluralizer {
    private static final String TO_BE_S = "is";
    private static final String TO_BE_P = "are";

    public static String format(String pre, String verbS, String verbP, int count, String nounS, String nounP, String post) {
        StringBuilder sb = new StringBuilder();
        if (pre != null) {
            sb.append(pre).append(" ");
        }
        if (count == 1) {
            if (verbS != null) {
                sb.append(verbS).append(" ");
            }
            sb.append(count).append(" ").append(nounS);
        } else {
            if (verbP != null) {
                sb.append(verbP).append(" ");
            }
            sb.append(count).append(" ").append(nounP);
        }
        if (post != null) {
            sb.append(" ").append(post);
        }

        return sb.toString();
    }

    public static String format(String pre, int count, String noun, String post) {
        return format(pre, null, null, count, noun, noun + "s", post);
    }

    public static String formatToBe(String pre, int count, String noun, String post) {
        return format(pre, TO_BE_S, TO_BE_P, count, noun, noun + "s", post);
    }

}
