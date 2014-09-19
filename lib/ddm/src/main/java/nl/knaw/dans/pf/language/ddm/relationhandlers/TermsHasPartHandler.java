package nl.knaw.dans.pf.language.ddm.relationhandlers;

import org.xml.sax.SAXException;

import nl.knaw.dans.pf.language.ddm.handlertypes.BasicIdentifierHandler;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

public class TermsHasPartHandler extends BasicIdentifierHandler {
    @Override
    public void finishElement(final String uri, final String localName) throws SAXException {
        final BasicIdentifier relation = createIdentifier(uri, localName);
        if (relation != null)
            getTarget().getEmdRelation().getTermsHasPart().add(relation);
    }
}
