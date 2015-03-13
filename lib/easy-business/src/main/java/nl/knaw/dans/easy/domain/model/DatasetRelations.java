package nl.knaw.dans.easy.domain.model;

import static nl.knaw.dans.common.lang.repo.relations.RelsConstants.DANS_NS;
import static nl.knaw.dans.common.lang.repo.relations.RelsConstants.OAI_ITEM_ID;
import static nl.knaw.dans.common.lang.repo.relations.RelsConstants.RDF_LITERAL;
import static nl.knaw.dans.common.lang.repo.relations.RelsConstants.getObjectURI;
import static nl.knaw.dans.common.lang.repo.relations.RelsConstants.stripFedoraUri;
import static nl.knaw.dans.easy.domain.model.Constants.OAI_IDENTIFIER_PREFIX;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.collections.DmoContainerItemRelations;
import nl.knaw.dans.common.lang.repo.relations.DansOntologyNamespace;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.RelationName;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.collections.ECollection;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.pf.language.emd.types.Author;

public class DatasetRelations extends DmoContainerItemRelations<Dataset> {

    private static final long serialVersionUID = 4284528908966717267L;

    private final Dataset dataset;

    public DatasetRelations(Dataset dataset) {
        super(dataset);
        this.dataset = dataset;
    }

    // OAI-identifier and OAI-sets
    public void addOAIIdentifier() {
        String oaiId = OAI_IDENTIFIER_PREFIX + dataset.getStoreId();
        addRelation(OAI_ITEM_ID, oaiId, RDF_LITERAL);
    }

    public void removeOAIIdentifier() {
        String oaiId = OAI_IDENTIFIER_PREFIX + dataset.getStoreId();
        removeRelation(OAI_ITEM_ID, oaiId);
    }

    public boolean hasOAIIdentifier() {
        return getRelation(OAI_ITEM_ID, null).size() == 1;
    }

    public void addOAISetMembership(DmoStoreId dmoStoreId) {
        String object = getObjectURI(dmoStoreId.getStoreId());
        addRelation(DANS_NS.IS_MEMBER_OF_OAI_SET, object);
    }

    public void addOAISetMembership(List<DmoStoreId> storeIds) {
        for (DmoStoreId dmoStoreId : storeIds) {
            addOAISetMembership(dmoStoreId);
        }
    }

    public void removeOAISetMembership() {
        removeRelation(DANS_NS.IS_MEMBER_OF_OAI_SET, null);
    }

    public void removeOAISetMembership(DmoStoreId dmoStoreId) {
        String object = getObjectURI(dmoStoreId.getStoreId());
        removeRelation(DANS_NS.IS_MEMBER_OF_OAI_SET, object);
    }

    public void removeOAISetMembership(List<DmoStoreId> storeIds) {
        for (DmoStoreId dmoStoreId : storeIds) {
            removeOAISetMembership(dmoStoreId);
        }
    }

    /**
     * Get objectIds that are targeted with predicate {@link DansOntologyNamespace#IS_MEMBER_OF_OAI_SET} from this relations dataset.
     * 
     * <pre>
     *    this.DatasetRelations --- dans:isMemberOfOAISet ---> DmoStoreId
     * </pre>
     * 
     * @param namespaces
     *        filter for the given namespaces or leave empty to get all relations with predicate {@link DansOntologyNamespace#IS_MEMBER_OF_OAI_SET}.
     * @return set of dmoStoreIds
     */
    public Set<DmoStoreId> getOAISetMemberships(DmoNamespace... namespaces) {
        List<DmoNamespace> namespaceList = Arrays.asList(namespaces);
        Set<DmoStoreId> memberships = new HashSet<DmoStoreId>();
        Set<Relation> allMemberships = getRelation(DANS_NS.IS_MEMBER_OF_OAI_SET.getURI().toString(), null);
        for (Relation r : allMemberships) {
            DmoStoreId dmoStoreId = new DmoStoreId(stripFedoraUri((String) r.getObject()));
            if (namespaceList.isEmpty() || namespaceList.contains(dmoStoreId.getNamespace())) {
                memberships.add(dmoStoreId);
            }
        }
        return memberships;
    }

    public boolean isOAISetMember(DmoStoreId setStoreId) {
        Set<DmoStoreId> memberships = getOAISetMemberships(setStoreId.getNamespace());
        return memberships.contains(setStoreId);
    }

    // Collections
    public void addCollectionMembership(DmoStoreId dmoStoreId) {
        if (!ECollection.isECollection(dmoStoreId)) {
            throw new IllegalArgumentException("Not an ECollection: " + dmoStoreId.getStoreId());
        } else {
            String object = getObjectURI(dmoStoreId.getStoreId());
            addRelation(DANS_NS.IS_COLLECTION_MEMBER, object);
        }
    }

    public void addCollectionMembership(DmoCollection dmoCollection) {
        addCollectionMembership(dmoCollection.getDmoStoreId());
    }

    public void addCollectionMembership(List<DmoStoreId> storeIds) {
        for (DmoStoreId dmoStoreId : storeIds) {
            addCollectionMembership(dmoStoreId);
        }
    }

    public void removeCollectionMembership(DmoStoreId dmoStoreId) {
        if (!ECollection.isECollection(dmoStoreId)) {
            throw new IllegalArgumentException("Not an ECollection: " + dmoStoreId.getStoreId());
        } else {
            String object = getObjectURI(dmoStoreId.getStoreId());
            removeRelation(DANS_NS.IS_COLLECTION_MEMBER, object);
        }
    }

    public void removeCollectionMembership(List<DmoStoreId> storeIds) {
        for (DmoStoreId dmoStoreId : storeIds) {
            removeCollectionMembership(dmoStoreId);
        }
    }

    public void removeCollectionMembership(DmoCollection dmoCollection) {
        removeCollectionMembership(dmoCollection.getDmoStoreId());
    }

    /**
     * Get objectIds that are targeted with predicate {@link DansOntologyNamespace#IS_COLLECTION_MEMBER} from this relations dataset.
     * 
     * <pre>
     *    this.DatasetRelations --- dans:isCollectionMember ---> DmoStoreId
     * </pre>
     * 
     * @param namespace
     *        filter for the given namespace or <code>null</code> to get all relations with predicate {@link DansOntologyNamespace#IS_COLLECTION_MEMBER}.
     * @return set of dmoStoreIds
     */
    public Set<DmoStoreId> getCollectionMemberships(DmoNamespace... namespaces) {
        List<DmoNamespace> namespaceList = Arrays.asList(namespaces);
        Set<DmoStoreId> memberships = new HashSet<DmoStoreId>();
        Set<Relation> allMemberships = getRelation(DANS_NS.IS_COLLECTION_MEMBER.getURI().toString(), null);
        for (Relation r : allMemberships) {
            DmoStoreId dmoStoreId = new DmoStoreId(stripFedoraUri((String) r.getObject()));
            if (namespaceList.isEmpty() || namespaceList.contains(dmoStoreId.getNamespace())) {
                memberships.add(dmoStoreId);
            }
        }
        return memberships;
    }

    public boolean isCollectionMember(DmoStoreId collectionStoreId) {
        Set<DmoStoreId> memberships = getCollectionMemberships(collectionStoreId.getNamespace());
        return memberships.contains(collectionStoreId);
    }

    // Digital Author Ids
    /**
     * Adds relations {@link DansOntologyNamespace#HAS_CREATOR_DAI} and {@link DansOntologyNamespace#HAS_CONTRIBUTOR_DAI} to the relations of the subject
     * dataset. Relations can be queried, f.i. with sparql:
     * 
     * <pre>
     * select ?dataset ?daiCreator from <#ri> where {?dataset <http://dans.knaw.nl/ontologies/relations#hasCreatorDAI> ?daiCreator . }
     * select ?dataset ?daiContributor from <#ri> where {?dataset <http://dans.knaw.nl/ontologies/relations#hasContributorDAI> ?daiContributor . }
     * </pre>
     */
    public void addDAIRelations() {
        List<Author> creators = dataset.getEasyMetadata().getEmdCreator().getDAIAuthors();
        for (Author author : creators) {
            URI object = author.getDigitalAuthorId().getURI();
            addRelation(DANS_NS.HAS_CREATOR_DAI, object);
        }
        List<Author> contributors = dataset.getEasyMetadata().getEmdContributor().getDAIAuthors();
        for (Author author : contributors) {
            URI object = author.getDigitalAuthorId().getURI();
            addRelation(DANS_NS.HAS_CONTRIBUTOR_DAI, object);
        }
    }

    /**
     * Removes relations {@link DansOntologyNamespace#HAS_CREATOR_DAI} and {@link DansOntologyNamespace#HAS_CONTRIBUTOR_DAI} from the relations of the subject
     * dataset.
     */
    public void removeDAIRelations() {
        removeRelation(DANS_NS.HAS_CREATOR_DAI, null);
        removeRelation(DANS_NS.HAS_CONTRIBUTOR_DAI, null);
    }

    // Identifiers
    public void setPersistentIdentifier(String urn) throws RepositoryException {
        addPidRelation(urn, DANS_NS.HAS_PID);
    }

    public String getPersistentIdentifier() {
        return getPidRelation(DANS_NS.HAS_PID);
    }

    public void setDansManagedDOI(String doi) throws RepositoryException {
        addPidRelation(doi, DANS_NS.HAS_DOI);
    }

    public String getDansManagedDOI() {
        return getPidRelation(DANS_NS.HAS_DOI);
    }

    private String getPidRelation(RelationName hasPid) {
        Set<Relation> relations = getRelation(hasPid.getURI().toString(), null);
        if (relations == null || !relations.isEmpty())
            return (String) relations.iterator().next().getObject();
        return null;
    }

    private void addPidRelation(String pid, RelationName hasPid) throws RepositoryException {
        List<Relation> relations = Data.getEasyStore().getRelations(null, hasPid.getURI().toString(), pid);
        if (relations.size() > 0)
            throw new IllegalStateException(pid + " exists for " + relations.get(0).getSubject());
        // due to latency the above check does not protect against concurrent updates
        // but it does protect against errors in seed configuration and exhaustion
        addRelation(hasPid, pid, RDF_LITERAL);
    }

    public void setAipId(String aipId) {
        addRelation(DANS_NS.HAS_AIP_ID, aipId, RDF_LITERAL);
    }
}
