package nl.knaw.dans.easy.domain.dataset;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.collections.AbstractDmoContainerItem;
import nl.knaw.dans.common.lang.repo.collections.DmoCollection;
import nl.knaw.dans.common.lang.repo.collections.DmoContainer;
import nl.knaw.dans.common.lang.repo.collections.DmoContainerItem;
import nl.knaw.dans.common.lang.repo.collections.DmoContainerItemRelations;
import nl.knaw.dans.common.lang.repo.exception.NoUnitOfWorkAttachedException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.DomainRuntimeException;
import nl.knaw.dans.easy.domain.model.DatasetItem;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;

public abstract class AbstractDatasetItemImpl extends AbstractDmoContainerItem implements DatasetItem, DmoContainerItem {
    private static final long serialVersionUID = -7007411478967909724L;

    public AbstractDatasetItemImpl(String storeId) {
        super(storeId);
        getDatasetItemMetadata().setDmoStoreId(new DmoStoreId(storeId));
    }

    public Set<DmoCollection> getCollections() {
        HashSet<DmoCollection> c = new HashSet<DmoCollection>();
        c.add(DatasetItemCollection.getInstance());
        return c;
    }

    public DmoStoreId getDatasetId() {
        return getDatasetItemMetadata().getDatasetDmoStoreId();
    }

    public void setDatasetId(DmoStoreId datasetId) {
        getDatasetItemMetadata().setDatasetDmoStoreId(datasetId);
        // add relation to dataset.
        DmoContainerItemRelations relations = (DmoContainerItemRelations) getRelations();
        relations.setSubordinateTo(datasetId);
    }

    public String getPath() {
        String path = getDatasetItemMetadata().getPath();
        return path == null ? getLabel() : path;
    }

    public void setParent(DatasetItemContainer parent) throws DomainException {
        try {
            super.setParent((DmoContainer) parent);

            onParentChanged(parent);

        }
        catch (RepositoryException e) {
            throw new DomainException(e);
        }
    }

    public void onParentChanged(DatasetItemContainer parent) throws NoUnitOfWorkAttachedException, RepositoryException {
        if (parent instanceof DatasetItem) {
            DatasetItem datasetItem = (DatasetItem) parent;
            getDatasetItemMetadata().setPath(datasetItem.getPath() + "/" + getLabel());
        }
        getDatasetItemMetadata().setParentDmoStoreId(parent.getDmoStoreId());
    }

    @Override
    public boolean isDescendantOf(DmoStoreId dmoStoreId) {
        return dmoStoreId != null
                && (dmoStoreId.equals(getDatasetItemMetadata().getDatasetDmoStoreId()) || dmoStoreId.equals(getDatasetItemMetadata().getParentDmoStoreId()));
    }

    @Override
    public boolean isDescendantOf(DataModelObject dmo) {
        return dmo != null && isDescendantOf(dmo.getDmoStoreId());
    }

    @Override
    public DmoContainer getParent() {
        try {
            return super.getParent();
        }
        catch (RepositoryException e) {
            throw new DomainRuntimeException(e);
        }
    }

}
