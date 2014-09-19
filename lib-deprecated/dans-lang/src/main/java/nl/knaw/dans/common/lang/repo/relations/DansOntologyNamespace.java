package nl.knaw.dans.common.lang.repo.relations;

/**
 * The DANS ontology. Currently I am not sure how we should use this, how and if we should map on existing standard, but at least here we can define our own
 * ontology if needed.
 * 
 * @author lobo
 */
public class DansOntologyNamespace extends OntologyNamespace {
    private static final long serialVersionUID = 8904001093613110074L;

    /**
     * STUPID! !(not)General relation to express membership. These relations are removed in method prepare for update and replaced with
     * DmoContainerItemRelations Parents or something! Anyway. If you add a relation of this type, than it is gone before update is actually processed. It's
     * time to get rid of this stupid DmoContainerItemRelations shit. (If ever Lodewijk gets into my hands I'll .....)
     */
    public final RelationName IS_MEMBER_OF;

    /**
     * Expresses that subject is member of collection.
     */
    public final RelationName IS_COLLECTION_MEMBER;

    /**
     * Expresses that subject is under the authority or control of the object.
     */
    public final RelationName IS_SUBORDINATE_TO;

    public final RelationName HAS_DOWNLOAD_HISTORY_OF;
    public final RelationName HAS_PERIOD;
    public final RelationName IS_JUMPOFF_PAGE_FOR;
    public final RelationName HAS_PID;
    public final RelationName HAS_AIP_ID;

    /**
     * Use to declare OAI set membership.
     */
    public final RelationName IS_MEMBER_OF_OAI_SET;

    /**
     * Subject has a child-parent relation to the object.
     */
    public final RelationName HAS_PARENT;

    /**
     * Subject has a parent-child relation to the object.
     */
    public final RelationName HAS_CHILD;

    public final RelationName HAS_SHORT_NAME;

    public final RelationName HAS_CREATOR_DAI;

    public final RelationName HAS_CONTRIBUTOR_DAI;

    public DansOntologyNamespace() {
        uri = "http://dans.knaw.nl/ontologies/relations#";
        prefix = "dans";

        IS_MEMBER_OF = new RelationName(this, "isMemberOf");
        IS_COLLECTION_MEMBER = new RelationName(this, "isCollectionMember");
        IS_SUBORDINATE_TO = new RelationName(this, "isSubordinateTo");
        HAS_DOWNLOAD_HISTORY_OF = new RelationName(this, "hasDownloadHistoryOf");
        HAS_PERIOD = new RelationName(this, "hasPeriod");
        IS_MEMBER_OF_OAI_SET = new RelationName(this, "isMemberOfOAISet");
        IS_JUMPOFF_PAGE_FOR = new RelationName(this, "isJumpoffPageFor");
        HAS_PID = new RelationName(this, "hasPid");
        HAS_AIP_ID = new RelationName(this, "hasAipId");
        HAS_PARENT = new RelationName(this, "hasParent");
        HAS_CHILD = new RelationName(this, "hasChild");
        HAS_SHORT_NAME = new RelationName(this, "hasShortName");
        HAS_CREATOR_DAI = new RelationName(this, "hasCreatorDAI");
        HAS_CONTRIBUTOR_DAI = new RelationName(this, "hasContributorDAI");
    }
}
