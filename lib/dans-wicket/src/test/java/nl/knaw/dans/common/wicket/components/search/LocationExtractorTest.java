package nl.knaw.dans.common.wicket.components.search;

import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.wicket.components.search.results.LocationExtractor;
import nl.knaw.dans.libs.map.LonLat;

import org.junit.Test;

import static org.junit.Assert.*;

public class LocationExtractorTest {
    final static double DELTA = 1.0E-6; // use this to ignore roundoffs and machine accuracy

    // RD central point (x=155000 y=463000) is inside the 'Onze Lieve Vrouwetoren' in Amersfoort, The Netherlands.
    // WGS84 for this central point: x=52.155174 y=5.387206
    final static LonLat centerRD = new LonLat(5.387206, 52.155174);

    private static double[] lonLatAsArray(final LonLat lonLat) {
        return new double[] {lonLat.getLon(), lonLat.getLat()};
    }

    @Test
    public void testValidRDPoint() {
        List<String> dcCoverage = Arrays.asList("scheme=RD x=155000 y=463000");

        LonLat lonLat = LocationExtractor.getLocation(dcCoverage);
        assertArrayEquals(lonLatAsArray(centerRD), lonLatAsArray(lonLat), DELTA);
    }

    @Test
    public void testValidRDBox() {
        List<String> dcCoverage = Arrays.asList("scheme=RD north=463001 east=155001 south=462999 west=154999");

        LonLat lonLat = LocationExtractor.getLocation(dcCoverage);
        assertArrayEquals(lonLatAsArray(centerRD), lonLatAsArray(lonLat), DELTA);
    }

    @Test
    public void testValidWGS84Point() {
        List<String> dcCoverage = Arrays.asList("scheme=http://www.opengis.net/def/crs/EPSG/0/4326 x=52.155174 y=5.387206");

        LonLat lonLat = LocationExtractor.getLocation(dcCoverage);
        assertArrayEquals(lonLatAsArray(centerRD), lonLatAsArray(lonLat), DELTA);
    }

    @Test
    public void testValidWGS84Box() {
        List<String> dcCoverage = Arrays
                .asList("scheme=http://www.opengis.net/def/crs/EPSG/0/4326 north=5.387207 east=52.155175 south=5.387205 west=52.155173");

        LonLat lonLat = LocationExtractor.getLocation(dcCoverage);
        assertArrayEquals(lonLatAsArray(centerRD), lonLatAsArray(lonLat), DELTA);
    }

    @Test
    public void testEmptyCoverage() {
        List<String> dcCoverage = null;
        LonLat lonLat = LocationExtractor.getLocation(dcCoverage);
        assertNull(lonLat);

        dcCoverage = Arrays.asList("");
        lonLat = LocationExtractor.getLocation(dcCoverage);
        assertNull(lonLat);
    }

    @Test
    public void testUnknownScheme() {
        List<String> dcCoverage = Arrays.asList("scheme=unknown a=0 b=0");
        LonLat lonLat = LocationExtractor.getLocation(dcCoverage);
        assertNull(lonLat);
    }

    @Test
    public void testInvalidRDPoint() {
        List<String> dcCoverage = Arrays.asList("scheme=RD x=invalid y=invalid");

        LonLat lonLat = LocationExtractor.getLocation(dcCoverage);
        assertNull(lonLat);
    }

    @Test
    public void testInvalidWGS84Point() {
        List<String> dcCoverage = Arrays.asList("scheme=http://www.opengis.net/def/crs/EPSG/0/4326 x=invalid y=invalid");

        LonLat lonLat = LocationExtractor.getLocation(dcCoverage);
        assertNull(lonLat);
    }

    @Test
    public void testInvalidRDBox() {
        List<String> dcCoverage = Arrays.asList("scheme=RD north=invalid east=invalid south=invalid west=invalid");

        LonLat lonLat = LocationExtractor.getLocation(dcCoverage);
        assertNull(lonLat);
    }

    @Test
    public void testInvalidWGS84Box() {
        List<String> dcCoverage = Arrays.asList("scheme=http://www.opengis.net/def/crs/EPSG/0/4326 north=invalid east=invalid south=invalid west=invalid");

        LonLat lonLat = LocationExtractor.getLocation(dcCoverage);
        assertNull(lonLat);
    }

    @Test
    public void testSkipInvalidButExtractValidRDPoint() {
        List<String> dcCoverage = Arrays.asList("scheme=RD x=invalid y=invalid", "scheme=RD x=155000 y=463000");

        LonLat lonLat = LocationExtractor.getLocation(dcCoverage);
        assertArrayEquals(lonLatAsArray(centerRD), lonLatAsArray(lonLat), DELTA);
    }
}
