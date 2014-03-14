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
        final String value = getCharsSinceStart().trim();
        final String attribute = getAttribute("", "DAI").trim();
        if (value.length() == 0 || attribute.length() == 0)
            return null;
        final Author author = new Author();
        author.setSurname(value);
        return setDAI(author, attribute);
    }

    protected Author setDAI(final Author author, final String value) throws SAXException
    {
        if (value.startsWith("info"))
        {
            final String[] strings = value.split("/");
            final String entityId = strings[strings.length - 1];
            final String idSys = value.replaceAll(entityId + "$", "");
            author.setEntityId(entityId, EmdConstants.SCHEME_DAI);
            author.setIdentificationSystem(toURI(idSys));
        }
        else
        {
            author.setEntityId(value, EmdConstants.SCHEME_DAI);
            author.setIdentificationSystem(toURI("info:eu-repo/dai/nl/"));
        }
        if (!DAI.isValid(author.getEntityId()))
        {
            error("invalid DAI " + author.getEntityId());
            return null;
        }
        return author;
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
