package nl.knaw.dans.common.wicket.components.search.results;

import java.util.List;

import nl.knaw.dans.common.lang.search.SortField;
import nl.knaw.dans.common.lang.search.SortOrder;
import nl.knaw.dans.common.lang.search.SortType;
import nl.knaw.dans.common.lang.search.simple.SimpleSortField;
import nl.knaw.dans.common.wicket.components.search.BaseSearchPanel;
import nl.knaw.dans.common.wicket.components.search.SearchResources;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.ResourceModel;

/**
 * This panel shows the sort options for the search result panel based
 * on a list of sort link config objects. It updates the sort fields in
 * the request builder whenever a sort option has been selected.
 * 
 * This panel does not update the SearchModel when it gets dirty, but does dirty the
 * SearchModel.
 *
 * @author lobo
 */
public class SearchSortPanel extends BaseSearchPanel
{
    private static final long serialVersionUID = -6328084337782262182L;

    public SearchSortPanel(final String wicketId, final SearchModel model, final List<SortLinkConfig> sortLinkConfigs)
    {
        super(wicketId, model);

        // add the sort links
        add(new ListView<SortLinkConfig>("sortLinks", sortLinkConfigs)
        {
            private static final long serialVersionUID = 2247965825915057160L;

            private int firstIndexVisible = -1;

            @Override
            protected void populateItem(final ListItem<SortLinkConfig> item)
            {
                // sort link
                SortLinkConfig config = item.getModelObject();
                final SortFieldLink sortFieldLink = new SortFieldLink("sortLink", model, config);
                item.add(sortFieldLink);

                // separator
                item.add(new Label("separator", new ResourceModel(SEARCHSORTPANEL_DEFAULT_SEPARATOR))
                {
                    private static final long serialVersionUID = 5014832630696320708L;

                    public boolean isVisible()
                    {
                        return !isFirstVisible() && sortFieldLink.isVisible();
                    }

                    private boolean isFirstVisible()
                    {
                        if (sortFieldLink.isVisible() && (firstIndexVisible == item.getIndex() || firstIndexVisible < 0))
                        {
                            firstIndexVisible = item.getIndex();
                            return true;
                        }
                        else
                            return false;
                    };
                });
            }
        });
    }

    @Override
    public boolean isVisible()
    {
        return getSearchResult().getTotalHits() > 1;
    }

    public class SortFieldLink extends Link implements SearchResources
    {

        private static final long serialVersionUID = 7527394391889821849L;

        private static final String ARROW_DOWN = "arrowDown";

        private static final String ARROW_UP = "arrowUp";

        private static final String SORT_LINK_TEXT = "sortLinkText";

        private SortOrder sortOrder;

        private SortLinkConfig config;

        public SortFieldLink(String id, SearchModel model, SortLinkConfig config)
        {
            super(id, model);
            this.config = config;

            SortField sister = getRequestBuilder().getSortField(config.getFieldName());
            if (sister != null)
            {
                this.sortOrder = sister.getValue();
            }
            else
            {
                this.sortOrder = config.getInitialSortOrder();
            }
            //			if (isActiveSortField())
            //			{
            //			    this.sortOrder = getActiveSortField().getValue();
            //			}	
            //			else
            //			{
            //				this.sortOrder = SortOrder.DESC;	
            //			}

            Label sortTextLabel;
            if (config.getSortType().equals(SortType.BY_RELEVANCE_SCORE))
            {
                sortTextLabel = new Label(SORT_LINK_TEXT, new ResourceModel(SEARCHSORTPANEL_RELEVANCE));
            }
            else
            {
                sortTextLabel = new Label(SORT_LINK_TEXT, new ResourceModel("fieldname." + config.getFieldName()));
            }
            add(sortTextLabel);

            add(new Image(ARROW_UP, new ResourceReference(SearchSortPanel.class, "arrow_up.gif"))
            {
                private static final long serialVersionUID = 755198480165223076L;

                public boolean isVisible()
                {
                    return !sortOrder.equals(SortOrder.DESC) && isSelected();
                }
            });
            add(new Image(ARROW_DOWN, new ResourceReference(SearchSortPanel.class, "arrow_down.gif"))
            {
                private static final long serialVersionUID = -4206389009490588450L;

                public boolean isVisible()
                {
                    return sortOrder.equals(SortOrder.DESC) && isSelected();
                }
            });
        }

        @Override
        public void onClick()
        {
            if (isSelected())
                sortOrder = sortOrder.getReverse();

            SimpleSortField newSortField = new SimpleSortField(config.getFieldName(), sortOrder, config.getSortType());
            setActiveSortField(newSortField);

            setResponsePage(getPage());
        }

        @Override
        public boolean isVisible()
        {
            if (getConfig().getSortType().equals(SortType.BY_RELEVANCE_SCORE))
                // remove relevance score sort link if the search engine says it has no use
                return getSearchResult().useRelevanceScore();
            else
                // remove sort link are set to invisible
                return getConfig().isVisible(getSearchData());
        }

        public SortLinkConfig getConfig()
        {
            return config;
        }

        private SearchRequestBuilder getRequestBuilder()
        {
            return getSearchModel().getObject().getRequestBuilder();
        }

        private List<SortField> getSortFields()
        {
            return getRequestBuilder().getSortFields();
        }

        public SortField getActiveSortField()
        {
            List<SortField> sortFields = getSortFields();
            SortField activeSortField = null;
            if (sortFields != null && sortFields.size() > 0)
                activeSortField = sortFields.get(0);
            return activeSortField;
        }

        public void setActiveSortField(SortField activeSortField)
        {
            getRequestBuilder().setFirstSortField(activeSortField);
        }

        public boolean isActiveSortField()
        {
            SortField activeSortField = getActiveSortField();
            if (activeSortField == null)
                return false;
            return activeSortField.getName().equals(config.getFieldName());
        }

        public boolean isSelected()
        {
            SortField activeSortField = getActiveSortField();
            if (activeSortField == null)
            {
                // if no active sorting is applied the default sort field is the relevance
                return (config.getSortType().equals(SortType.BY_RELEVANCE_SCORE));
            }

            boolean isSelected;
            if (config.getSortType().equals(SortType.BY_RELEVANCE_SCORE))
                isSelected = activeSortField.getSortType().equals(SortType.BY_RELEVANCE_SCORE);
            else
                isSelected = isActiveSortField();
            return isSelected;
        }

    }

}
