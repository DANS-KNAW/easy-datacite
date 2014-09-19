package nl.knaw.dans.common.wicket.components.search.criteria;

import java.util.List;

import nl.knaw.dans.common.wicket.components.search.BaseSearchPanel;
import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shows the list of selected criteria and makes it possible for a user to remove one or more of these criteria. This panel does not update the SearchModel when
 * it gets dirty, but does dirty the SearchModel.
 * 
 * @author lobo
 */
public class SearchCriteriaPanel extends BaseSearchPanel {
    private static final long serialVersionUID = 713695445090703764L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchCriteriaPanel.class);

    private static final String DEFAULT_CRITERIUM_SEPARATOR = " | ";

    private boolean firstCriteriumDisabled;

    public SearchCriteriaPanel(String wicketId, SearchModel model) {
        super(wicketId, model);

        firstCriteriumDisabled = getCriteria().size() > 0;

        add(new ListView<SearchCriterium>("criteriumLinks", new AbstractReadOnlyModel<List<SearchCriterium>>() {
            private static final long serialVersionUID = 1L;

            public List<SearchCriterium> getObject() {
                return getCriteria();
            }
        })
        {
            private static final long serialVersionUID = 4136872716659659458L;

            @Override
            protected void populateItem(final ListItem<SearchCriterium> item) {
                final SearchCriterium criterium = item.getModelObject();

                // @formatter:off
                /*
                item.add(new Link<Void>("criteriumLink")
                {
                	private static final long	serialVersionUID	= -2838312214613553559L;

                	{
                		add(new CriteriumLabel("criteriumText", criterium.getLabelModel()));
                	}
                	
                	@Override
                	public void onClick()
                	{
                		int idx = getCriteriaIdx(criterium);
                		idx++;
                		List<SearchCriterium> criteria = getCriteria();
                		while(idx < criteria.size())
                			getRequestBuilder().removeCriterium(idx);
                	}
                	
                	public boolean isEnabled() 
                	{
                		if (firstCriteriumDisabled)
                			return item.getIndex()+1 < getCriteria().size();
                		else
                			return true;
                	};
                }
                ); */
                // Note: clicking the link removed all other criteria, it was decided not to have it 
                // but I left it as a comment above!
                // @formatter:on
                item.add(new CriteriumLabel("criteriumText", criterium.getLabelModel()));

                item.add(new Link("removeLink") {
                    private static final long serialVersionUID = -1063707405830738778L;

                    {
                        add(new Image("closeImage", new ResourceReference(SearchCriteriaPanel.class, "close-icon.png")));
                    }

                    public void onClick() {
                        getRequestBuilder().removeCriterium(criterium);
                    };

                    public boolean isVisible() {
                        return !(criterium instanceof InitialSearchCriterium);
                    };
                });

                item.add(new Label("criteriumSeparator", DEFAULT_CRITERIUM_SEPARATOR).setVisible(item.getIndex() + 1 < getCriteria().size()));
            }
        });
    }

    private List<SearchCriterium> getCriteria() {
        return getRequestBuilder().getCriteria();
    }

    private int getCriteriaIdx(SearchCriterium criterium) {
        List<SearchCriterium> criteria = getCriteria();
        int idx = criteria.indexOf(criterium);
        if (idx >= 0) {
            return idx;
        } else {
            LOGGER.error("Could not find criterium " + getModelObject().toString());
            throw new InternalWebError();
        }
    }

    @Override
    public boolean isVisible() {
        int criteria = getCriteria().size();
        int hits = getSearchResult().getTotalHits();
        return !(criteria <= 1 && hits == 0);
    }
}
