package nl.knaw.dans.common.lang.repo.collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.InvalidContainerException;
import nl.knaw.dans.common.lang.repo.exception.NoUnitOfWorkAttachedException;
import nl.knaw.dans.common.lang.repo.exception.ObjectIsNotPartOfCollection;
import nl.knaw.dans.common.lang.repo.relations.Relations;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDmoContainerItem extends AbstractDmoCollectionMember implements DmoContainerItem {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDmoContainerItem.class);
    private static final long serialVersionUID = 2083740106599096049L;

    public static String CONTENT_MODEL = "dans-container-item-v1";

    private List<DmoContainer> addedParents = new ArrayList<DmoContainer>();

    public AbstractDmoContainerItem(String storeId) {
        super(storeId);
    }

    @Override
    protected Relations newRelationsObject() {
        return new DmoContainerItemRelations(this);
    }

    @Override
    public Set<String> getContentModels() {
        Set<String> contentModels = super.getContentModels();
        contentModels.add(CONTENT_MODEL);
        return contentModels;
    }

    // ---- ADD

    public void addParent(DmoContainer container) throws RepositoryException {
        checkDmoCompatible(container);

        // store sid in relationships
        ((DmoContainerItemRelations) getRelations()).addParent(container.getDmoStoreId());

        // attach the object in the unit of work
        // tryAttachToUnitOfWork(container);

        // cache added parent
        addedParents.add(container);
    }

    public void addParentSid(DmoStoreId parentDmoStoreId) throws ObjectIsNotPartOfCollection, NoUnitOfWorkAttachedException, RepositoryException {
        checkSidCompatible(parentDmoStoreId);

        ((DmoContainerItemRelations) getRelations()).addParent(parentDmoStoreId);
    }

    // GET

    public Set<DmoStoreId> getParentSids() throws RepositoryException {
        return new HashSet<DmoStoreId>(((DmoContainerItemRelations) getRelations()).getParents());
    }

    private DmoContainer getParentObject(DmoStoreId parentDmoStoreId) throws RepositoryException {
        // try to get the object form the unit of work
        DmoContainer uowParent = (DmoContainer) tryGetObjectFromUnitOfWork(parentDmoStoreId);
        if (uowParent != null) {
            return uowParent;
        }

        // check if parent was added already
        for (DmoContainer addedParent : addedParents) {
            if (addedParent.getDmoStoreId().equals(parentDmoStoreId)) {
                return addedParent;
            }
        }

        if (isLoaded()) {
            // other wise get the object from the store
            DataModelObject dmo = getStore().retrieve(parentDmoStoreId);
            if (!(dmo instanceof DmoContainer))
                throw new InvalidContainerException("Object " + dmo + " is not a container object");

            // attach to unit of work
            tryAttachToUnitOfWork(dmo);

            return (DmoContainer) dmo;
        } else
            return null;
    }

    public List<? extends DmoContainer> getParents() throws RepositoryException {
        Set<DmoStoreId> parentSids = getParentSids();
        List<DmoContainer> resultList = new ArrayList<DmoContainer>();

        for (DmoStoreId parentSid : parentSids) {
            DmoContainer dmo = getParentObject(parentSid);
            if (dmo != null)
                resultList.add(dmo);
            else
                throw new InvalidContainerException("Could not find object with sid " + parentSid);
        }

        return resultList;
    }

    public DmoContainer getParent() throws RepositoryException {
        DmoStoreId parentSid = getParentSid();
        if (getParentSid() != null && !StringUtils.isBlank(getParentSid().getId()))
            return getParentObject(parentSid);
        else
            return null;
    }

    public DmoStoreId getParentSid() throws RepositoryException {
        Set<DmoStoreId> parents = getParentSids();
        return parents.size() > 0 ? parents.iterator().next() : null;
    }

    // ---- SET

    private void clear() {
        ((DmoContainerItemRelations) getRelations()).clearParents();
        addedParents.clear();
    }

    public void setParent(DmoContainer container) throws RepositoryException {
        clear();
        addParent(container);
    }

    public void setParentSid(DmoStoreId parentDmoStoreId) throws RepositoryException {
        clear();

        addParentSid(parentDmoStoreId);
        Set<DmoStoreId> parentSids = new HashSet<DmoStoreId>(1);
        parentSids.add(parentDmoStoreId);
        setParentSids(parentSids);
    }

    public void setParentSids(Set<DmoStoreId> parentSids) throws RepositoryException {
        clear();

        for (DmoStoreId parentSid : parentSids)
            addParentSid(parentSid);
    }

    public void setParents(List<? extends DmoContainer> parents) throws RepositoryException {
        clear();

        for (DmoContainer parent : parents) {
            addParent(parent);
        }
    }

    // ---- REMOVE

    public void removeParent(DmoContainer container) throws RepositoryException {
        // remove from added parents
        Iterator<DmoContainer> apIt = addedParents.iterator();
        while (apIt.hasNext()) {
            DmoContainer addedParent = apIt.next();
            if (container == addedParent || addedParent.getStoreId().equals(container.getStoreId())) {
                apIt.remove();
                break;
            }
        }

        // remove from relations
        removeParentSid(container.getDmoStoreId());
    }

    public void removeParentSid(DmoStoreId parentDmoStoreId) throws RepositoryException {
        ((DmoContainerItemRelations) getRelations()).removeParent(parentDmoStoreId);
    }

}
