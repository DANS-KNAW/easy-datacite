package nl.knaw.dans.easy.tools.task.dump;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.easy.tools.Application;

import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;

public class ListDatasetFinder implements DatasetFinder {
    private static final String PID_KEY = "pid";

    private List<Integer> sidNumbers = new LinkedList<Integer>();
    private FieldSearchQuery query;
    private FieldSearchResult result;
    private int index;

    ListDatasetFinder(File listFile) {
        readSidNumbersFrom(listFile);
    }

    private void readSidNumbersFrom(File listFile) {
        BufferedReader reader = openForLineReading(listFile);
        String line;

        while ((line = readLine(reader)) != null) {
            sidNumbers.add(Integer.parseInt(line.substring(line.indexOf(':') + 1)));
        }
    }

    private BufferedReader openForLineReading(File file) {
        try {
            return new BufferedReader(new FileReader(file));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("Cannot read the list of datasets: '%s'", file), e);
        }
    }

    private String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException("Could not read line from dataset list file", e);
        }
    }

    @Override
    public ObjectFields next() {
        if (index < sidNumbers.size()) {
            buildQueryForNextDatasetNumber();
            tryQuery();

            return result.getResultList()[0];
        }

        return null;
    }

    private void buildQueryForNextDatasetNumber() {
        query = new FieldSearchQuery(
                new Condition[] {new Condition(PID_KEY, ComparisonOperator.eq, String.format("easy-dataset:%d", sidNumbers.get(index++)))}, null);
    }

    private void tryQuery() {
        try {
            result = Application.getFedora().getObjectAccessor().findObjects(new String[] {PID_KEY}, 10, query);
        }
        catch (final RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

}
