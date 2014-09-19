package nl.knaw.dans.common.lang.repo.collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.InvalidContainerItemException;
import nl.knaw.dans.common.lang.repo.exception.NoStoreAttachedException;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;

public abstract class AbstractDmoContainer extends AbstractDmoCollectionMember implements DmoContainer {
    private static final long serialVersionUID = 484270842981828080L;

    public static String CONTENTMODEL = "dans-container-v1";

    private List<DmoContainerItem> removedChildren = new ArrayList<DmoContainerItem>();

    private List<DmoContainerItem> addedChildren = new ArrayList<DmoContainerItem>();

    private List<DmoContainerItem> loadedChildrenCache = null;

    public AbstractDmoContainer(String storeId) {
        super(storeId);
    }

    /**
     * this helps with faking multiple inheritance in recursive item
     */
    protected DmoContainer getThisDmo() {
        return this;
    }

    @Override
    public Set<String> getContentModels() {
        Set<String> contentModels = super.getContentModels();
        contentModels.add(CONTENTMODEL);
        return contentModels;
    }

    public void addChild(DmoContainerItem item) throws RepositoryException {
        checkDmoCompatible(item);

        tryAttachToUnitOfWork(item);

        // add to added children list
        addedChildren.add(item);

        // just in case
        removedChildren.remove(item);

        item.addParent(getThisDmo());
    }

    public Set<DmoStoreId> getChildSids() throws RepositoryException {
        HashSet<DmoStoreId> resultSet = new HashSet<DmoStoreId>();

        // get child sids from store
        if (getThisDmo().isLoaded())
            resultSet.addAll(getLoadedChildSids());

        // added children
        for (DmoContainerItem item : addedChildren) {
            DmoStoreId dmoStoreId = item.getDmoStoreId();
            if (dmoStoreId != null)
                resultSet.add(dmoStoreId);
        }

        return resultSet;
    }

    protected Set<DmoStoreId> getLoadedChildSids() throws RepositoryException, NoStoreAttachedException {
        // query store for child relations
        List<Relation> childRelations = getThisDmo().getStore().getRelations(null, RelsConstants.DANS_NS.IS_MEMBER_OF.toString(), getThisDmo().getStoreId());

        Set<DmoStoreId> loadedChildSids = new HashSet<DmoStoreId>(childRelations.size());
        for (Relation childRelation : childRelations) {
            // filter out removed children
            for (DmoContainerItem removedChild : removedChildren) {
                if (removedChild.getStoreId() != null && removedChild.getStoreId().equals(childRelation.subject))
                    continue;
            }
            // add to cache
            loadedChildSids.add(new DmoStoreId(childRelation.subject));
        }

        return loadedChildSids;
    }

    private List<DmoContainerItem> getLoadedChildren() throws RepositoryException {
        // check if cached children are still validated
        boolean childInvalidated = false;
        if (loadedChildrenCache != null) {
            for (DmoContainerItem loadedChild : loadedChildrenCache) {
                if (loadedChild.isInvalidated()) {
                    // invalidate cache if one of the children got invalidated
                    childInvalidated = true;
                    break;
                }
            }
        }

        if (loadedChildrenCache == null || childInvalidated) {
            Set<DmoStoreId> loadedChildSids = getLoadedChildSids();
            List<DmoContainerItem> loadedChildren = new ArrayList<DmoContainerItem>(loadedChildSids.size());

            for (DmoStoreId childSid : loadedChildSids) {
                // check if the unit of work has a copy (does not need to be validated, because
                // it might be part of a transaction that will ignore invalidation)
                DmoContainerItem uowChild = (DmoContainerItem) tryGetObjectFromUnitOfWork(childSid);
                if (uowChild != null) {
                    loadedChildren.add(uowChild);
                    continue;
                }

                // check if we still have a validated copy in the added children list
                for (DmoContainerItem addedChild : addedChildren) {
                    if (addedChild.getDmoStoreId().equals(childSid)) {
                        loadedChildren.add(addedChild);
                        continue;
                    }
                }

                // check if we still have a validated copy of the object in the old cache
                if (loadedChildrenCache != null) {
                    DmoContainerItem validCachedChild = null;
                    for (DmoContainerItem cachedChild : loadedChildrenCache) {
                        if (!cachedChild.isInvalidated() && cachedChild.getDmoStoreId().equals(childSid)) {
                            validCachedChild = cachedChild;
                            break;
                        }
                    }

                    if (validCachedChild != null) {
                        loadedChildren.add(validCachedChild);
                        continue;
                    }
                }

                // if all else fails then try to get the child from the store
                DataModelObject dmo = getThisDmo().getStore().retrieve(childSid);
                if (!(dmo instanceof DmoContainerItem))
                    throw new InvalidContainerItemException("Data Model Object " + dmo.toString() + " is not a container item");

                loadedChildren.add((DmoContainerItem) dmo);

                // attach to unit of work
                tryAttachToUnitOfWork(dmo);
            }

            // remove the loaded children from the added children (added children must have been
            // comitted)
            for (DmoContainerItem loadedChild : loadedChildren) {
                removeFromList(addedChildren, loadedChild);
            }

            loadedChildrenCache = loadedChildren;
        }

        return loadedChildrenCache;
    }

    public List<? extends DmoContainerItem> getChildren() throws RepositoryException {
        List<DmoContainerItem> resultList = new ArrayList<DmoContainerItem>();

        // get loaded children from the store
        if (isLoaded())
            resultList.addAll(getLoadedChildren());

        // add added children
        resultList.addAll(addedChildren);

        return resultList;
    }

    @SuppressWarnings("unchecked")
    public void setChildren(List<? extends DmoContainerItem> children) throws RepositoryException {
        addedChildren = (List<DmoContainerItem>) children;

        for (DmoContainerItem item : addedChildren) {
            tryAttachToUnitOfWork(item);
        }
    }

    public void removeChild(DmoContainerItem item) throws RepositoryException {
        removedChildren.add(item);

        // remove from added children if it is the same object or they share the same not null sid
        removeFromList(addedChildren, item);

        item.removeParent(getThisDmo());
    }

    private void removeFromList(List<DmoContainerItem> list, DmoContainerItem item) {
        Iterator<DmoContainerItem> listIt = list.iterator();
        while (listIt.hasNext()) {
            DmoContainerItem child = listIt.next();
            if (item.getStoreId().equals(child.getStoreId()))
                listIt.remove();
        }
    }

    public void removeAndDeleteChild(DmoContainerItem item) throws RepositoryException {
        removeChild(item);
        item.registerDeleted();
    }

}
