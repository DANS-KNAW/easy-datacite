package nl.knaw.dans.common.fedora.rdf;

import java.net.URI;

import nl.knaw.dans.common.fedora.fox.FoxConstants;
import fedora.common.rdf.SimpleURIReference;

public class FedoraURIReference extends SimpleURIReference {
    private static final long serialVersionUID = -6920476539882547795L;

    public FedoraURIReference(URI uri) {
        super(URI.create(uri.toString().startsWith(FoxConstants.FEDORA_NAMESPACE.uri) ? uri.toString() : FoxConstants.FEDORA_NAMESPACE.uri + uri.toString()));
    }

    public static String create(String uri) {
        return new FedoraURIReference(URI.create(uri)).toString();
    }

    public static String strip(String uri) {
        if (uri.startsWith(FoxConstants.FEDORA_NAMESPACE.uri))
            return uri.substring(FoxConstants.FEDORA_NAMESPACE.uri.length());
        return uri;
    }
}
