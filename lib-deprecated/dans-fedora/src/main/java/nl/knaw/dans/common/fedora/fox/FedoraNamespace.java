package nl.knaw.dans.common.fedora.fox;

import nl.knaw.dans.common.lang.repo.relations.OntologyNamespace;

/**
 * The Fedora RDF namespace.
 * 
 * <pre>
 * Namespace URI    : info:fedora/
 * Preferred Prefix : fedora
 * </pre>
 * 
 * @see <a
 *      href="http://info-uri.info/registry/OAIHandler?verb=GetRecord&metadataPrefix=reg&identifier=info:fedora/">
 *      "info" URI Scheme Registry page</a>
 * @author Chris Wilper
 */
public class FedoraNamespace extends OntologyNamespace
{

    private static final long serialVersionUID = 1L;

    public FedoraNamespace()
    {

        uri = "info:fedora/";
        prefix = "fedora";
    }

}
