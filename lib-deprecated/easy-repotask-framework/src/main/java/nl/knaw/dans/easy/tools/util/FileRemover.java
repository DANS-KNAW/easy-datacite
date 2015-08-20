package nl.knaw.dans.easy.tools.util;

import java.io.File;

public class FileRemover {
    private final File file;

    public FileRemover(final File file) {
        this.file = file;
    }

    public void remove() {
        remove(file);
    }

    private static void remove(final File file) {
        if (file.isDirectory()) {
            removeFilesFromDirectory(file);
        }

        file.delete();
    }

    private static void removeFilesFromDirectory(final File dir) {
        final File[] files = dir.listFiles();

        for (final File f : files) {
            remove(f);
        }
    }
}
