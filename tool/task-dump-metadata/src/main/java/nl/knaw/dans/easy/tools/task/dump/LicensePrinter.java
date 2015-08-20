package nl.knaw.dans.easy.tools.task.dump;

import java.util.List;

public class LicensePrinter {
    private final List<LicenseData> licenses;

    public LicensePrinter(List<LicenseData> licenses) {
        this.licenses = licenses;
    }

    private String property(String key, String value) {
        return String.format("LICENSE_DATA:%s=%s\n", key, new CrlfEscapedString(value));
    }

    public String toString() {
        StringBuilder s = new StringBuilder();

        for (LicenseData l : licenses) {
            s.append(property("SHA-1", l.getSha1Hash()));
            s.append(property("label", l.getLabel()));
        }

        return s.toString();
    }
}
