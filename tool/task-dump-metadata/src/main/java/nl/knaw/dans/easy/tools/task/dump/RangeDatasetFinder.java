package nl.knaw.dans.easy.tools.task.dump;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.easy.tools.Application;
import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;

class RangeDatasetFinder implements DatasetFinder {
    private static final String PID_KEY = "pid";

    private int from;
    private int to;
    private FieldSearchQuery query;
    private FieldSearchResult result;

    RangeDatasetFinder from(int from) {
        this.from = from;
        return this;
    }

    RangeDatasetFinder to(int to) {
        this.to = to;
        return this;
    }

    public ObjectFields next() {
        do {
            buildQueryForNextDatasetNumber();
            tryQuery();

            if (from > to) {
                return null;
            }

        } while (result.getResultList() == null || result.getResultList().length == 0);

        return result.getResultList()[0];
    }

    private void buildQueryForNextDatasetNumber() {
        query = new FieldSearchQuery(new Condition[] {new Condition(PID_KEY, ComparisonOperator.eq, String.format("easy-dataset:%d", from++))}, null);
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
