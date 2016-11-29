package nl.knaw.dans.pf.language.ddm.handlermaps;

public enum NameSpace {
    DC("dc", "http://purl.org/dc/elements/1.1/", "http://dublincore.org/schemas/xmls/qdc/dc.xsd"), //
    DC_TERMS("dcterms", "http://purl.org/dc/terms/", "http://dublincore.org/schemas/xmls/qdc/dcterms.xsd"), //
    DCMITYPE("dcmitype", "http://purl.org/dc/dcmitype/", "http://dublincore.org/schemas/xmls/qdc/dcmitype.xsd"), //
    DCX("dcx", "http://easy.dans.knaw.nl/schemas/dcx/", "http://easy.dans.knaw.nl/schemas/dcx/2012/10/dcx.xsd"), //
    GML("gml", "http://www.opengis.net/gml", "http://schemas.opengis.net/gml/3.2.1/gml.xsd"), //
    DCX_DAI("dcx-dai", "http://easy.dans.knaw.nl/schemas/dcx/dai/", "http://easy.dans.knaw.nl/schemas/dcx/2012/10/dcx-dai.xsd"), //
    DCX_GML("dcx-gml", "http://easy.dans.knaw.nl/schemas/dcx/gml/", "http://easy.dans.knaw.nl/schemas/dcx/2016/dcx-gml.xsd"), //
    DDM("ddm", "http://easy.dans.knaw.nl/schemas/md/ddm/", "http://easy.dans.knaw.nl/schemas/md/2016/ddm.xsd"), //
    XSI("xsi", "http://www.w3.org/2001/XMLSchema-instance", "https://www.w3.org/2001/XMLSchema-instance"), //
    NARCIS_TYPE("narcis", "http://easy.dans.knaw.nl/schemas/vocab/narcis-type/", "http://easy.dans.knaw.nl/schemas/vocab/2015/narcis-type.xsd"), //
    IDENTIFIER_TYPE("id-type", "http://easy.dans.knaw.nl/schemas/vocab/identifier-type/", "http://easy.dans.knaw.nl/schemas/vocab/2015/identifier-type.xsd"), //
    ABR("abr", "http://www.den.nl/standaard/166/Archeologisch-Basisregister/", "http://easy.dans.knaw.nl/schemas/vocab/2012/10/abr-type.xsd");

    public final String uri;
    public final String prefix;
    public final String xsd;

    private NameSpace(final String prefix, final String uri, final String xsd) {
        this.prefix = prefix;
        this.uri = uri;
        this.xsd = xsd;
    }
}
