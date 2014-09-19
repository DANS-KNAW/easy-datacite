package nl.knaw.dans.easy.web;

import nl.knaw.dans.easy.web.editabletexts.EasyEditablePanel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

public class EditableInfoPage extends AbstractEasyNavPage {

    public static final String WI_HEADING = "heading";

    private final String heading;

    private Class<? extends Page> callingClass;

    private EasyEditablePanel editablePanel;

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

    private boolean initiated;

    public EditableInfoPage(String heading, final String templatePath, final Object... placeholders) {
        this.heading = heading;
        editablePanel = new EasyEditablePanel("editablePanel", templatePath, placeholders);
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
        add(editablePanel);

        Link backToListLink = new Link("backToList") {
            private static final long serialVersionUID = 2282643032675018321L;

            @Override
            public void onClick() {
                Page page = EditableInfoPage.this.getEasySession().getRedirectPage(callingClass);
                if (page != null && page instanceof AbstractEasyPage) {
                    ((AbstractEasyPage) page).refresh();
                }
                if (page != null) {
                    setResponsePage(page);
                }
            }

            @Override
            public boolean isVisible() {
                return EditableInfoPage.this.getEasySession().hasRedirectPage(callingClass);
            }
        };
        add(backToListLink);

    }

}
