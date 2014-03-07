package nl.knaw.dans.easy.domain.model;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.collections.DmoContainerItemRelations;
import nl.knaw.dans.common.lang.repo.relations.DansOntologyNamespace;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.easy.domain.collections.ECollection;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.pf.language.emd.types.Author;

public class DatasetRelations extends DmoContainerItemRelations<Dataset>
{

    private static final long serialVersionUID = 4284528908966717267L;

    private final Dataset dataset;

    public DatasetRelations(Dataset dataset)
    {
        super(dataset);
        this.dataset = dataset;
    }

    // OAI-identifier and OAI-sets
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

    public void addOAISetMembership(DmoStoreId dmoStoreId)
    {
        String object = RelsConstants.getObjectURI(dmoStoreId.getStoreId());
        addRelation(RelsConstants.DANS_NS.IS_MEMBER_OF_OAI_SET, object);
    }

    public void addOAISetMembership(List<DmoStoreId> storeIds)
    {
        for (DmoStoreId dmoStoreId : storeIds)
        {
            addOAISetMembership(dmoStoreId);
        }
    }

    public void removeOAISetMembership()
    {
        removeRelation(RelsConstants.DANS_NS.IS_MEMBER_OF_OAI_SET, null);
    }

    public void removeOAISetMembership(DmoStoreId dmoStoreId)
    {
        String object = RelsConstants.getObjectURI(dmoStoreId.getStoreId());
        removeRelation(RelsConstants.DANS_NS.IS_MEMBER_OF_OAI_SET, object);
    }

    public void removeOAISetMembership(List<DmoStoreId> storeIds)
    {
        for (DmoStoreId dmoStoreId : storeIds)
        {
            removeOAISetMembership(dmoStoreId);
        }
    }

    /**
     * Get objectIds that are targeted with predicate {@link DansOntologyNamespace#IS_MEMBER_OF_OAI_SET}
     * from this relations dataset.
     * 
     * <pre>
     *    this.DatasetRelations --- dans:isMemberOfOAISet ---> DmoStoreId
     * </pre>
     * 
     * @param namespaces
     *        filter for the given namespaces or leave empty to get all relations with predicate
     *        {@link DansOntologyNamespace#IS_MEMBER_OF_OAI_SET}.
     * @return set of dmoStoreIds
     */
    public Set<DmoStoreId> getOAISetMemberships(DmoNamespace... namespaces)
    {
        List<DmoNamespace> namespaceList = Arrays.asList(namespaces);
        Set<DmoStoreId> memberships = new HashSet<DmoStoreId>();
        Set<Relation> allMemberships = getRelation(RelsConstants.DANS_NS.IS_MEMBER_OF_OAI_SET.getURI().toString(), null);
        for (Relation r : allMemberships)
        {
            DmoStoreId dmoStoreId = new DmoStoreId(RelsConstants.stripFedoraUri((String) r.getObject()));
            if (namespaceList.isEmpty() || namespaceList.contains(dmoStoreId.getNamespace()))
            {
                memberships.add(dmoStoreId);
            }
        }
        return memberships;
    }

    public boolean isOAISetMember(DmoStoreId setStoreId)
    {
        Set<DmoStoreId> memberships = getOAISetMemberships(setStoreId.getNamespace());
        return memberships.contains(setStoreId);
    }

    // Collections
    public void addCollectionMembership(DmoStoreId dmoStoreId)
    {
        if (!ECollection.isECollection(dmoStoreId))
        {
            throw new IllegalArgumentException("Not an ECollection: " + dmoStoreId.getStoreId());
        }
        else
        {
            String object = RelsConstants.getObjectURI(dmoStoreId.getStoreId());
            addRelation(RelsConstants.DANS_NS.IS_COLLECTION_MEMBER, object);
        }
    }

    public void addCollectionMembership(DmoCollection dmoCollection)
    {
        addCollectionMembership(dmoCollection.getDmoStoreId());
    }

    public void addCollectionMembership(List<DmoStoreId> storeIds)
    {
        for (DmoStoreId dmoStoreId : storeIds)
        {
            addCollectionMembership(dmoStoreId);
        }
    }

    public void removeCollectionMembership(DmoStoreId dmoStoreId)
    {
        if (!ECollection.isECollection(dmoStoreId))
        {
            throw new IllegalArgumentException("Not an ECollection: " + dmoStoreId.getStoreId());
        }
        else
        {
            String object = RelsConstants.getObjectURI(dmoStoreId.getStoreId());
            removeRelation(RelsConstants.DANS_NS.IS_COLLECTION_MEMBER, object);
        }
    }

    public void removeCollectionMembership(List<DmoStoreId> storeIds)
    {
        for (DmoStoreId dmoStoreId : storeIds)
        {
            removeCollectionMembership(dmoStoreId);
        }
    }

    public void removeCollectionMembership(DmoCollection dmoCollection)
    {
        removeCollectionMembership(dmoCollection.getDmoStoreId());
    }

    /**
     * Get objectIds that are targeted with predicate {@link DansOntologyNamespace#IS_COLLECTION_MEMBER}
     * from this relations dataset.
     * 
     * <pre>
     *    this.DatasetRelations --- dans:isCollectionMember ---> DmoStoreId
     * </pre>
     * 
     * @param namespace
     *        filter for the given namespace or <code>null</code> to get all relations with predicate
     *        {@link DansOntologyNamespace#IS_COLLECTION_MEMBER}.
     * @return set of dmoStoreIds
     */
    public Set<DmoStoreId> getCollectionMemberships(DmoNamespace... namespaces)
    {
        List<DmoNamespace> namespaceList = Arrays.asList(namespaces);
        Set<DmoStoreId> memberships = new HashSet<DmoStoreId>();
        Set<Relation> allMemberships = getRelation(RelsConstants.DANS_NS.IS_COLLECTION_MEMBER.getURI().toString(), null);
        for (Relation r : allMemberships)
        {
            DmoStoreId dmoStoreId = new DmoStoreId(RelsConstants.stripFedoraUri((String) r.getObject()));
            if (namespaceList.isEmpty() || namespaceList.contains(dmoStoreId.getNamespace()))
            {
                memberships.add(dmoStoreId);
            }
        }
        return memberships;
    }

    public boolean isCollectionMember(DmoStoreId collectionStoreId)
    {
        Set<DmoStoreId> memberships = getCollectionMemberships(collectionStoreId.getNamespace());
        return memberships.contains(collectionStoreId);
    }

    // Digital Author Ids
    /**
     * Adds relations {@link DansOntologyNamespace#HAS_CREATOR_DAI} and
     * {@link DansOntologyNamespace#HAS_CONTRIBUTOR_DAI} to the relations of the subject dataset.
     * Relations can be queried, f.i. with sparql:
     * 
     * <pre>
     * select ?dataset ?daiCreator from <#ri> where {?dataset <http://dans.knaw.nl/ontologies/relations#hasCreatorDAI> ?daiCreator . }
     * select ?dataset ?daiContributor from <#ri> where {?dataset <http://dans.knaw.nl/ontologies/relations#hasContributorDAI> ?daiContributor . }
     * </pre>
     */
    public void addDAIRelations()
    {
        List<Author> creators = dataset.getEasyMetadata().getEmdCreator().getDAIAuthors();
        for (Author author : creators)
        {
            URI object = author.getDigitalAuthorId().getURI();
            addRelation(RelsConstants.DANS_NS.HAS_CREATOR_DAI, object);
        }
        List<Author> contributors = dataset.getEasyMetadata().getEmdContributor().getDAIAuthors();
        for (Author author : contributors)
        {
            URI object = author.getDigitalAuthorId().getURI();
            addRelation(RelsConstants.DANS_NS.HAS_CONTRIBUTOR_DAI, object);
        }
    }

    /**
     * Removes relations {@link DansOntologyNamespace#HAS_CREATOR_DAI} and
     * {@link DansOntologyNamespace#HAS_CONTRIBUTOR_DAI} from the relations of the subject dataset.
     */
    public void removeDAIRelations()
    {
        removeRelation(RelsConstants.DANS_NS.HAS_CREATOR_DAI, null);
        removeRelation(RelsConstants.DANS_NS.HAS_CONTRIBUTOR_DAI, null);
    }

    // Identifiers
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

    public void setAipId(String aipId)
    {
        addRelation(RelsConstants.DANS_NS.HAS_AIP_ID, aipId, RelsConstants.RDF_LITERAL);
    }

}
