package nl.knaw.dans.easy.tools.task.dump;

import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.DescriptiveMetadata;

public class DmdPrinter {
    private DescriptiveMetadata descriptiveMetadata;
    private String prefix = "";

    public DmdPrinter(DescriptiveMetadata dmd) {
        descriptiveMetadata = dmd;
    }

    public DmdPrinter withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    private String property(String key, String value) {
        return String.format("%sDMD:%s=%s\n", prefix, key, new CrlfEscapedString(value));
    }

    public String toString() {
        List<KeyValuePair> ps = descriptiveMetadata.getProperties();
        StringBuilder sb = new StringBuilder();

        for (KeyValuePair pair : ps) {
            sb.append(property(pair.getKey(), pair.getValue()));
        }

        return sb.toString();
    }

}
