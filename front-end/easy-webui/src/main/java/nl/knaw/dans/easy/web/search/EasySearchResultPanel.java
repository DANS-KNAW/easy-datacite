package nl.knaw.dans.easy.web.search;

import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.results.SearchResultConfig;
import nl.knaw.dans.common.wicket.components.search.results.SearchResultPanel;
import nl.knaw.dans.easy.web.common.HelpFileReader;
import nl.knaw.dans.easy.web.common.ModalHelpPanel;
import nl.knaw.dans.easy.web.common.StyledModalWindow;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;

public abstract class EasySearchResultPanel extends SearchResultPanel
{
    private static final long serialVersionUID = 8067439489635623029L;

    private boolean showTips;

    public EasySearchResultPanel(final String wicketId, boolean showTips, SearchResultConfig config)
    {
        super(wicketId, config);
        this.showTips = showTips;
        init();
    }

    public EasySearchResultPanel(final String wicketId, SearchModel searchModel, boolean showTips, SearchResultConfig config)
    {
        super(wicketId, searchModel, config);
        this.showTips = showTips;
        init();
    }

    private void init()
    {
        if (showTips)
        {
            WebMarkupContainer searchTips = new WebMarkupContainer("searchTips")
            {
                private static final long serialVersionUID = 1234523335L;

                @Override
                public boolean isVisible()
                {
                    return !StringUtils.isBlank(getRequestBuilder().getRequest().getQuery().getQueryString());
                }
            };

            // Search Help popup
            final ModalWindow popup = new StyledModalWindow("searchTipsHelpPopup", "Help");
            searchTips.add(popup);
            AjaxLink<Void> showHelpLink = new AjaxLink<Void>("popupLink")
            {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target)
                {
                    popup.setContent(new ModalHelpPanel(popup, "Search"));
                    popup.show(target);
                }
            };
            searchTips.add(showHelpLink);

            WebMarkupContainer noResultsTip = new WebMarkupContainer("noResultsTip")
            {
                private static final long serialVersionUID = 12345235L;

                public boolean isVisible()
                {
                    return getSearchResult().getTotalHits() == 0;
                };
            };
            searchTips.add(noResultsTip);

            add(searchTips);
        }
        else
        {
            hide("searchTips");
        }
    }

    @Override
    protected String getRefineHelpContent()
    {
        return new HelpFileReader("Refine").read();
    }
}
