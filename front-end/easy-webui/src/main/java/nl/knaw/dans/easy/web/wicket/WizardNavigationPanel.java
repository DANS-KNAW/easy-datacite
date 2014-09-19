package nl.knaw.dans.easy.web.wicket;

import nl.knaw.dans.easy.domain.form.*;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class WizardNavigationPanel extends Panel {

    private static final long serialVersionUID = 8137149138691417525L;

    private final FormDefinition formDefinition;
    private final WizardNavigationListener listener;
    private boolean initiated;
    private String labelResourceKey;
    private FormPage currentPage;

    @SpringBean(name = "staticContentBaseUrl")
    private String staticContentBaseUrl;

    public WizardNavigationPanel(String id, FormDefinition formDefinition, WizardNavigationListener listener) {
        super(id);
        this.listener = listener;
        this.formDefinition = formDefinition;
    }

    public String getLabelResourceKey() {
        return labelResourceKey;
    }

    public void setLabelResourceKey(String labelResourceKey) {
        this.labelResourceKey = labelResourceKey;
    }

    public FormPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(FormPage currentPage) {
        this.currentPage = currentPage;
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
        final ListView pageLinks = new ListView("listView", formDefinition.getFormPages()) {

            private static final long serialVersionUID = 6265957616943932059L;

            @Override
            protected void populateItem(ListItem item) {
                final FormPage formPage = (FormPage) item.getDefaultModelObject();
                SubmitLink submitLink = new SubmitLink("pageLink") {
                    private static final long serialVersionUID = -6058708083767256426L;

                    @Override
                    public void onSubmit() {
                        listener.onPageClick(formPage);
                    }

                };
                submitLink.add(new Label("pageLinkLabel", new ResourceModel(formPage.getLabelResourceKey())));
                submitLink.setEnabled(!formPage.equals(currentPage));
                item.add(submitLink);
            }

        };
        final WebMarkupContainer pageLinkContainer = new WebMarkupContainer("pageLinkContainer");
        pageLinkContainer.add(pageLinks);
        add(new Label("label", new ResourceModel(labelResourceKey, "")));
        add(pageLinkContainer);
        String instructionFileNameBase = getInstructionFileNameBase();
        add(new ExternalLink("instructionLink_EN", staticContentBaseUrl + "/" + instructionFileNameBase + "UK.pdf" + "", "English"));
        add(new ExternalLink("instructionLink_NL", staticContentBaseUrl + "/" + instructionFileNameBase + "NL.pdf" + "", "Nederlands"));
    }

    private String getInstructionFileNameBase() {
        return currentPage.getInstructionFile() == null ? currentPage.getParent().getInstructionFile() : currentPage.getInstructionFile();
    }
}
