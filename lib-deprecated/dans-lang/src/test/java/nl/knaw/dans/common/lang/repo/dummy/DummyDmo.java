package nl.knaw.dans.common.lang.repo.dummy;

import nl.knaw.dans.common.lang.repo.AbstractDataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;

public class DummyDmo extends AbstractDataModelObject {
    private static final long serialVersionUID = -3565807304612555180L;
    public static final DmoNamespace NAMESPACE = new DmoNamespace("dummy-object");
    private long loadTime = 0;

    public DummyDmo(String storeId) {
        super(storeId);
    }

    public DmoNamespace getDmoNamespace() {
        return NAMESPACE;
    }

    public boolean isDeletable() {
        return true;
    }

    public void setLoadTime(long time) {
        loadTime = time;
    }

    @Override
    public long getloadTime() {
        if (loadTime != 0)
            return loadTime;
        else
            return super.getloadTime();
    }
}
