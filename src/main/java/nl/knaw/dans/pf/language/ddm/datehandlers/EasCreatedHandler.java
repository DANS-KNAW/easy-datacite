package nl.knaw.dans.pf.language.ddm.datehandlers;

import nl.knaw.dans.pf.language.ddm.handlertypes.IsoDateHandler;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

import org.xml.sax.SAXException;

public class EasCreatedHandler extends IsoDateHandler
{
    @Override
    public void finishElement(final String uri, final String localName) throws SAXException
    {
        final IsoDate isoDate = createDate(uri, localName);
        if (isoDate == null)
            return;
        getTarget().getEmdDate().getEasCreated().add(isoDate);
    }
}
