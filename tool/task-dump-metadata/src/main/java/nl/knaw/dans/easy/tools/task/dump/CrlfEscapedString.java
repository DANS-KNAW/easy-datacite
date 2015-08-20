package nl.knaw.dans.easy.tools.task.dump;

public class CrlfEscapedString {
    private final String string;

    public CrlfEscapedString(String string) {
        this.string = string;
    }

    public String toString() {
        return string == null ? "" : string.replaceAll("\r", "").replaceAll("\n", "[LINEFEED CHARACTER]");
    }
}
