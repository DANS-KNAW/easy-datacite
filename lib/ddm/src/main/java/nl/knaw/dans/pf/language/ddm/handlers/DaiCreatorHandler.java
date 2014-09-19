package nl.knaw.dans.pf.language.ddm.handlers;

import nl.knaw.dans.pf.language.ddm.handlertypes.DaiAuthorHandler;
import nl.knaw.dans.pf.language.emd.types.Author;

import org.xml.sax.SAXException;

public class DaiCreatorHandler extends DaiAuthorHandler {
    @Override
    protected void finishElement(final String uri, final String localName) throws SAXException {
        final Author author = createDaiAuthor(uri, localName);
        if (author != null)
            getTarget().getEmdCreator().getEasCreator().add(author);
    }
}
