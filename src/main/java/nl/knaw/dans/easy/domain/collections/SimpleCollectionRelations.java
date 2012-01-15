package nl.knaw.dans.easy.domain.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.relations.AbstractRelations;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.easy.domain.model.Constants;

public class SimpleCollectionRelations extends AbstractRelations<SimpleCollection>
{

    private static final long serialVersionUID = -7417911716465435099L;
    

    protected SimpleCollectionRelations(SimpleCollection subject)
    {
        super(subject);
    }
    
    protected void setParent(SimpleCollection parent)
    {
        String object = RelsConstants.getObjectURI(parent.getStoreId());
        addRelation(RelsConstants.DANS_NS.HAS_PARENT, object);
    }
    
    protected void removeParent(SimpleCollection parent)
    {
        String object = RelsConstants.getObjectURI(parent.getStoreId());
        removeRelation(RelsConstants.DANS_NS.HAS_PARENT, object);
    }
    
    public DmoStoreId getParentId()
    {
        DmoStoreId parentId = null;
        Set<Relation> parentRelations = getRelation(RelsConstants.DANS_NS.HAS_PARENT.getURI().toString(), null);
        if (!parentRelations.isEmpty())
        {
            Relation parentRelation = parentRelations.iterator().next();
            parentId = DmoStoreId.newDmoStoreId(RelsConstants.stripFedoraUri((String) parentRelation.getObject()));
        }
        return parentId;
    }
    
    protected void addChild(SimpleCollection child)
    {
        String object = RelsConstants.getObjectURI(child.getStoreId());
        addRelation(RelsConstants.DANS_NS.HAS_CHILD, object);
    }
    
    protected void removeChild(SimpleCollection child)
    {
        String object = RelsConstants.getObjectURI(child.getStoreId());
        removeRelation(RelsConstants.DANS_NS.HAS_CHILD, object);
    }
    
    public List<DmoStoreId> getChildIds()
    {
        List<DmoStoreId> childIds = new ArrayList<DmoStoreId>();
        Set<Relation> childRelations = getRelation(RelsConstants.DANS_NS.HAS_CHILD.getURI().toString(), null);
        for (Relation r : childRelations)
        {
            childIds.add(DmoStoreId.newDmoStoreId(RelsConstants.stripFedoraUri((String) r.getObject())));
        }
        return childIds;
    }
    
    protected void addOAISetRelation(String setSpec, String setName)
    {
        String object = RelsConstants.getObjectURI(Constants.CM_OAI_SET_1);
        addRelation(RelsConstants.FM_HAS_MODEL, object);
        
        addRelation(RelsConstants.OAI_SET_SPEC, setSpec, RelsConstants.RDF_LITERAL);
        addRelation(RelsConstants.OAI_SET_NAME, setName, RelsConstants.RDF_LITERAL);
    }
    
    protected void removeOAISetRelation()
    {
        String object = RelsConstants.getObjectURI(Constants.CM_OAI_SET_1);
        removeRelation(RelsConstants.FM_HAS_MODEL, object);
        
        removeRelation(RelsConstants.OAI_SET_SPEC, null);
        removeRelation(RelsConstants.OAI_SET_NAME, null);
    }
    
    public boolean hasOAISetRelation()
    {
        String object = RelsConstants.getObjectURI(Constants.CM_OAI_SET_1);
        Set<Relation> oaiContentModels = getRelation(RelsConstants.FM_HAS_MODEL, object);
        return !oaiContentModels.isEmpty();
    }
}
