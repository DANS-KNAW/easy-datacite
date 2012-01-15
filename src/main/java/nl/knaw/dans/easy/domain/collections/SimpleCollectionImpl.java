package nl.knaw.dans.easy.domain.collections;

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
import nl.knaw.dans.common.lang.repo.DmoDecorator;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.common.lang.repo.exception.NoUnitOfWorkAttachedException;
import nl.knaw.dans.common.lang.repo.relations.Relations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCollectionImpl extends AbstractDataModelObject implements SimpleCollection, Observer
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -1334420292881968002L;
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleCollectionImpl.class);
    
    private final DmoDecorator dmoDecorator;
    private final List<SimpleCollection> children = new ArrayList<SimpleCollection>();
    private SimpleCollection parent;
    private JiBXDublinCoreMetadata dcMetadata;
    
    public SimpleCollectionImpl(String storeId, DmoDecorator dmoDecorator)
    {
        super(storeId);
        if (storeId == null || !storeId.startsWith(dmoDecorator.getObjectNamespace().getValue()))
        {
            throw new IllegalArgumentException("Invallid storeId: " + storeId);
        }
        this.dmoDecorator = dmoDecorator;
        setDcMetadata(new JiBXDublinCoreMetadata());
        setState("Active");
        setOwnerId("FedoraAdmin");
    }

    @Override
    public DmoNamespace getDmoNamespace()
    {
        return dmoDecorator.getObjectNamespace();
    }
    
    @Override
    public void setLabel(String label)
    {
        // label is updated by observable.notify. see #update(Observable, Object)
        getDcMetadata().set(PropertyName.Title, label);
    }
    
    @Override
    public void update(Observable o, Object arg)
    {
        if (o instanceof JiBXDublinCoreMetadata)
        {
            update((JiBXDublinCoreMetadata)o, ((PropertyName)arg));
        }
    }
    
    private void update(JiBXDublinCoreMetadata jibxDcMetadata, PropertyName propName)
    {
        if (PropertyName.Title.equals(propName))
        {
            super.setLabel(jibxDcMetadata.getFirst(PropertyName.Title));
        }
    }
    
    @Override
    public Set<String> getContentModels()
    {
        Set<String> contentModels =  super.getContentModels();
        contentModels.add(CONTENT_MODEL);
        return contentModels;
    }

    @Override
    public boolean isDeletable()
    {
        return !hasParent() && !hasChildren();
    }
    
    // in stead of using generics, return a typed relations object.
    @Override
    public SimpleCollectionRelations getRelations()
    {
        return (SimpleCollectionRelations) super.getRelations();
    }
    
    @Override
    protected Relations newRelationsObject()
    {
        return new SimpleCollectionRelations(this);
    }
    
    public DublinCoreMetadata getDcMetadata()
    {
        return dcMetadata;
    }
    
    /**
     * NO PUBLIC METHOD, used for deserialisation.
     * @param jdc dcmd to set.
     */
    public void setDcMetadata(JiBXDublinCoreMetadata jdc)
    {
        if (dcMetadata != null)
        {
            dcMetadata.deleteObserver(this);
        }
        dcMetadata = jdc;
        dcMetadata.addObserver(this);
    }
    
    @Override
    public List<MetadataUnit> getMetadataUnits()
    {
        List<MetadataUnit> metadataUnits = super.getMetadataUnits();
        metadataUnits.add(getDcMetadata());
        return metadataUnits;
    }

    @Override
    public boolean isPublishedAsOAISet()
    {
        return getRelations().hasOAISetRelation();
    }
    
    public void publishAsOAISet()
    {
        SimpleCollection parent = getParent();
        if (parent != null)
        {
            parent.publishAsOAISet();
        }
        String setSpec = createOAISetSpec(getOAISetElement());
        getRelations().addOAISetRelation(setSpec, getLabel());
    }
    
    public void unpublishAsOAISet()
    {
        for (SimpleCollection child : getChildren())
        {
            child.unpublishAsOAISet();
        }
        getRelations().removeOAISetRelation();
    }
    
    protected String createOAISetSpec(String setElements)
    {
        SimpleCollectionImpl parent = (SimpleCollectionImpl) getParent();
        if (parent != null)
        {
            setElements = parent.createOAISetSpec(parent.getOAISetElement() + ":" + setElements);
        }
        return setElements;
    }
    
    protected String getOAISetElement()
    {
        return isRoot() ? getDmoNamespace().getValue() : getDmoStoreId().getId();
    }

    @Override
    public SimpleCollection getParent()
    {
        DmoStoreId parentId = getParentId();
        if (parentId != null && parent == null)
        {
            parent = getObject(parentId);
        }
        return parent;
    }
    
    @Override
    public List<SimpleCollection> getChildren()
    {
        List<DmoStoreId> childIds = getChildIds();
        if (childIds.size() != children.size())
        {
            children.clear();
            for (DmoStoreId dmoStoreId : childIds)
            {
                SimpleCollectionImpl kid = (SimpleCollectionImpl) getObject(dmoStoreId);
                kid.parent = this;
                children.add(kid);
            }
        }
        return Collections.unmodifiableList(children);
    }
    
    @Override
    public boolean isRoot()
    {
        DmoStoreId dmoStoreId = getDmoStoreId();
        return !hasParent() && dmoStoreId != null && ROOT_ID.equals(dmoStoreId.getId());
    }

    @Override
    public boolean hasParent()
    {
        return getParentId() != null;
    }
    
    @Override
    public DmoStoreId getParentId()
    {
        return getRelations().getParentId();
    }

    @Override
    public boolean hasChildren()
    {
        return !getChildIds().isEmpty();
    }
    
    @Override
    public List<DmoStoreId> getChildIds()
    {
        return getRelations().getChildIds();
    }
    
    @Override
    public boolean addChild(SimpleCollection child)
    {
        if (getStoreId().equals(child.getStoreId()))
        {
            throw new IllegalArgumentException("Cannot add child that is me!");
        }
        if (child.isPublishedAsOAISet())
        {
            throw new IllegalArgumentException("Cannot add a child that is published as OAI set.");
        }
        SimpleCollectionImpl childImpl;
        if (child instanceof SimpleCollectionImpl)
        {
            childImpl = (SimpleCollectionImpl) child;
        }
        else
        {
            throw new IllegalArgumentException("Child is not a " + SimpleCollectionImpl.class.getName());
        }
        
        boolean childAdded = false;
        if (childImpl.setParent(this))
        {
            children.add(child);
            getRelations().addChild(child);
            childAdded = true;
        }
        return childAdded;
    }

    private boolean setParent(SimpleCollection parent)
    {
        boolean parentSet = false;
        if (!hasParent() && !isRoot())
        {
            this.parent = parent;
            getRelations().setParent(parent);
            parentSet = true;
            logger.debug("[" + getStoreId() + "] now has parent [" + parent.getStoreId() + "]");
        }
        return parentSet;
    }
    
    public boolean removeChild(SimpleCollection child)
    {
        SimpleCollectionImpl childImpl;
        if (child instanceof SimpleCollectionImpl)
        {
            childImpl = (SimpleCollectionImpl) child;
        }
        else
        {
            throw new IllegalArgumentException("Child is not a " + SimpleCollectionImpl.class.getName());
        }
        
        boolean childRemoved = false;
        if (childImpl.removeParent(this))
        {
            children.remove(child);
            getRelations().removeChild(child);
            childRemoved = true;
        }
        return childRemoved;
    }
    
    private boolean removeParent(SimpleCollection parent)
    {
        boolean parrentRemoved = false;
        if (parent.getStoreId().equals(getParentId()))
        {
            this.parent = null;
            getRelations().removeParent(parent);
            unpublishAsOAISet();
            parrentRemoved = true;
            logger.debug("[" + getStoreId() + "] removed parent " + parent);
        }
        return parrentRemoved;
    }
    
    private SimpleCollection getObject(DmoStoreId dmoStoreId)
    {
        SimpleCollection simpleCollection = null;
        try
        {
            UnitOfWork uow = getUnitOfWork();
            simpleCollection = (SimpleCollection) uow.retrieveObject(dmoStoreId.getStoreId());
        }
        catch (NoUnitOfWorkAttachedException e)
        {
            throw new IllegalStateException(e);
        }
        catch (RepositoryException e)
        {
            throw new ApplicationException(e);
        }
        return simpleCollection;
    }


}
