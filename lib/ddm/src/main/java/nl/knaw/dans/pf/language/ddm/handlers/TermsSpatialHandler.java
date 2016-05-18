package nl.knaw.dans.pf.language.ddm.handlers;

import org.xml.sax.SAXException;

import nl.knaw.dans.pf.language.ddm.handlertypes.BasicStringHandler;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import static org.apache.commons.lang.StringUtils.isBlank;

public class TermsSpatialHandler extends BasicStringHandler {
    @Override
    protected void finishElement(final String uri, final String localName) throws SAXException {
        BasicString basicString = createBasicString(uri, localName);
        // the uri check prevents creating an element for each level of
        // for example: <dcterms:spatial><Point><pos>
        if (basicString != null && !isBlank(uri))
            getTarget().getEmdCoverage().getTermsSpatial().add(basicString);
    }
}
