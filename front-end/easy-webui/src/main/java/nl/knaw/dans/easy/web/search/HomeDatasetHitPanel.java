package nl.knaw.dans.easy.web.search;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.wicket.components.UnescapedLabel;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.template.dates.EasyDateLabel;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.commons.lang.StringUtils;

public class HomeDatasetHitPanel extends AbstractEasyPanel<SearchHit<EasyDatasetSB>> {
    private static final long serialVersionUID = 1L;

    public HomeDatasetHitPanel(String wicketId, IModel<SearchHit<EasyDatasetSB>> model, SearchModel svModel) {
        super(wicketId, model);

        add(new DatasetLink("titleLink", model, svModel));

        SearchHit<EasyDatasetSB> hit = model.getObject();
        EasyDatasetSB datasetHit = hit.getData();
        add(new Label("creator", new Model<String>(StringUtils.join(datasetHit.getDcCreator(), "; "))));
        add(new EasyDateLabel("datePublished", datasetHit.getDatePublished()));
    }

    public class DatasetLink extends AbstractDatasetLink<SearchHit<EasyDatasetSB>> {
        private static final long serialVersionUID = -2898309546692290393L;

        DatasetLink(String wicketId, IModel<SearchHit<EasyDatasetSB>> model, SearchModel svModel) {
            super(wicketId, model);
            Label title = new Label("title", new Model<String>(getSnippetOrValue("dcTitle")));
            title.setRenderBodyOnly(true);
            addLabel(title);
        }

        @Override
        public void onClick() {
            SearchHit<? extends DatasetSB> hit = (SearchHit<? extends DatasetSB>) getModelObject();
            DatasetSB datasetHit = hit.getData();

            // instructions how to get back to this searchView
            ((EasySession) getSession()).setRedirectPage(DatasetViewPage.class, getPage());

            // view the dataset on dataset view page.
            PageParameters params = new PageParameters();
            params.put(DatasetViewPage.PM_DATASET_ID, datasetHit.getStoreId());
            setResponsePage(DatasetViewPage.class, params);
        }
    }
}
