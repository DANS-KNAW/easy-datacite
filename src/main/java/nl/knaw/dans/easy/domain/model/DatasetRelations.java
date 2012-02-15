package nl.knaw.dans.easy.domain.model;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.collections.DmoContainerItemRelations;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.collections.EasyCollections;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;

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
        
        // dmoCollections
        Set<DmoStoreId> memberIds = getMemberships(EasyCollections.DMO_NAMESPACE_EASY_COLLECTION);
        try
        {
            Set<DmoStoreId> oaiEndNodes = Data.getEasyCollections().filterOAIEndNodes(memberIds);
            for (DmoStoreId collectionId : oaiEndNodes)
            {
                String object = RelsConstants.getObjectURI(collectionId.getStoreId());
                addRelation(RelsConstants.DANS_NS.IS_MEMBER_OF_OAI_SET, object);
            }
        }
        catch (CollectionsException e)
        {
            throw new DomainException(e);
        }
    }
    
    public void removeOAISetMembership()
    {
        removeRelation(RelsConstants.DANS_NS.IS_MEMBER_OF_OAI_SET, null);
    }
    
    public void addMembership(DmoCollection dmoCollection)
    {
        String object = RelsConstants.getObjectURI(dmoCollection.getStoreId());
        addRelation(RelsConstants.DANS_NS.IS_MEMBER_OF, object);
    }
    
    public void removeMembership(DmoCollection dmoCollection)
    {
        String object = RelsConstants.getObjectURI(dmoCollection.getStoreId());
        removeRelation(RelsConstants.DANS_NS.IS_MEMBER_OF, object);
    }
    
    /**
     * Get the set of dmoStoreIds of objects the dataset is member of.
     * <pre>
     *    dataset-subject --- rdf:about --- this.DatasetRelations --- dans:isMemberOf --- dmo-object
     * </pre>
     * @param namespace filter for the given namespace or return all if namespace == <code>null</code>.
     * @return set of dmoStoreIds
     */
    public Set<DmoStoreId> getMemberships(DmoNamespace namespace)
    {
        Set<DmoStoreId> memberships = new HashSet<DmoStoreId>();
        Set<Relation> allMemberships = getRelation(RelsConstants.DANS_NS.IS_MEMBER_OF.getURI().toString(), null);
        for (Relation r : allMemberships)
        {
            DmoStoreId dmoStoreId = new DmoStoreId(RelsConstants.stripFedoraUri((String) r.getObject()));
            if (dmoStoreId.isInNamespace(namespace) || namespace == null)
            {
                memberships.add(dmoStoreId);
            }
        }
        return memberships;
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
