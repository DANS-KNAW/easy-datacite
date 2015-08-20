package nl.knaw.dans.easy.tools.dmo;

import java.util.NoSuchElementException;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;

public class DmoIterator<T extends DataModelObject> {

    private static final int DEFAULT_MAX_RESULTS = 10;

    private final DmoFilter<T>[] dmoFilters;
    private final PidIterator pidIterator;

    private int maxResults;
    private T nextDmo;

    @SuppressWarnings("unchecked")
    public DmoIterator(DmoNamespace objectNamespace) {
        this(objectNamespace, new DefaultDmoFilter<T>());
    }

    public DmoIterator(DmoNamespace objectNamespace, DmoFilter<T>... dmoFilters) {
        this.dmoFilters = dmoFilters;
        this.pidIterator = new PidIterator(objectNamespace);
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
        while (pidIterator.hasNext() && nextDmo == null) {
            T dmo = iteratorNext();
            for (DmoFilter<T> dmoFilter : dmoFilters) {
                if (dmoFilter.accept(dmo)) {
                    nextDmo = dmo;
                } else {
                    nextDmo = null;
                    break;
                }
            }
        }
        return nextDmo != null;
    }

    public T next() throws RepositoryException {
        if (nextDmo != null) {
            return shiftDmo();
        } else if (hasNext()) {
            return shiftDmo();
        } else {
            String filterClasses = "";
            for (DmoFilter<T> dmoFilter : dmoFilters) {
                filterClasses += "\n\t - " + dmoFilter.getClass().getName();
            }
            throw new NoSuchElementException("No next dmo that conforms to filter(s): " + filterClasses);
        }
    }

    private T shiftDmo() {
        T dmo = nextDmo;
        nextDmo = null;
        return dmo;
    }

    @SuppressWarnings("unchecked")
    private T iteratorNext() throws RepositoryException {
        return (T) Data.getEasyStore().retrieve(new DmoStoreId(pidIterator.next()));
    }

    private static class DefaultDmoFilter<T extends DataModelObject> implements DmoFilter<T> {

        @Override
        public boolean accept(T dmo) {
            return true;
        }

    }

}
