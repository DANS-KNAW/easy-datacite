package nl.knaw.dans.pf.language.ddm.handlermaps;

import nl.knaw.dans.pf.language.emd.types.EmdConstants;

public enum NameSpace {
    DC("dc", "http://purl.org/dc/elements/1.1/"), //
    DC_TERMS("dcterms", "http://purl.org/dc/terms/"), //
    DCMITYPE("dcmitype", "http://purl.org/dc/dcmitype/"), //
    DCX("dcx", "http://easy.dans.knaw.nl/schemas/dcx/"), //
    GML("gml", "http://www.opengis.net/gml"), //
    DCX_DAI("dcx-dai", "http://easy.dans.knaw.nl/schemas/dcx/dai/", "http://easy.dans.knaw.nl/schemas/dcx/2012/10/dcx-dai.xsd"), //
    DCX_GML("dcx-gml", "http://easy.dans.knaw.nl/schemas/dcx/gml/", "http://easy.dans.knaw.nl/schemas/dcx/2012/10/dcx-gml.xsd"), //
    DDM("ddm", "http://easy.dans.knaw.nl/schemas/md/ddm/", "http://easy.dans.knaw.nl/schemas/md/2015/12/ddm.xsd"), //
    XSI("xsi", "http://www.w3.org/2001/XMLSchema-instance", "http://easy.dans.knaw.nl/schemas/md/2015/12/ddm.xsd"), //
    NARCIS_TYPE("narcis", "http://easy.dans.knaw.nl/schemas/vocab/narcis-type/", "http://easy.dans.knaw.nl/schemas/vocab/2015/narcis-type.xsd",
            EmdConstants.SCHEME_ID_DISCIPLINES), //
    IDENTIFIER_TYPE("id-type", "http://easy.dans.knaw.nl/schemas/vocab/identifier-type/", "http://easy.dans.knaw.nl/schemas/vocab/2015/identifier-type.xsd"), //
    ABR("abr", "http://www.den.nl/standaard/166/Archeologisch-Basisregister/", "http://easy.dans.knaw.nl/schemas/vocab/2012/10/abr-type.xsd",
            "archaeology.dcterms.temporal");

    public final String uri;
    public final String prefix;
    public final String xsd;
    public final String schemeId;

    private NameSpace(final String prefix, final String uri) {
        this.prefix = prefix;
        this.uri = uri;
        this.xsd = null;
        this.schemeId = null;
    }

    private NameSpace(final String prefix, final String uri, final String xsd) {
        this.prefix = prefix;
        this.uri = uri;
        this.xsd = xsd;
        this.schemeId = null;
    }

    private NameSpace(final String prefix, final String uri, final String xsd, final String schemeId) {
        this.prefix = prefix;
        this.schemeId = schemeId;
        this.uri = uri;
        this.xsd = xsd;
    }
}
