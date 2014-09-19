package nl.knaw.dans.common.lang.repo.dummy;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.collections.AbstractDmoContainerItem;
import nl.knaw.dans.common.lang.repo.collections.DmoCollection;

public class DummyDmoContainerItem extends AbstractDmoContainerItem {
    public static final DmoNamespace NAMESPACE = new DmoNamespace("dummy-item");
    /**
     * 
     */
    private static final long serialVersionUID = -6930344805740878231L;

    public DummyDmoContainerItem(String storeId) {
        super(storeId);
    }

    public boolean canHaveMultipleParents() {
        return true;
    }

    public DmoNamespace getDmoNamespace() {
        return NAMESPACE;
    }

    public boolean isDeletable() {
        return true;
    }

    public Set<DmoCollection> getCollections() {
        Set<DmoCollection> c = new HashSet<DmoCollection>();
        c.add(DummyDmoCollection.getInstance());
        return c;
    }

}
