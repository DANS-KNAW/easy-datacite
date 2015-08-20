package nl.knaw.dans.easy.tools.task.dump;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FileLineSorter {
    private final File file;
    private List<String> lines = new LinkedList<String>();

    public FileLineSorter(final File file) {
        this.file = file;
    }

    public void sort() {
        readLines();
        sortLines();
        writeLines();
    }

    private void readLines() {
        final BufferedReader br = createBufferedReader(file);

        String line = null;

        while ((line = readLine(br)) != null) {
            lines.add(line);
        }

        closeReader(br);
    }

    private static BufferedReader createBufferedReader(final File file) {
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        }
        catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readLine(final BufferedReader reader) {
        try {
            return reader.readLine();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void closeReader(final Reader reader) {
        try {
            reader.close();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sortLines() {
        Collections.sort(lines);
    }

    private void writeLines() {
        final FileWriter fw = createFileWriter(file);

        for (final String line : lines) {
            writeLine(fw, line + "\n");
        }

        closeWriter(fw);
    }

    private static FileWriter createFileWriter(final File file) {
        try {
            return new FileWriter(file);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeLine(final FileWriter writer, final String line) {
        try {
            writer.write(line);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void closeWriter(final Writer writer) {
        try {
            writer.close();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
