package nl.knaw.dans.common.wicket.components.search.results;

import java.util.List;

import nl.knaw.dans.common.lang.search.SortField;
import nl.knaw.dans.common.lang.search.SortType;
import nl.knaw.dans.common.lang.search.simple.SimpleSortField;
import nl.knaw.dans.common.wicket.components.search.BaseSearchPanel;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * This panel shows the sort options for the search result panel based on a list of sort link config objects. It updates the sort fields in the request builder
 * whenever a sort option has been selected. This panel does not update the SearchModel when it gets dirty, but does dirty the SearchModel.
 */
public class SearchSortPanel extends BaseSearchPanel {
    private static final long serialVersionUID = -6328084337782262182L;

    public SearchSortPanel(final String wicketId, final SearchModel model, final List<SortLinkConfig> sortLinkConfigs) {
        super(wicketId, model);

        SortOptionsDropDownChoice sortOptions = new SortOptionsDropDownChoice("sortOptions", createSortLinkConfigModel(), sortLinkConfigs);
        add(sortOptions).setOutputMarkupId(true);
    }

    @Override
    public boolean isVisible() {
        return getSearchResult().getTotalHits() > 1;
    }

    private IModel<SortLinkConfig> createSortLinkConfigModel() {
        return new IModel<SortLinkConfig>() {
            private static final long serialVersionUID = 1L;
            private SortLinkConfig s;

            @Override
            public void detach() {}

            @Override
            public SortLinkConfig getObject() {
                return s;
            }

            @Override
            public void setObject(SortLinkConfig object) {
                s = object;
            }
        };
    }

    public class SortOptionsDropDownChoice extends DropDownChoice<SortLinkConfig> {
        private static final long serialVersionUID = 1L;

        private class SortOptionsRenderer extends ChoiceRenderer<SortLinkConfig> {
            private static final long serialVersionUID = 1L;

            @Override
            public Object getDisplayValue(SortLinkConfig object) {
                Label sortTextLabel;
                if (object.getSortType().equals(SortType.BY_RELEVANCE_SCORE)) {
                    sortTextLabel = new Label("not_used", new ResourceModel(SEARCHSORTPANEL_RELEVANCE));
                } else {
                    sortTextLabel = new Label("not_used", new ResourceModel("fieldname." + object.getFieldName()));
                }

                return sortTextLabel.getDefaultModelObjectAsString();
            }
        }

        public SortOptionsDropDownChoice(String id, IModel<SortLinkConfig> m, List<SortLinkConfig> data) {
            super(id, m, data);
            setChoiceRenderer(new SortOptionsRenderer());
        }

        @Override
        protected boolean wantOnSelectionChangedNotifications() {
            return true;
        }

        protected void onSelectionChanged(final SortLinkConfig newSelection) {
            SimpleSortField newSortField = new SimpleSortField(newSelection.getFieldName(), newSelection.getInitialSortOrder(), newSelection.getSortType());
            setActiveSortField(newSortField);

            setResponsePage(getPage());
        }

        private SearchRequestBuilder getRequestBuilder() {
            return getSearchModel().getObject().getRequestBuilder();
        }

        private List<SortField> getSortFields() {
            return getRequestBuilder().getSortFields();
        }

        public SortLinkConfig getConfig() {
            return (SortLinkConfig) getDefaultModelObject();
        }

        public SortField getActiveSortField() {
            List<SortField> sortFields = getSortFields();
            SortField activeSortField = null;
            if (sortFields != null && sortFields.size() > 0)
                activeSortField = sortFields.get(0);
            return activeSortField;
        }

        public void setActiveSortField(SortField activeSortField) {
            getRequestBuilder().setFirstSortField(activeSortField);
        }
    }
}
