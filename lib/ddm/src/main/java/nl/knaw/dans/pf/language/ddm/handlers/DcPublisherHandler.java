package nl.knaw.dans.pf.language.ddm.handlers;

import nl.knaw.dans.pf.language.ddm.handlertypes.BasicStringHandler;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.xml.sax.SAXException;

public class DcPublisherHandler extends BasicStringHandler {
    @Override
    protected void finishElement(final String uri, final String localName) throws SAXException {
        final BasicString basicString = createBasicString(uri, localName);
        if (basicString != null)
            getTarget().getEmdPublisher().getDcPublisher().add(basicString);
    }
}
