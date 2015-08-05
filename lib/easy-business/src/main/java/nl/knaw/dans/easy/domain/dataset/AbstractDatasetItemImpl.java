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
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.DomainRuntimeException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItem;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.DatasetItemMetadata;

public abstract class AbstractDatasetItemImpl extends AbstractDmoContainerItem implements DatasetItem, DmoContainerItem {
    private static final long serialVersionUID = -7007411478967909724L;
    private DmoStoreId datasetSid;
    private DmoStoreId parentSid;

    public AbstractDatasetItemImpl(String storeId) {
        super(storeId);
    }

    public Set<DmoCollection> getCollections() {
        HashSet<DmoCollection> c = new HashSet<DmoCollection>();
        c.add(DatasetItemCollection.getInstance());
        return c;
    }

    public DmoStoreId getDatasetId() {
        Set<Relation> rs = getRelations().getRelation("http://dans.knaw.nl/ontologies/relations#isSubordinateTo", null);
        for (Relation r : rs)
            datasetSid = new DmoStoreId(((String) r.getObject()).replace("info:fedora/", ""));
        return datasetSid;
    }

    public DmoStoreId getParentId() {
        Set<Relation> rs = getRelations().getRelation("http://dans.knaw.nl/ontologies/relations#isMemberOf", null);
        for (Relation r : rs)
            parentSid = new DmoStoreId(((String) r.getObject()).replace("info:fedora/", ""));
        return parentSid;
    }

    public void setDatasetId(DmoStoreId datasetId) {
        this.datasetSid = datasetId;
        // add relation to dataset.
        DmoContainerItemRelations relations = (DmoContainerItemRelations) getRelations();
        relations.setSubordinateTo(datasetId);
    }

    public String getPath() {
        return getDatasetItemMetadata().getPath() == null ? getLabel() : getDatasetItemMetadata().getPath();
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
        } else if (parent instanceof Dataset) {
            getDatasetItemMetadata().setPath(getLabel());
        }
        parentSid = parent.getDmoStoreId();
    }

    public abstract DatasetItemMetadata getDatasetItemMetadata();

    @Override
    public boolean isDescendantOf(DmoStoreId dmoStoreId) {
        return dmoStoreId != null && (dmoStoreId.equals(datasetSid) || dmoStoreId.equals(parentSid));
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
