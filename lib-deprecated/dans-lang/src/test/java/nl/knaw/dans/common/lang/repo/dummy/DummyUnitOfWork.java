package nl.knaw.dans.common.lang.repo.dummy;

import nl.knaw.dans.common.lang.repo.AbstractUnitOfWork;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStore;

public class DummyUnitOfWork extends AbstractUnitOfWork {
    private static final long serialVersionUID = -2376953145049383061L;
    private transient DmoStore store;

    public DummyUnitOfWork(DmoStore store) {
        super("dummyOwnerId");
        this.store = store;
    }

    public DummyUnitOfWork() {
        super("dummyOwnerId");
        this.store = new DummyDmoStore();
    }

    @Override
    public DmoStore getStore() {
        return store;
    }

    @Override
    protected String getIngestLogMessage(DataModelObject dmo) {
        return "Ingest by dummy unit of work " + dmo.toString();
    }

    @Override
    protected String getPurgeLogMessage(DataModelObject dmo) {
        return "Purged by dummy unit of work " + dmo.toString();
    }

    @Override
    protected String getUpdateLogMessage(DataModelObject dmo) {
        return "Updated by dummy unit of work " + dmo.toString();
    }

}
