package nl.knaw.dans.pf.language.ddm.handlertypes;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.InvalidLanguageTokenException;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;

import org.xml.sax.SAXException;

public abstract class BasicIdentifierHandler extends CrosswalkHandler<EasyMetadata> {
    protected BasicIdentifier createIdentifier(final String uri, final String localName) throws SAXException {
        final String value = getCharsSinceStart().trim();
        if (value.length() != 0) {
            try {
                return new BasicIdentifier(value.trim());
            }
            catch (final InvalidLanguageTokenException e) {
                error(e.getMessage());
            }
        }
        return null;
    }

}
