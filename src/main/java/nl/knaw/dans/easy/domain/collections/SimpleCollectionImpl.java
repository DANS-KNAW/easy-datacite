package nl.knaw.dans.easy.domain.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.ApplicationException;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDataModelObject;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.NoUnitOfWorkAttachedException;
import nl.knaw.dans.common.lang.repo.relations.Relations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCollectionImpl extends AbstractDataModelObject implements SimpleCollection
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -1334420292881968002L;
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleCollectionImpl.class);
    
    private SimpleCollection parent;
    private List<SimpleCollection> children = new ArrayList<SimpleCollection>();
    

    public SimpleCollectionImpl(String storeId)
    {
        super(storeId);
        if (storeId == null || !storeId.startsWith(SimpleCollection.NAMESPACE))
        {
            throw new IllegalArgumentException("Invallid storeId: " + storeId);
        }
        setState("Active");
        setOwnerId("FedoraAdmin");
    }

    @Override
    public String getObjectNamespace()
    {
        return NAMESPACE;
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
    
    @Override
    public boolean isOAISet()
    {
        return getRelations().hasOAISetRelation();
    }
    
    @Override
    public void setOAISet(boolean isOAISet)
    {
        if (isOAISet)
        {
            String setSpec = createSetSpec(getSetElement());
            getRelations().addOAISetRelation(setSpec, getLabel());
        }
        else
        {
            getRelations().removeOAISetRelation();
        }
    }
    
    protected String createSetSpec(String setElements)
    {
        SimpleCollectionImpl parent = (SimpleCollectionImpl) getParent();
        if (parent != null)
        {
            setElements = parent.createSetSpec(parent.getSetElement() + ":" + setElements);
        }
        return setElements;
    }
    
    protected String getSetElement()
    {
        return getStoreId().substring(NAMESPACE.length() + 1);
    }

    @Override
    public SimpleCollection getParent()
    {
        String parentId = getParentId();
        if (parentId != null && parent == null)
        {
            parent = getObject(parentId);
        }
        return parent;
    }
    
    @Override
    public List<SimpleCollection> getChildren()
    {
        List<String> childIds = getChildIds();
        if (childIds.size() != children.size())
        {
            children.clear();
            for (String storeId : childIds)
            {
                SimpleCollectionImpl kid = (SimpleCollectionImpl) getObject(storeId);
                kid.parent = this;
                children.add(kid);
            }
        }
        return Collections.unmodifiableList(children);
    }

    @Override
    public boolean hasParent()
    {
        return getParentId() != null;
    }
    
    @Override
    public String getParentId()
    {
        return getRelations().getParentId();
    }

    @Override
    public boolean hasChildren()
    {
        return !getChildIds().isEmpty();
    }
    
    @Override
    public List<String> getChildIds()
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
        if (!hasParent())
        {
            this.parent = parent;
            getRelations().setParent(parent);
            parentSet = true;
            logger.debug("[" + getLabel() + "] now has parent " + parent);
        }
        return parentSet;
    }
    
    public boolean remove(SimpleCollectionImpl child)
    {
        boolean childRemoved = false;
        if (child.removeParent(this))
        {
            children.remove(child);
            getRelations().removeChild(child);
            setOAISet(false);
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
            parrentRemoved = true;
            logger.debug("[" + getLabel() + "] removed parent " + parent);
        }
        return parrentRemoved;
    }
    
    private SimpleCollection getObject(String storeId)
    {
        SimpleCollection simpleCollection = null;
        try
        {
            UnitOfWork uow = getUnitOfWork();
            simpleCollection = (SimpleCollection) uow.retrieveObject(storeId);
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
