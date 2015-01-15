package nl.knaw.dans.easy.web.search;

import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.results.SearchHitPanelFactory;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class HomeSearchHitPanelFactory implements SearchHitPanelFactory {
    private static final long serialVersionUID = 684074651842653845L;

    private static HomeSearchHitPanelFactory INSTANCE = new HomeSearchHitPanelFactory();

    public static HomeSearchHitPanelFactory getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public Panel createHitPanel(String id, SearchHit<?> hit, SearchModel model) {
        assert hit.getData() instanceof EasyDatasetSB : "hit.getData() must be instance of EasyDatasetSB";
        return new HomeDatasetHitPanel(id, new Model<SearchHit<EasyDatasetSB>>((SearchHit<EasyDatasetSB>) hit), model);
    }
}
