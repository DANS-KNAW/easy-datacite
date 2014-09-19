package nl.knaw.dans.pf.language.ddm.handlers;

import java.util.List;

import nl.knaw.dans.pf.language.ddm.handlertypes.BasicStringHandler;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.xml.sax.SAXException;

public class AccessRightsHandler extends BasicStringHandler {
    @Override
    public final void finishElement(final String uri, final String localName) throws SAXException {
        final List<BasicString> accessRightsList = getTarget().getEmdRights().getTermsAccessRights();
        final BasicString basicString = createBasicString(uri, localName);
        // skip access rights beyond the ddm:profile
        if (accessRightsList.size() <= 0 && basicString != null)
            accessRightsList.add(basicString);
    }
}
