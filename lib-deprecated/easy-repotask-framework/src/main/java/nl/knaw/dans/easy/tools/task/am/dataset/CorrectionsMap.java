package nl.knaw.dans.easy.tools.task.am.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;

public class CorrectionsMap<T extends Enum<T>> {
    private Map<String, Map<String, Enum<T>>> corrections = new HashMap<String, Map<String, Enum<T>>>();
    private Enum<T> enumeration;

    CorrectionsMap(File correctionsFile, Enum<T> enumeration) throws FatalTaskException {
        this.enumeration = enumeration;
        fillCorrectionsMap(correctionsFile);
    }

    private void fillCorrectionsMap(File correctionsFile) throws FatalTaskException {
        BufferedReader reader = openFileForLineReading(correctionsFile);
        String line;

        while ((line = readLine(reader)) != null) {
            addEntryToCorrectionsMap(line);
        }
    }

    private BufferedReader openFileForLineReading(File file) {
        try {
            return new BufferedReader(new FileReader(file));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("Corrections file could not be read: %s", e.getMessage()), e);
        }
    }

    private String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void addEntryToCorrectionsMap(String line) {
        String[] record = line.split("@@@", 3);
        getCorrectionsForDataset(record[0]).put(record[1], (Enum<T>) Enum.valueOf(enumeration.getClass(), record[2]));
    }

    private Map<String, Enum<T>> getCorrectionsForDataset(String aipId) {
        if (!corrections.containsKey(aipId)) {
            corrections.put(aipId, new HashMap<String, Enum<T>>());
        }

        return corrections.get(aipId);
    }

    public boolean contains(String aipId) {
        return corrections.containsKey(aipId);
    }

    public boolean containsPath(String aipId, String path) {
        return getCorrection(aipId, path) != null;
    }

    public Enum<T> getCorrection(String aipId, String path) {
        if (!contains(aipId)) {
            return null;
        }

        Map<String, Enum<T>> correctionsForDataset = corrections.get(aipId);
        return correctionsForDataset.get(path);
    }
}
