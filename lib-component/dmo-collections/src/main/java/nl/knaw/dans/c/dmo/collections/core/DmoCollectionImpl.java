package nl.knaw.dans.c.dmo.collections.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.ApplicationException;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.common.lang.repo.exception.NoUnitOfWorkAttachedException;
import nl.knaw.dans.common.lang.repo.relations.Relations;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmoCollectionImpl extends AbstractDataModelObject implements DmoCollection, Observer {

    private static final long serialVersionUID = -1334420292881968002L;

    private static final Logger logger = LoggerFactory.getLogger(DmoCollectionImpl.class);

    private final DmoNamespace dmoNamespace;
    private final List<DmoCollection> children = new ArrayList<DmoCollection>();
    private DmoCollectionImpl parent;
    private JiBXDublinCoreMetadata dcMetadata;

    public DmoCollectionImpl(DmoStoreId dmoStoreId) {
        super(dmoStoreId);
        this.dmoNamespace = dmoStoreId.getNamespace();
        init();
    }

    private void init() {
        setDcMetadata(new JiBXDublinCoreMetadata());
        setState("Active");
        setOwnerId("FedoraAdmin");
        getDcMetadata().addDate(new DateTime().toString());
        getDcMetadata().addIdentifier(getStoreId());
    }

    @Override
    public DmoNamespace getDmoNamespace() {
        return dmoNamespace;
    }

    @Override
    public void setLabel(String label) {
        // label is updated by observable.notify. see #update(Observable, Object)
        getDcMetadata().set(PropertyName.Title, label);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof JiBXDublinCoreMetadata) {
            update((JiBXDublinCoreMetadata) o, ((PropertyName) arg));
        }
    }

    private void update(JiBXDublinCoreMetadata jibxDcMetadata, PropertyName propName) {
        if (PropertyName.Title.equals(propName)) {
            super.setLabel(jibxDcMetadata.getFirst(PropertyName.Title));
        }
    }

    @Override
    public Set<String> getContentModels() {
        Set<String> contentModels = super.getContentModels();
        contentModels.add(CONTENT_MODEL);
        return contentModels;
    }

    @Override
    public boolean isDeletable() {
        return !hasParent() && !hasChildren();
    }

    // in stead of using generics, return a typed relations object.
    @Override
    public DmoCollectionRelations getRelations() {
        return (DmoCollectionRelations) super.getRelations();
    }

    @Override
    protected Relations newRelationsObject() {
        return new DmoCollectionRelations(this);
    }

    public DublinCoreMetadata getDcMetadata() {
        return dcMetadata;
    }

    /**
     * NO PUBLIC METHOD, used for deserialisation.
     * 
     * @param jdc
     *        dcmd to set.
     */
    public void setDcMetadata(JiBXDublinCoreMetadata jdc) {
        if (dcMetadata != null) {
            dcMetadata.deleteObserver(this);
        }
        dcMetadata = jdc;
        dcMetadata.addObserver(this);
    }

    @Override
    public List<MetadataUnit> getMetadataUnits() {
        List<MetadataUnit> metadataUnits = super.getMetadataUnits();
        metadataUnits.add(getDcMetadata());
        return metadataUnits;
    }

    @Override
    public boolean isPublishedAsOAISet() {
        return getRelations().hasOAISetRelation();
    }

    public void publishAsOAISet() {
        DmoCollectionImpl parent = getParent();
        if (parent != null) {
            parent.publishAsOAISet();
        }
        String setSpec = createOAISetSpec(getOAISetElement());
        getRelations().addOAISetRelation(setSpec, getLabel());
    }

    public void unpublishAsOAISet() {
        for (DmoCollection child : getChildren()) {
            ((DmoCollectionImpl) child).unpublishAsOAISet();
        }
        getRelations().removeOAISetRelation();
    }

    protected String createOAISetSpec(String setElements) {
        DmoCollectionImpl parent = getParent();
        if (parent != null) {
            setElements = parent.createOAISetSpec(parent.getOAISetElement() + ":" + setElements);
        }
        return setElements;
    }

    protected String getOAISetElement() {
        return isRoot() ? getDmoNamespace().getValue() : getDmoStoreId().getId();
    }

    @Override
    public String getShortName() {
        return getRelations().getShortName();
    }

    @Override
    public void setShortName(String shortName) {
        getRelations().addShortName(shortName);
    }

    @Override
    public DmoCollectionImpl getParent() {
        DmoStoreId parentId = getParentId();
        if (parentId != null && parent == null) {
            parent = (DmoCollectionImpl) getObject(parentId);
        }
        return parent;
    }

    @Override
    public List<DmoCollection> getChildren() {
        List<DmoStoreId> childIds = getChildIds();
        if (childIds.size() != children.size()) {
            children.clear();
            for (DmoStoreId dmoStoreId : childIds) {
                DmoCollectionImpl kid = (DmoCollectionImpl) getObject(dmoStoreId);
                kid.parent = this;
                children.add(kid);
            }
        }
        return Collections.unmodifiableList(children);
    }

    @Override
    public boolean isRoot() {
        DmoStoreId dmoStoreId = getDmoStoreId();
        return !hasParent() && dmoStoreId != null && ROOT_ID.equals(dmoStoreId.getId());
    }

    @Override
    public boolean isLeaf() {
        return getChildIds().isEmpty();
    }

    @Override
    public boolean hasParent() {
        return getParentId() != null;
    }

    @Override
    public DmoStoreId getParentId() {
        return getRelations().getParentId();
    }

    @Override
    public boolean hasChildren() {
        return !getChildIds().isEmpty();
    }

    @Override
    public List<DmoStoreId> getChildIds() {
        return getRelations().getChildIds();
    }

    public DmoCollectionImpl addChild(DmoCollection child) throws CollectionsException {
        if (getStoreId().equals(child.getStoreId())) {
            throw new CollectionsException("Cannot add child that is me!");
        }
        if (!getDmoNamespace().equals(child.getDmoNamespace())) {
            throw new CollectionsException("Cannot add child with a different namespace." + " My namespace=" + getDmoNamespace() + ", child namespace="
                    + child.getDmoNamespace());
        }
        if (child.isPublishedAsOAISet()) {
            throw new CollectionsException("Cannot add a child that is published as OAI set.");
        }
        DmoCollectionImpl childImpl;
        if (child instanceof DmoCollectionImpl) {
            childImpl = (DmoCollectionImpl) child;
        } else {
            throw new CollectionsException("Child is not a " + DmoCollectionImpl.class.getName());
        }
        childImpl.setParent(this);
        children.add(child);
        getRelations().addChild(child);
        return childImpl;
    }

    private void setParent(DmoCollectionImpl parent) throws CollectionsException {
        if (!hasParent() && !isRoot()) {
            this.parent = parent;
            getRelations().setParent(parent);
        } else if (hasParent()) {
            throw new CollectionsException("Cannot set parent. " + "Already got a parent. parentId=" + getParentId() + " " + this);
        } else if (isRoot()) {
            throw new CollectionsException("Root of collection cannot be set as a child.");
        }
    }

    public void removeChild(DmoCollection child) throws CollectionsException {
        DmoCollectionImpl childImpl;
        if (child instanceof DmoCollectionImpl) {
            childImpl = (DmoCollectionImpl) child;
        } else {
            throw new CollectionsException("Child is not a " + DmoCollectionImpl.class.getName());
        }

        childImpl.removeParent(this);
        children.remove(child);
        getRelations().removeChild(child);
    }

    @Override
    public boolean isOAIendNode(Set<DmoStoreId> memberIds) {
        // removeAll "Returns true if this list changed as a result of the call.." (javadoc
        // java.util.List)
        return !getOAIPublishedDescendentIds().removeAll(memberIds);
    }

    @Override
    public List<DmoCollection> getDescendants() {
        List<DmoCollection> descendants = new ArrayList<DmoCollection>();
        descendants.addAll(getChildren());
        for (DmoCollection child : getChildren()) {
            descendants.addAll(child.getDescendants());
        }
        return descendants;
    }

    @Override
    public List<DmoStoreId> getDescendantIds() {
        List<DmoStoreId> descendantIds = new ArrayList<DmoStoreId>();
        descendantIds.addAll(getChildIds());
        for (DmoCollection child : getChildren()) {
            descendantIds.addAll(child.getDescendantIds());
        }
        return descendantIds;
    }

    @Override
    public List<DmoStoreId> getOAIPublishedDescendentIds() {
        List<DmoStoreId> descendantIds = new ArrayList<DmoStoreId>();
        for (DmoCollection child : getChildren()) {
            if (child.isPublishedAsOAISet()) {
                descendantIds.add(child.getDmoStoreId());
                descendantIds.addAll(child.getOAIPublishedDescendentIds());
            }
        }
        return descendantIds;
    }

    private void removeParent(DmoCollection parent) throws CollectionsException {
        if (parent.getStoreId().equals(getParentId())) {
            this.parent = null;
            getRelations().removeParent(parent);
            unpublishAsOAISet();
            logger.debug("[" + getStoreId() + "] removed parent " + parent);
        } else {
            throw new CollectionsException("Parent '" + parent.getStoreId() + "' cannot be removed, because my parent is '" + getParentId() + ". " + this);
        }
    }

    private DmoCollection getObject(DmoStoreId dmoStoreId) {
        DmoCollection dmoCollection = null;
        try {
            UnitOfWork uow = getUnitOfWork();
            dmoCollection = (DmoCollection) uow.retrieveObject(dmoStoreId);
        }
        catch (NoUnitOfWorkAttachedException e) {
            throw new IllegalStateException(e);
        }
        catch (RepositoryException e) {
            throw new ApplicationException(e);
        }
        return dmoCollection;
    }

}
