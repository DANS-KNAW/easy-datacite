package nl.knaw.dans.common.fedora.fox;

import nl.knaw.dans.common.lang.repo.relations.OntologyNamespace;
import nl.knaw.dans.common.lang.repo.relations.RelationName;

public class FedoraModelOntology extends OntologyNamespace {

    /**
     * 
     */
    private static final long serialVersionUID = 1472419590583658143L;

    // Properties

    public final RelationName ALT_IDS;

    /**
     * Deprecated as of Fedora 3.0. Replaced by HAS_CONTENT_MODEL.
     */
    @Deprecated
    public final RelationName CONTENT_MODEL;

    public final RelationName CONTROL_GROUP;

    public final RelationName CREATED_DATE;

    public final RelationName DEFINES_METHOD;

    public final RelationName DIGEST;

    public final RelationName DIGEST_TYPE;

    /**
     * Deprecated as of Fedora 3.0. No replacement. This information is no longer recorded.
     */
    @Deprecated
    public final RelationName DEPENDS_ON;

    public final RelationName EXT_PROPERTY;

    public final RelationName FORMAT_URI;

    /**
     * Deprecated as of Fedora 3.0. Replaced by HAS_BDEF.
     */
    @Deprecated
    public final RelationName IMPLEMENTS_BDEF;

    public final RelationName LABEL;

    public final RelationName LENGTH;

    public final RelationName OWNER;

    public final RelationName STATE;

    /**
     * Deprecated as of Fedora 3.0. No direct replacement. Objects now point to content models via HAS_CMODEL. Service Deployments used by an object are those
     * that point to the content model of the object via IS_CONTRACTOR_OF.
     */
    @Deprecated
    public final RelationName USES_BMECH;

    public final RelationName VERSIONABLE;

    // Values
    public final RelationName ACTIVE;

    public final RelationName DELETED;

    public final RelationName INACTIVE;

    // CMA RDF Relationships
    public final RelationName HAS_SERVICE;

    public final RelationName IS_CONTRACTOR_OF;

    public final RelationName IS_DEPLOYMENT_OF;

    public final RelationName HAS_MODEL;

    // Pre 3.0 object types

    /**
     * Behavior Definition Object, in pre-3.0 terminology.
     * <p>
     * In 3.0, an objects "typeness" is determined by its content model. What used to be known as BDef objects in Fedora 2.x are analogous to objects in the
     * {@link SERVICE_DEFINITION_3_0} model in Fedora 3.0.
     * </p>
     * 
     * @deprecated
     */
    @Deprecated
    public final RelationName BDEF_OBJECT;

    /**
     * Behavior Mechanism Object, in pre-3.0 terminology.
     * <p>
     * In 3.0, an objects "typeness" is determined by its content model. What used to be known as BMech objects in Fedora 2.x are analogous to objects in the
     * {@link SERVICE_DEPLOYMENT_3_0} model in Fedora 3.0.
     * </p>
     * 
     * @deprecated
     */
    @Deprecated
    public final RelationName BMECH_OBJECT;

    /**
     * Data Object, in pre-3.0 terminology.
     * <p>
     * In 3.0, an objects "typeness" is determined by its content model. What used to be known as data objects in Fedora 2.x are analogous to objects in the
     * {@link FEDORA_OBJECT_3_0} model in Fedora 3.0.
     * </p>
     * 
     * @deprecated
     */
    @Deprecated
    public final RelationName DATA_OBJECT;

    public FedoraModelOntology() {

        uri = "info:fedora/fedora-system:def/model#";
        prefix = "fedora-model";

        // Properties
        ALT_IDS = new RelationName(this, "altIds");
        CONTENT_MODEL = new RelationName(this, "contentModel");
        CONTROL_GROUP = new RelationName(this, "controlGroup");
        CREATED_DATE = new RelationName(this, "createdDate");
        DEFINES_METHOD = new RelationName(this, "definesMethod");
        DEPENDS_ON = new RelationName(this, "dependsOn");
        DIGEST = new RelationName(this, "digest");
        DIGEST_TYPE = new RelationName(this, "digestType");
        EXT_PROPERTY = new RelationName(this, "extProperty");
        FORMAT_URI = new RelationName(this, "formatURI");
        IMPLEMENTS_BDEF = new RelationName(this, "implementsBDef");
        LABEL = new RelationName(this, "label");
        LENGTH = new RelationName(this, "length");

        OWNER = new RelationName(this, "ownerId");
        STATE = new RelationName(this, "state");
        VERSIONABLE = new RelationName(this, "versionable");

        // Values
        ACTIVE = new RelationName(this, "Active");
        DELETED = new RelationName(this, "Deleted");
        INACTIVE = new RelationName(this, "Inactive");

        // CMA RDF Relationships
        HAS_SERVICE = new RelationName(this, "hasService");
        IS_DEPLOYMENT_OF = new RelationName(this, "isDeploymentOf");
        IS_CONTRACTOR_OF = new RelationName(this, "isContractorOf");
        HAS_MODEL = new RelationName(this, "hasModel");

        // Types
        BDEF_OBJECT = new RelationName(this, "FedoraBDefObject");

        BMECH_OBJECT = new RelationName(this, "FedoraBMechObject");
        DATA_OBJECT = new RelationName(this, "FedoraObject");

        USES_BMECH = new RelationName(this, "usesBMech");
    }

}
