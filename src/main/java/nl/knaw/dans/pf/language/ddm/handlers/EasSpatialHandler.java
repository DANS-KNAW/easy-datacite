package nl.knaw.dans.pf.language.ddm.handlers;

import nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.Spatial;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EasSpatialHandler extends CrosswalkHandler<EasyMetadata>
{
    public static final String WGS84_4326 = "http://www.opengis.net/def/crs/EPSG/0/4326";
    private String scheme = null;
    private String description;
    private boolean typeIsPoint = false;
    private boolean typeIsBox = false;

    @Override
    public void initFirstElement(final String uri, final String localName, final Attributes attributes)
    {
        scheme = null;
        description = null;
        typeIsPoint = false;
        typeIsBox = false;
    }

    @Override
    public void initElement(final String uri, final String localName, final Attributes attributes)
    {
        if (scheme == null)
            scheme = attributes.getValue(NameSpace.XSI.uri, "type");
        if ("Point".equals(localName))
            typeIsPoint = true;
        if ("Box".equals(localName))
            typeIsBox = true;
    }

    @Override
    protected void finishElement(final String uri, final String localName) throws SAXException
    {
        if (!"description".equals(localName))
        {
            // TODO point description provided in documentation but not in XSD
            description = getCharsSinceStart().trim();
        }
        if (!"pos".equals(localName))
            return;
        if (typeIsPoint && (scheme == null || WGS84_4326.equals(scheme)))
        {
            // WGS84
            // http://wiki.esipfed.org/index.php/CRS_Specification
            // urn:ogc:def:crs:EPSG::4326 has coordinate order latitude(north), longitude(east)
            final String[] yx = getCharsSinceStart().trim().split(" ");
            if (yx.length < 2)
            {
                error("expected two floats separated with a space");
                return;
            }
            final Spatial.Point point = new Spatial.Point(WGS84_4326, yx[1], yx[0]);
            getTarget().getEmdCoverage().getEasSpatial().add(new Spatial(description, point));
        }
        else if (typeIsBox || typeIsPoint)
            warning("ignored: not yet implemented");
        // TODO box and other schemes than WGS84
        // other types than point/box not supported by EMD: don't warn
    }
}
