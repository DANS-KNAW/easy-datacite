package nl.knaw.dans.pf.language.ddm.handlers;

import nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.Spatial;
import nl.knaw.dans.pf.language.emd.types.Spatial.Point;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EasSpatialHandler extends CrosswalkHandler<EasyMetadata>
{
    public static final String WGS84_4326 = "http://www.opengis.net/def/crs/EPSG/0/4326";
    private String description = null;
    private Point lower, upper, pos;

    @Override
    public void initFirstElement(final String uri, final String localName, final Attributes attributes)
    {
        description = null;
        lower = upper = pos = null;
    }

    @Override
    protected void finishElement(final String uri, final String localName) throws SAXException
    {
        if ("description".equals(localName))
        {
            // TODO point description provided in documentation but not in XSD
            description = getCharsSinceStart().trim();
        }
        else if ("pos".equals(localName))
            pos = wgs84Point();
        else if ("lowerCorner".equals(localName))
            lower = wgs84Point();
        else if ("upperCorner".equals(localName))
            upper = wgs84Point();
        else if ("Point".equals(localName))
        {
            if (pos != null)
                getTarget().getEmdCoverage().getEasSpatial().add(new Spatial(description, pos));
        }
        else if ("Envelope".equals(localName))
        {
            if (lower != null && upper != null)
            {
                final Spatial.Box box = new Spatial.Box(WGS84_4326, upper.getY(), upper.getX(), lower.getY(), lower.getX());
                getTarget().getEmdCoverage().getEasSpatial().add(new Spatial(description, box));
            }
        }
        // other types than point/box not supported by EMD: don't warn
        return;
    }

    private Point wgs84Point() throws SAXException
    {
        final String type = getAttribute(NameSpace.XSI.uri, "type");
        if (type != null)
            warning("ignored: not yet implemented");
        // http://wiki.esipfed.org/index.php/CRS_Specification
        // urn:ogc:def:crs:EPSG::4326 has coordinate order latitude(north), longitude(east)
        final String[] yx = getCharsSinceStart().trim().split(" ");
        if (yx.length < 2)
        {
            error("expected two floats separated with a space");
            return null;
        }
        return new Spatial.Point(WGS84_4326, yx[1], yx[0]);
    }
}
