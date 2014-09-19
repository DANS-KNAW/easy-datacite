package nl.knaw.dans.pf.language.ddm.datehandlers;

import nl.knaw.dans.pf.language.ddm.handlertypes.IsoDateHandler;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

import org.xml.sax.SAXException;

public class EasIssuedHandler extends IsoDateHandler {
    @Override
    public void finishElement(final String uri, final String localName) throws SAXException {
        final IsoDate date = createDate(uri, localName);
        if (date != null)
            getTarget().getEmdDate().getEasIssued().add(date);
    }
}
