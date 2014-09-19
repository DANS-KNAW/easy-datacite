package nl.knaw.dans.easy.web;

import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class InfoPage extends AbstractEasyNavPage {

    public static final String WI_HEADING = "heading";

    public static final String WI_MISSION_ACCOMPLISHED_PANEL = "missionAccomplishedFeedback";

    private final String heading;

    private Class<? extends Page> callingClass;

    private boolean allowHtml;

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

    private boolean initiated;

    public InfoPage() {
        this(null);
    }

    public InfoPage(String heading) {
        this.heading = heading;
    }

    /**
     * Allow HTML content to be set on the {@link InfoPage}. Use with caution! Use only if a hundred percent sure that the {@link InfoPage} will not echo back
     * user input, as that would introduce vulnerability to cross site scripting.
     */
    public void setAllowHtml(boolean allow) {
        this.allowHtml = allow;
    }

    @Override
    public String getPageTitlePostfix() {
        return heading;
    }

    public Class<? extends Page> getCallingClass() {
        return callingClass;
    }

    public void setCallingClass(Class<? extends Page> callingClass) {
        this.callingClass = callingClass;
    }

    @Override
    protected void onBeforeRender() {
        if (!initiated) {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init() {
        add(new Label(WI_HEADING, heading)).setVisible(StringUtils.isNotBlank(heading));
        FeedbackPanel fbp = new FeedbackPanel(WI_MISSION_ACCOMPLISHED_PANEL);
        fbp.setEscapeModelStrings(!allowHtml);
        add(fbp);

        Link backToListLink = new Link("backToList") {
            private static final long serialVersionUID = 2282643032675018321L;

            @Override
            public void onClick() {
                Page page = InfoPage.this.getEasySession().getRedirectPage(callingClass);
                if (page != null && page instanceof AbstractEasyPage) {
                    ((AbstractEasyPage) page).refresh();
                }
                if (page != null) {
                    setResponsePage(page);
                }
            }

            @Override
            public boolean isVisible() {
                return InfoPage.this.getEasySession().hasRedirectPage(callingClass);
            }
        };
        add(backToListLink);

    }

}
