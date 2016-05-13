package nl.knaw.dans.pf.language.ddm.handlers;

import nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.Spatial;
import nl.knaw.dans.pf.language.emd.types.Spatial.Point;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EasSpatialHandler extends CrosswalkHandler<EasyMetadata> {
    public static final String WGS84_4326 = "http://www.opengis.net/def/crs/EPSG/0/4326";
    private static final String SRS_NAME = "srsName";
    private String description = null;
    private Point lower, upper, pos;

    /**
     * Proper processing requires pushing/popping and inheriting the attribute, so we skip for the current implementation
     */
    private boolean foundSRS = false;

    @Override
    public void initFirstElement(final String uri, final String localName, final Attributes attributes) {
        description = null;
        lower = upper = pos = null;
        foundSRS = false;
        checkSRS(attributes);
    }

    @Override
    public void initElement(final String uri, final String localName, final Attributes attributes) {
        checkSRS(attributes);
    }

    private void checkSRS(final Attributes attributes) {
        for (int i = 0; i < attributes.getLength(); i++)
            foundSRS = foundSRS || SRS_NAME.equals(attributes.getLocalName(i));
    }

    @Override
    protected void finishElement(final String uri, final String localName) throws SAXException {
        if ("description".equals(localName))
            description = getCharsSinceStart().trim();
        else if ("pos".equals(localName))
            pos = wgs84Point();
        else if ("lowerCorner".equals(localName))
            lower = wgs84Point();
        else if ("upperCorner".equals(localName))
            upper = wgs84Point();
        else if ("Point".equals(localName)) {
            if (pos != null && !foundSRS)
                getTarget().getEmdCoverage().getEasSpatial().add(new Spatial(description, pos));
        } else if ("Envelope".equals(localName)) {
            if (lower != null && upper != null && foundSRS) {
                getTarget().getEmdCoverage().getEasSpatial().add(new Spatial(description, createBox()));
            }
        }
        // other types than point/box not supported by EMD: don't warn
        return;
    }

    private Spatial.Box createBox() throws SAXException {
        final float upperY = Float.parseFloat(upper.getY());
        final float upperX = Float.parseFloat(upper.getX());
        final float lowerY = Float.parseFloat(lower.getY());
        final float lowerX = Float.parseFloat(lower.getX());
        final String n = "" + (upperY > lowerY ? upperY : lowerY);
        final String s = "" + (upperY < lowerY ? upperY : lowerY);
        final String e = "" + (upperX > lowerX ? upperX : lowerX);
        final String w = "" + (upperX < lowerX ? upperX : lowerX);
        return new Spatial.Box(WGS84_4326, n, e, s, w);
    }

    private Point wgs84Point() throws SAXException {
        final String type = getAttribute(NameSpace.XSI.uri, "type");
        if (type != null)
            warning("ignored: not yet implemented");
        // http://wiki.esipfed.org/index.php/CRS_Specification
        // urn:ogc:def:crs:EPSG::4326 has coordinate order latitude(north), longitude(east)
        final String[] yx = getCharsSinceStart().trim().split(" ");
        if (yx.length < 2) {
            error("expected at least two floats separated with a space");
            return null;
        }
        return new Spatial.Point(WGS84_4326, yx[1], yx[0]);
    }
}
