package nl.knaw.dans.common.wicket.components.search.results;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.libs.map.LonLat;

public class LocationExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationExtractor.class);

    /**
     * Extract the location coordinates (point) if available.
     * 
     * @param dcCoverage
     *        Should be the dcCoverage from a DatasetSB (Search results)
     * @return The location coordinates
     */
    public static LonLat getLocation(List<String> dcCoverage) {
        LonLat lonlat = null;
        if (dcCoverage != null) {
            for (String coverageItem : dcCoverage) {
                // Note that parsing should be fast
                // and not trying to do complex handling of wrongly formatted input
                if (coverageItem.startsWith("scheme=")) { // we have a scheme
                    String[] coords = coverageItem.split(" ");
                    String scheme = coords[0].substring("scheme=".length());
                    try {
                        if (scheme.contentEquals("RD")) {
                            lonlat = getLocationFromRD(coords);
                        } else if (scheme.contentEquals("http://www.opengis.net/def/crs/EPSG/0/4326")) {
                            lonlat = getLocationFromEPSG4326(coords);
                        } else {
                            LOGGER.debug("Cannot do location extraction for scheme: \"" + scheme + "\"");
                            continue; // just ignore and skip this item
                        }
                        break; // first one found is returned
                    }
                    catch (Exception e) {
                        LOGGER.debug("Could not extract location from: \"" + coverageItem + "\" " + e.getMessage());
                        continue; // just ignore and skip this item
                    }
                }
            }
        }
        return lonlat;
    }

    // EPSG4326 aka WGS84
    private static LonLat getLocationFromEPSG4326(String[] coords) throws IllegalArgumentException {
        LonLat lonlat = null;
        // Note that the first array element is the scheme string
        if (coords.length == 3) {
            // a point with two coordinates after the scheme
            try {
                // extract x and y
                Double x = Double.parseDouble(coords[1].substring(2)); // skip 'x='
                Double y = Double.parseDouble(coords[2].substring(2)); // skip 'y='
                if (isValidRangeWGS84(x, y)) {
                    lonlat = new LonLat(y, x);
                }
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unable to extract point coordinates from: " + coords[1] + ", " + coords[2], e);
            }
        } else if (coords.length == 5) {
            // must be a box with four coordinates
            try {
                // extract north, east, south, west
                Double north = Double.parseDouble(coords[1].substring(6)); // skip 'north='
                Double east = Double.parseDouble(coords[2].substring(5)); // skip 'east='
                Double south = Double.parseDouble(coords[3].substring(6)); // skip 'south='
                Double west = Double.parseDouble(coords[4].substring(5)); // skip 'west='
                // use centerpoint
                Double x = (east + west) * 0.5;
                Double y = (north + south) * 0.5;
                if (isValidRangeWGS84(x, y)) {
                    lonlat = new LonLat(y, x);
                }
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unable to extract box coordinates from: " + coords[1] + ", " + coords[2] + ", " + coords[3] + ", "
                        + coords[4], e);
            }
        } else {
            new IllegalArgumentException("Incorrect number of array elements, need 3 or 5, was: " + coords.length);
        }
        return lonlat;
    }

    private static LonLat getLocationFromRD(String[] coords) throws IllegalArgumentException {
        LonLat lonlat = null;
        // Note that the first array element is the scheme string
        if (coords.length == 3) {
            // a point with two coordinates after the scheme
            try {
                // extract x and y
                Double x = Double.parseDouble(coords[1].substring(2)); // skip 'x='
                Double y = Double.parseDouble(coords[2].substring(2)); // skip 'y='
                lonlat = convertRDtoWGS84(x, y);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unable to extract point coordinates from: " + coords[1] + ", " + coords[2], e);
            }
        } else if (coords.length == 5) {
            // must be a box with four coordinates
            try {
                // extract north, east, south, west
                Double north = Double.parseDouble(coords[1].substring(6)); // skip 'north='
                Double east = Double.parseDouble(coords[2].substring(5)); // skip 'east='
                Double south = Double.parseDouble(coords[3].substring(6)); // skip 'south='
                Double west = Double.parseDouble(coords[4].substring(5)); // skip 'west='
                // use centerpoint
                Double x = (east + west) * 0.5;
                Double y = (north + south) * 0.5;
                lonlat = convertRDtoWGS84(x, y);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unable to extract box coordinates from: " + coords[1] + ", " + coords[2] + ", " + coords[3] + ", "
                        + coords[4], e);
            }
        } else {
            new IllegalArgumentException("Incorrect number of array elements, need 3 or 5, was: " + coords.length);
        }
        return lonlat;
    }

    private static boolean isValidRangeRD(Double x, Double y) {
        // check the bounding rectangle (coordinate range) for RD; The Netherlands
        if (x >= -7000.0 && x <= 300000.0 && y >= 289000.0 && y <= 629000.0) {
            return true;
        } else {
            LOGGER.debug("RD coordinates not in valid range: " + x + ", " + y);
            return false;
        }
    }

    private static boolean isValidRangeWGS84(Double x, Double y) {
        // should be on the globe, not sure if cyclic redundancy is allowed
        if (x >= -90.0 && x <= 90.0 && y >= -180.0 && y <= 180.0) {
            return true;
        } else {
            LOGGER.debug("WGS84 coordinates not in valid range: " + x + ", " + y);
            return false;
        }
    }

    private static LonLat convertRDtoWGS84(Double x, Double y) {
        if (!isValidRangeRD(x, y)) {
            return null;
        }
        // convert to WGS84, like in the OAI xslt
        // RD x, y to WGS84 latitude longitude. See: http://www.regiolab-delft.nl/?q=node/36
        Double p = (x - 155000.0) * 1.0E-5;
        Double q = (y - 463000.0) * 1.0E-5;
        // p2 and q2 for speedup and readability
        Double p2 = p * p;
        Double q2 = q * q;
        Double df = ((q * 3235.65389) + (p2 * -32.58297) + (q2 * -0.24750) + (p2 * q * -0.84978) + (q2 * q * -0.06550) + (p2 * q2 * -0.01709) + (p * -0.00738)
                + (p2 * p2 * 0.00530) + (p2 * q2 * q * -0.00039) + (p2 * p2 * q * 0.00033) + (p * q * -0.00012)) / 3600.0;
        Double dl = ((p * 5260.52916) + (p * q * 105.94684) + (p * q2 * 2.45656) + (p2 * p * -0.81885) + (p * q * q2 * 0.05594) + (p2 * p * q * -0.05607)
                + (q * 0.01199) + (p * p2 * q2 * -0.00256) + (p * q2 * q2 * 0.00128) + (q2 * 0.00022) + (p2 * -0.00022) + (p2 * p2 * p * 0.00026)) / 3600.0;
        Double lat = 52.15517440 + df;
        Double lon = 5.387206210 + dl;
        // roundoff at 6 decimals, maybe not needed but gives smaller strings for the json output
        lat = (Math.round(lat * 1.0E6)) * 1.0E-6;
        lon = (Math.round(lon * 1.0E6)) * 1.0E-6;
        return new LonLat(lon, lat);
    }
}
