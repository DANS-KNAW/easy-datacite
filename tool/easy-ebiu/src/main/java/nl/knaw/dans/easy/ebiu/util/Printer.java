package nl.knaw.dans.easy.ebiu.util;

public class Printer {

    private static final String LINE = "------------------------------------------------------------------------";

    public static String format(String s) {
        return "\n" + LINE + "\n" + s + "\n" + LINE;
    }

    public static void println(String s) {
        System.out.println(format(s));
    }
}
