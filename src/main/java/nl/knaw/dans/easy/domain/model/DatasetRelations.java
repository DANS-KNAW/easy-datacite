package nl.knaw.dans.easy.domain.model;

import java.util.Set;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.collections.DmoContainerItemRelations;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;

public class DatasetRelations extends DmoContainerItemRelations<Dataset>
{

    private static final long serialVersionUID = 4284528908966717267L;
    
    private final Dataset dataset;

    public DatasetRelations(Dataset dataset)
    {
        super(dataset);
        this.dataset = dataset;
    }
    
    public void addOAIIdentifier()
    {
        String oaiId = Constants.OAI_IDENTIFIER_PREFIX + dataset.getStoreId();
        addRelation(RelsConstants.OAI_ITEM_ID, oaiId, RelsConstants.RDF_LITERAL);
    }
    
    public void removeOAIIdentifier()
    {
        String oaiId = Constants.OAI_IDENTIFIER_PREFIX + dataset.getStoreId();
        removeRelation(RelsConstants.OAI_ITEM_ID, oaiId);
    }
    
    public boolean hasOAIIdentifier()
    {
        return getRelation(RelsConstants.OAI_ITEM_ID, null).size() == 1;
    }
    
    public void addOAISetMembership() throws DomainException
    {
        // discipline sets
        for (DisciplineContainer dc : dataset.getLeafDisciplines())
        {
            String object = RelsConstants.getObjectURI(dc.getStoreId());
            addRelation(RelsConstants.DANS_NS.IS_MEMBER_OF_OAI_SET, object);
        }
        
        // driver set
        if (AccessCategory.isOpenAccess(dataset.getAccessCategory()) && !dataset.isUnderEmbargo())
        {
            String object = RelsConstants.getObjectURI(Constants.OAI_DRIVER_SET_ID);
            addRelation(RelsConstants.DANS_NS.IS_MEMBER_OF_OAI_SET, object);
        }
                
    }
    
    public void removeOAISetMembership()
    {
        removeRelation(RelsConstants.DANS_NS.IS_MEMBER_OF_OAI_SET, null);
    }
    
    public void setPersistentIdentifier(String pid)
    {
        addRelation(RelsConstants.DANS_NS.HAS_PID, pid, RelsConstants.RDF_LITERAL);
    }
    
    public String getPersistentIdentifier()
    {
        String persistentIdentifier = null;
        Set<Relation> pidLiterals = getRelation(RelsConstants.DANS_NS.HAS_PID.getURI().toString(), null);
        if (!pidLiterals.isEmpty())
        {
            persistentIdentifier = (String) pidLiterals.iterator().next().getObject();
        }
        return persistentIdentifier;
    }
    
//    public void removePersistentIdentifier()
//    {
//        removeRelation(RelsConstants.DANS_NS.HAS_PID, null);
//    }
    
    public void setAipId(String aipId)
    {
        addRelation(RelsConstants.DANS_NS.HAS_AIP_ID, aipId, RelsConstants.RDF_LITERAL);
    }
    
//    public void removeAipId()
//    {
//        removeRelation(RelsConstants.DANS_NS.HAS_AIP_ID, null);
//    }

}
