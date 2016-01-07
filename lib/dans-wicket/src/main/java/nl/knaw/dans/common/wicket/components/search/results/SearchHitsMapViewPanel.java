package nl.knaw.dans.common.wicket.components.search.results;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchHit;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.wicket.components.search.SearchPanel;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.libs.map.LazyLoadingMapViewerPanel;
import nl.knaw.dans.libs.map.LonLat;
import nl.knaw.dans.libs.map.Marker;

import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchHitsMapViewPanel extends SearchPanel {
    private static final long serialVersionUID = -5091780572108146302L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchHitsMapViewPanel.class);

    private SearchResultConfig config;
    LazyLoadingMapViewerPanel viewer = null;
    Component indicator = null;

    private Label markersLoadedMsg = null;
    private final static int MAX_NUM_MARKERS = 5000;

    public SearchResultConfig getConfig() {
        return config;
    }

    public SearchHitsMapViewPanel(String id, SearchModel searchModel, final SearchResultConfig config) {
        super(id, searchModel);
        this.config = config;

        markersLoadedMsg = new Label("markersLoadedMsg", "");
        markersLoadedMsg.setOutputMarkupId(true);
        add(markersLoadedMsg);

        initGeoViewer();
    }

    private void initGeoViewer() {
        viewer = new LazyLoadingMapViewerPanel("geoviewer") {
            private static final long serialVersionUID = 2887353735740669595L;

            @Override
            protected List<Marker> produceMarkers() {
                return getEasyMarkers(getSearchModel());
            }

            @Override
            public void addMarkers(List<Marker> markers, AjaxRequestTarget target) {
                int numHits = getSearchResult().getHits().size();
                String msg = "";
                if (markers.size() >= MAX_NUM_MARKERS) {
                    markers.subList(MAX_NUM_MARKERS, markers.size()).clear();
                    msg = String.format("Showing the first %d locations of %d results, please reduce the results to see them on the map", MAX_NUM_MARKERS,
                            numHits);
                } else {
                    msg = String.format("%d of %d results have a location presented on the map", markers.size(), numHits);
                }
                super.addMarkers(markers, target);

                Label newloadedMsg = new Label("markersLoadedMsg", msg);
                newloadedMsg.setOutputMarkupId(true);
                markersLoadedMsg.replaceWith(newloadedMsg);
                markersLoadedMsg = newloadedMsg;
                target.addComponent(newloadedMsg);
            }
        };
        add(viewer);
    }

    public static List<Marker> getEasyMarkers(SearchModel model) {
        List<Marker> loc = new ArrayList<Marker>();

        final List<?> hits = model.getObject().getResult().getHits();
        for (final Object hit : hits) {
            @SuppressWarnings("unchecked")
            final DatasetSB datasetSB = ((SimpleSearchHit<DatasetSB>) hit).getData();
            LOGGER.debug("sid: {} coverage: {}", datasetSB.getStoreId(), datasetSB.getDcCoverage());
            // Note that it would be faster if the extracted locations where indexed at dataset ingestion,
            // so it could be retrieved from the datasetSB without string parsing and calculations.
            LonLat lonlat = LocationExtractor.getLocation(datasetSB.getDcCoverage());
            if (lonlat != null) {
                if (loc.size() < MAX_NUM_MARKERS) {
                    loc.add(new Marker(lonlat, getMarkerInfo(datasetSB)));
                } else {
                    break;
                }
            }
        }

        return loc;
    }

    private static String getMarkerInfo(DatasetSB datasetSB) {
        String title = datasetSB.getDcTitleSortable();
        if (title == null)
            title = ""; // avoid NPE, but leave link text empty

        // escape the string for html (but keep the whitespaces for correct wrapping)
        title = Strings.escapeMarkup(title, false, true).toString();

        // construct url (link) to dataset using the StoreID
        String hrefStr = getBaseUrl() + "ui/datasets/id/" + datasetSB.getStoreId();

        return "<a href='" + hrefStr + "'>" + title + "</a>";
    }

    private static String baseUrl = null;

    private static String getBaseUrl() {
        // assume baseUrl does not change runtime, then we only have to determine it once
        if (baseUrl == null) {
            String absPath = RequestUtils.toAbsolutePath(RequestCycle.get().getRequest().getRelativePathPrefixToContextRoot());
            if (!absPath.endsWith("/"))
                absPath = absPath + "/";
            // up to and including the first slash after the double slashes
            int slashslash = absPath.indexOf("//") + 2;
            int end = absPath.indexOf('/', slashslash) + 1;
            baseUrl = absPath.substring(0, end);
        }

        return baseUrl;
    }

    @Override
    public SearchResult<?> search(SimpleSearchRequest request) {
        return null;
    }

}
