package nl.knaw.dans.pf.language.ddm.handlers;

import nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace;
import nl.knaw.dans.pf.language.ddm.handlertypes.BasicStringHandler;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import nl.knaw.dans.pf.language.emd.types.EmdConstants;
import org.xml.sax.SAXException;

public class SubjectHandler extends BasicStringHandler {

    private final NameSpace namespace;

    public SubjectHandler() {
        this.namespace = null;
    }

    public SubjectHandler(NameSpace namespace) {
        super(null, EmdConstants.SCHEME_ID_SUBJECT);
        this.namespace = namespace;
    }

    @Override
    protected void finishElement(final String uri, final String localName) throws SAXException {
        final BasicString basicString = createBasicString(uri, localName);
        if (basicString != null) {
            if (this.namespace != null)
                basicString.setScheme(this.namespace.prefix.toUpperCase());
            getTarget().getEmdSubject().getDcSubject().add(basicString);
        }
    }
}
