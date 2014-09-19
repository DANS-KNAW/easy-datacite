package nl.knaw.dans.pf.language.ddm.handlers;

import org.xml.sax.SAXException;

import nl.knaw.dans.pf.language.ddm.handlertypes.BasicIdentifierHandler;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

public class IdentifierHandler extends BasicIdentifierHandler {
    @Override
    public void finishElement(final String uri, final String localName) throws SAXException {
        final BasicIdentifier identifier = createIdentifier(uri, localName);
        if (identifier != null)
            getTarget().getEmdIdentifier().add(identifier);
    }
}
