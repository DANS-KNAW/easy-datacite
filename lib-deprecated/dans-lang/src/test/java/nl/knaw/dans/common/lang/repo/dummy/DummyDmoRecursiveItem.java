package nl.knaw.dans.common.lang.repo.dummy;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.collections.AbstractDmoRecursiveItem;
import nl.knaw.dans.common.lang.repo.collections.DmoCollection;

public class DummyDmoRecursiveItem extends AbstractDmoRecursiveItem {

    public static final DmoNamespace NAMESPACE = new DmoNamespace("dummy-ritem");
    /**
     * 
     */
    private static final long serialVersionUID = 6421940487982233884L;

    public DummyDmoRecursiveItem(String storeId) {
        super(storeId);
    }

    public DmoNamespace getDmoNamespace() {
        return NAMESPACE;
    }

    public boolean isDeletable() {
        return true;
    }

    public boolean canHaveMultipleParents() {
        return true;
    }

    public Set<DmoCollection> getCollections() {
        Set<DmoCollection> c = new HashSet<DmoCollection>();
        c.add(DummyDmoCollection.getInstance());
        return c;
    }

}
