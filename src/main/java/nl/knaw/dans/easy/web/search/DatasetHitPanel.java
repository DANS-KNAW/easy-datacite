package nl.knaw.dans.easy.web.search;

import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.SnippetField;
import nl.knaw.dans.common.wicket.components.UnescapedLabel;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.view.commondataset.ViewCommonDatasetPage;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;
import nl.knaw.dans.easy.web.wicket.HighlightedCharSequence;
import nl.knaw.dans.easy.web.wicket.ShortenedCharSequenceModel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class DatasetHitPanel extends AbstractEasyPanel<SearchHit<DatasetSB>>
{
    private static final long serialVersionUID = 1765294309790135569L;

    public DatasetHitPanel(String wicketId, IModel<SearchHit<DatasetSB>> model, SearchModel svModel)
    {
        super(wicketId, model);
        add(new DatasetLink("showDataset", model, svModel));
    }

    public class DatasetLink extends AbstractDatasetLink<SearchHit<DatasetSB>>
    {
        private static final long serialVersionUID = -2898309546692290393L;

        @SuppressWarnings("unchecked")
        DatasetLink(String wicketId, IModel<SearchHit<DatasetSB>> model, SearchModel svModel)
        {
            super(wicketId, model);

            SearchHit<DatasetSB> hit = model.getObject();
            DatasetSB datasetHit = hit.getData();

            addLabel(new UnescapedLabel("title", new Model<String>(getSnippetOrValue("dcTitle"))));
            addLabel(new UnescapedLabel("creator", new ShortenedCharSequenceModel(getSnippetOrValue("dcCreator"))));
            addLabel(new UnescapedLabel("description", new ShortenedCharSequenceModel(getSnippetOrValue("dcDescription"))));

            // -------- column 3
            addLabel(new Label("accessrights", new ResourceModel("fieldvalue." + datasetHit.getAccessCategory())), datasetHit.getAccessCategory() != null);

            // -------- footer
            addLabel(new Label("relevance", String.format("%.0f", hit.getRelevanceScore() * 100)),
                    !StringUtils.isBlank(svModel.getObject().getRequestBuilder().getRequest().getQuery().getQueryString()));
            List<SnippetField> remainingSnippets = getRemainingSnippets();
            add(new ListView("snippets", remainingSnippets)
            {
                private static final long serialVersionUID = 6092057488401837474L;

                @Override
                protected void populateItem(ListItem item)
                {
                    final SnippetField snippetField = (SnippetField) item.getDefaultModelObject();
                    String snippet = "";
                    for (String snip : snippetField.getValue())
                        snippet += snip;
                    item.add(new Label("snippetField", new ResourceModel("fieldname." + snippetField.getName())));
                    item.add(new UnescapedLabel("snippet", new ShortenedCharSequenceModel(new HighlightedCharSequence(snippet), 100)));
                }
            }.setVisible(remainingSnippets.size() > 0));
        }

        @Override
        public void onClick()
        {
            SearchHit<? extends DatasetSB> hit = (SearchHit<? extends DatasetSB>) getModelObject();
            DatasetSB datasetHit = hit.getData();

            // instructions how to get back to this searchView
            ((EasySession) getSession()).setRedirectPage(ViewCommonDatasetPage.class, getPage());

            // view the dataset on dataset view page.
            PageParameters params = new PageParameters();
            params.put(DatasetViewPage.PM_DATASET_ID, datasetHit.getStoreId());
            setResponsePage(ViewCommonDatasetPage.class, params);
        }
    }

}
