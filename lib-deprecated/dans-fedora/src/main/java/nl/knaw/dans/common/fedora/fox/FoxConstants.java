package nl.knaw.dans.common.fedora.fox;

import java.net.URI;

import fedora.common.Constants;

public final class FoxConstants {

    /**
     * The xml-mimetype.
     */
    public static final String MIMETYPE_XML = "text/xml";

    public static final String STREAM_ID_EXT = "RELS-EXT";

    public static final String STREAM_ID_INT = "RELS-INT";

    public static final String LABEL = "RDF Statements about this object";

    public static final URI RELS_EXT_FORMAT_URI_EXT = URI.create(Constants.RELS_EXT1_0.uri);

    public static final URI RELST_INT_FORMAT_URI_INT = URI.create(Constants.RELS_INT1_0.uri);

    public static FedoraModelOntology MODEL_ONTOLOGY = new FedoraModelOntology();

    public static FedoraNamespace FEDORA_NAMESPACE = new FedoraNamespace();

    private FoxConstants() {

    }
}
