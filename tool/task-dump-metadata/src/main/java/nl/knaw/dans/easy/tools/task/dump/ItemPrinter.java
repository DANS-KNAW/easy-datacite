package nl.knaw.dans.easy.tools.task.dump;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class ItemPrinter {
    private final PrintWriter writer;
    private final String prefix;

    public ItemPrinter(final PrintWriter writer, final String prefix) {
        this.writer = writer;
        this.prefix = prefix;
    }

    public void printItems(final String key, final String... items) {
        printItems(key, Arrays.asList(items));
    }

    public void printItems(final String key, final List<String> items) {
        if (items == null) {
            return;
        }

        for (final String item : items) {
            writer.printf("%s:%s=%s\n", prefix, key, new CrlfEscapedString(item));
        }
    }
}
