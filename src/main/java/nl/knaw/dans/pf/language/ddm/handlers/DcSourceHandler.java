package nl.knaw.dans.pf.language.ddm.handlers;

import nl.knaw.dans.pf.language.ddm.handlertypes.BasicIdentifierHandler;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

import org.xml.sax.SAXException;

public class DcSourceHandler extends BasicIdentifierHandler
{
    @Override
    protected void finishElement(final String uri, final String localName) throws SAXException
    {
        BasicIdentifier value = createIdentifier(uri, localName);
        if (value != null)
            getTarget().getEmdSource().getDcSource().add(value);
    }
}
