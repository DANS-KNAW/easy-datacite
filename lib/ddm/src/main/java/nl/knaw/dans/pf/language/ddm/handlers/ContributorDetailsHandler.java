package nl.knaw.dans.pf.language.ddm.handlers;

import nl.knaw.dans.pf.language.ddm.handlertypes.AuthorDetailsHandler;
import nl.knaw.dans.pf.language.emd.types.Author;

import org.xml.sax.Attributes;

public class ContributorDetailsHandler extends AuthorDetailsHandler {
    @Override
    public void initFirstElement(final String uri, final String localName, final Attributes attributes) {
        author = new Author();
        getTarget().getEmdContributor().getEasContributor().add(author);
    }
}
