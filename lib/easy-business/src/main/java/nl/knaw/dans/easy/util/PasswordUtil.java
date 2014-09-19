package nl.knaw.dans.easy.util;

public class PasswordUtil {

    public static final char[] SPECIAL_CHARS = {'!', '#', '$', '%', '&', '*', '@'};
    public static final char[] DIGITS = {'2', '3', '4', '5', '6', '7', '8'};
    public static final char[] LOWER = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'k', 'm', 'n', 'p', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    public static final char[] UPPER = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'K', 'M', 'N', 'P', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static final int LENGTH = 8;

    public static String newPassword() {
        char[] word = new char[LENGTH];
        word[0] = getSpecialChar();
        word[1] = getDigit();
        word[2] = getLower();
        word[3] = getUpper();
        word[4] = getLower();
        word[5] = getUpper();
        word[6] = getLower();
        word[7] = getUpper();
        for (int i = 0; i < 10; i++) {
            int from = (int) (Math.random() * ((double) LENGTH));
            int to = (int) (Math.random() * ((double) LENGTH));
            char c = word[to];
            word[to] = word[from];
            word[from] = c;
        }
        return new String(word);
    }

    private static char getSpecialChar() {
        int rnd = (int) (Math.random() * ((double) SPECIAL_CHARS.length));
        return SPECIAL_CHARS[rnd];
    }

    private static char getDigit() {
        int rnd = (int) (Math.random() * ((double) DIGITS.length));
        return DIGITS[rnd];
    }

    private static char getLower() {
        int rnd = (int) (Math.random() * ((double) LOWER.length));
        return LOWER[rnd];
    }

    private static char getUpper() {
        int rnd = (int) (Math.random() * ((double) UPPER.length));
        return UPPER[rnd];
    }

}
