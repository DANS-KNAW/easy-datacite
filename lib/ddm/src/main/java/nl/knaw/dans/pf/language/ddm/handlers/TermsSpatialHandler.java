package nl.knaw.dans.pf.language.ddm.handlers;

import org.xml.sax.SAXException;

import nl.knaw.dans.pf.language.ddm.handlertypes.BasicStringHandler;
import nl.knaw.dans.pf.language.emd.types.BasicString;

public class TermsSpatialHandler extends BasicStringHandler {
    @Override
    protected void finishElement(final String uri, final String localName) throws SAXException {
        BasicString basicString = createBasicString(uri, localName);
        if (basicString != null)
            getTarget().getEmdCoverage().getTermsSpatial().add(basicString);
    }
}
