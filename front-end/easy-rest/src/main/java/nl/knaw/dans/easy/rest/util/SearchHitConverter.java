package nl.knaw.dans.easy.rest.util;

import java.util.List;

import nl.knaw.dans.common.lang.search.simple.SimpleSearchHit;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;

/**
 * A class for converting search hits to XML.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
public class SearchHitConverter extends SimpleXmlWriter {

    /**
     * Create a XML representation of a list of search hits.
     * 
     * @param hits
     *        A list of search hits.
     * @return A String containing XML representing the hits.
     */
    public static String convert(List<?> hits) {
        String xml = startNode("hits");
        for (Object o : hits) {
            SimpleSearchHit<?> hit = (SimpleSearchHit<?>) o;
            xml += convert(hit);
        }
        xml += endNode("hits");
        return xml;
    }

    /**
     * Convert a single search hit to XML.
     * 
     * @param hit
     *        The search hit.
     * @return A String containing XML representing the hit.
     */
    public static String convert(SimpleSearchHit<?> hit) {
        EasyDatasetSB hitData = (EasyDatasetSB) hit.getData();
        String xml = startNode("hit");
        xml += addNode("title", hitData.getDcTitleSortable());
        xml += addNode("storeId", hitData.getStoreId());
        xml += addNode("creator", hitData.getDcCreatorSortable());
        xml += addNode("dateCreated", hitData.getDateCreatedFormatted());
        if (hitData.getDcDescription() != null) {
            for (String description : hitData.getDcDescription()) {
                xml += addNode("description", description);
            }
        }
        if (hitData.getDcIdentifier() != null) {
            for (String id : hitData.getDcIdentifier()) {
                xml += addNode("identifier", id);
            }
        }
        if (hitData.getDcCoverage() != null) {
            for (String coverage : hitData.getDcCoverage()) {
                xml += addNode("coverage", coverage);
            }
        }
        xml += addNode("accessCategory", hitData.getAccessCategory().toString());
        xml += endNode("hit");
        return xml;
    }

}
