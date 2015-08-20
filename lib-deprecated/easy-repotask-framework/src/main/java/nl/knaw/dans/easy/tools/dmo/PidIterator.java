package nl.knaw.dans.easy.tools.dmo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.easy.tools.Application;

public class PidIterator {

    private static final String PID_KEY = "pid";
    private static final int DEFAULT_MAX_RESULTS = 10;

    private final DmoNamespace objectNamespace;

    private Iterator<String> idIterator;
    private String token;
    private int maxResults;
    private boolean searchInitiated;

    public PidIterator(DmoNamespace objectNamespace) {
        this.objectNamespace = objectNamespace;
    }

    public int getMaxResults() {
        if (maxResults < 1) {
            maxResults = DEFAULT_MAX_RESULTS;
        }
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public boolean hasNext() throws RepositoryException {
        if (idIterator == null || !idIterator.hasNext()) {
            getNextIdsFromRepo();
        }
        return idIterator.hasNext();
    }

    public String next() throws RepositoryException {
        if (idIterator == null || !idIterator.hasNext()) {
            getNextIdsFromRepo();
        }
        return idIterator.next();
    }

    private void getNextIdsFromRepo() throws RepositoryException {
        if (!searchInitiated || token != null) {
            searchInitiated = true;
            FieldSearchResult result = getFieldSearchResult();
            token = result.getListSession() == null ? null : result.getListSession().getToken();
            List<String> pids = new ArrayList<String>();
            for (ObjectFields of : result.getResultList()) {
                pids.add(of.getPid());
            }
            idIterator = pids.iterator();
        }
    }

    private FieldSearchResult getFieldSearchResult() throws RepositoryException {
        FieldSearchResult result;
        if (token == null) {
            FieldSearchQuery query = new FieldSearchQuery();
            Condition condition = new Condition(PID_KEY, ComparisonOperator.has, objectNamespace.getValue() + ":*");
            query.setConditions(new Condition[] {condition});
            result = Application.getFedora().getObjectAccessor().findObjects(new String[] {PID_KEY}, getMaxResults(), query);
        } else {
            result = Application.getFedora().getObjectAccessor().resumeFindObjects(token);
        }
        return result;
    }

}
