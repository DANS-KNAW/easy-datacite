package nl.knaw.dans.pf.language.ddm.relationhandlers;

import org.xml.sax.SAXException;

import nl.knaw.dans.pf.language.ddm.handlertypes.BasicIdentifierHandler;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

import javax.xml.XMLConstants;

public class DcRelationHandler extends BasicIdentifierHandler {
    @Override
    public void finishElement(final String uri, final String localName) throws SAXException {
        final BasicIdentifier relation = createIdentifier(uri, localName);
        final String schemeId = getAttribute("", "scheme");
        if (schemeId != null)
            relation.setSchemeId(schemeId);
        if (relation != null)
            getTarget().getEmdRelation().getDcRelation().add(relation);
    }
}
