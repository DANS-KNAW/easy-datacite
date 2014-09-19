package nl.knaw.dans.pf.language.ddm.datehandlers;

import nl.knaw.dans.pf.language.ddm.handlertypes.BasicDateHandler;
import nl.knaw.dans.pf.language.emd.types.BasicDate;

import org.xml.sax.SAXException;

public class DcDateHandler extends BasicDateHandler {
    @Override
    public void finishElement(final String uri, final String localName) throws SAXException {
        final BasicDate date = createDate(uri, localName);
        if (date != null)
            getTarget().getEmdDate().getDcDate().add(date);
    }
}
