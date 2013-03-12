package nl.knaw.dans.pf.language.ddm.handlertypes;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.BasicDate;
import nl.knaw.dans.pf.language.emd.types.InvalidLanguageTokenException;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;

import org.xml.sax.SAXException;

public abstract class BasicDateHandler extends CrosswalkHandler<EasyMetadata>
{
    protected BasicDate createDate(final String uri, final String localName) throws SAXException
    {
        final String value = getCharsSinceStart().trim();
        if (value.length() != 0)
        {
            try
            {
                return new BasicDate(value.trim());
            }
            catch (final InvalidLanguageTokenException e)
            {
                error(e.getMessage());
            }
        }
        return null;
    }
}
