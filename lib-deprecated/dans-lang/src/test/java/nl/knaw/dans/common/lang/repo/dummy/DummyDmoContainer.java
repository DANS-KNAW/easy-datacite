package nl.knaw.dans.common.lang.repo.dummy;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.collections.AbstractDmoContainer;
import nl.knaw.dans.common.lang.repo.collections.DmoCollection;

public class DummyDmoContainer extends AbstractDmoContainer {
    private static final long serialVersionUID = -7282653435250070438L;
    public static final DmoNamespace NAMESPACE = new DmoNamespace("dummy-container");

    public DummyDmoContainer(String storeId) {
        super(storeId);
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
