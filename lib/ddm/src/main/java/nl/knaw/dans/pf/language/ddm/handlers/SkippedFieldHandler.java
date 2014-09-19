package nl.knaw.dans.pf.language.ddm.handlers;

import org.xml.sax.SAXException;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;

public class SkippedFieldHandler extends CrosswalkHandler<EasyMetadata> {
    private String warn;

    public SkippedFieldHandler(String string) {
        this.warn = string;
    }

    @Override
    protected void finishElement(final String uri, final String localName) throws SAXException {
        // in this case we might want qName of the endElement
        if (warn != null)
            warning("skipped " + uri + " " + localName + " [" + warn + "]");
    }
}
