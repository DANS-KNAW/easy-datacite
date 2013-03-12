package nl.knaw.dans.pf.language.ddm.handlermaps;

import nl.knaw.dans.pf.language.ddm.api.DDMValidator;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

public enum NameSpace
{
    DC("dc", "http://purl.org/dc/elements/1.1/"), //
    DC_TERMS("dcterms", "http://purl.org/dc/terms/"), //
    DCMITYPE("dcmitype", "http://purl.org/dc/dcmitype/"), //
    DCX("dcx", "http://easy.dans.knaw.nl/schemas/dcx/"), //
    DCX_DAI("dcx-dai", "http://easy.dans.knaw.nl/schemas/dcx/dai/"), //
    GML("dcx-gml","http://easy.dans.knaw.nl/schemas/dcx/gml/","http://eof12.dans.knaw.nl/schemas/dcx/2012/10/dcx-gml.xsd"),//
    DDM("ddm", "http://easy.dans.knaw.nl/schemas/md/ddm/", DDMValidator.SCHEMA_LOCATION), //
    XSI("xsi", "http://www.w3.org/2001/XMLSchema-instance", "http://easy.dans.knaw.nl/schemas/md/2012/11/ddm.xsd"), //
    NARCIS_TYPE("narcis", EmdConstants.SCHEME_ID_DISCIPLINES, "http://easy.dans.knaw.nl/schemas/vocab/narcis-type/", "http://easy.dans.knaw.nl/schemas/vocab/2012/10/narcis-type.xsd"), //
    ABR("abr", "archaeology.dcterms.temporal","http://www.den.nl/standaard/166/Archeologisch-Basisregister/", "http://easy.dans.knaw.nl/schemas/vocab/2012/10/abr-type.xsd"), //
    ;
    public final String uri;
    public final String prefix;
    public final String xsd;
    public String schemeId;

    private NameSpace(final String prefix, final String uri)
    {
        this.prefix = prefix;
        this.uri = uri;
        this.xsd = null;
    }

    private NameSpace(final String prefix, final String uri, final String xsd)
    {
        this.prefix = prefix;
        this.uri = uri;
        this.xsd = xsd;
    }

    private NameSpace(final String prefix, final String schemeId,final String uri, final String xsd)
    {
        this.prefix = prefix;
        this.schemeId = schemeId;
        this.uri = uri;
        this.xsd = xsd;
    }
}
