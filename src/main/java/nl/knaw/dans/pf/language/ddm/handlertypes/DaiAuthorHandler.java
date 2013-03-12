package nl.knaw.dans.pf.language.ddm.handlertypes;

import java.net.URI;
import java.net.URISyntaxException;

import nl.knaw.dans.common.lang.id.DAI;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.Author;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;

import org.xml.sax.SAXException;

public abstract class DaiAuthorHandler extends CrosswalkHandler<EasyMetadata>
{
    protected Author createDaiAuthor(final String uri, final String localName) throws SAXException
    {
        final Author author = new Author();
        final String value = getCharsSinceStart().trim();
        final String attribute = getAttribute("", "DAI").trim();
        if (value.length() == 0 || attribute.length() == 0)
            return null;
        setDAI(author, attribute);
        author.setSurname(value);
        return author;
    }

    protected void setDAI(final Author author, final String attribute) throws SAXException
    {
        if (attribute.startsWith("info"))
        {
            final String[] strings = attribute.split("/");
            final String entityId = strings[strings.length - 1];
            final String idSys = attribute.replaceAll(entityId + "$", "");
            author.setEntityId(entityId, EmdConstants.SCHEME_DAI);
            author.setIdentificationSystem(toURI(idSys));
        }
        else
        {
            final DAI dai = toDAI(attribute);
            author.setEntityId(dai.getIdentifier(), EmdConstants.SCHEME_DAI);
            author.setIdentificationSystem(toURI("info:eu-repo/dai/nl/"));
        }
    }

    private DAI toDAI(final String attribute) throws SAXException
    {
        try
        {
            return new DAI(attribute);
        }
        catch (final IllegalArgumentException e)
        {
            error(e.getMessage());
            return null;
        }
    }

    private URI toURI(final String string) throws SAXException
    {
        try
        {
            return new URI(string);
        }
        catch (final URISyntaxException e)
        {
            error(e.getMessage());
            return null;
        }
    }
}
