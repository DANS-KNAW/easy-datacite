package nl.knaw.dans.common.wicket.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.wicket.behavior.FocusOnLoadBehavior;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

public abstract class RESTcascadePage extends RESTpage {

    public static final String ID_NAVIGATION_PANEL = "navigationPanel";

    private String idValue;
    private String nextStep;

    public RESTcascadePage() {
        super();
    }

    public RESTcascadePage(PageParameters parameters) {
        super(parameters);
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    /**
     * Return a map of children of this page, where key is the name of the page.
     * 
     * @return a map of children of this page
     */
    public abstract Map<String, PageDescription> getChildren();

    /**
     * Does nothing. Override this method if you want to contribute parameters for the children of this page.
     * 
     * @param parameters
     *        the page parameters
     */
    protected void contributeParameters(PageParameters parameters) {
        // do nothing
    }

    /**
     * Redirect to the first child of this page. Pages that want to react different should override.
     */
    @Override
    protected void doDefaultDissemination() {
        if (getChildren().isEmpty()) {
            super.doDefaultDissemination();
        } else {
            PageDescription description = getChildren().entrySet().iterator().next().getValue();
            String targetUrl = composeUrl(getLevel() + 1, description.getName());
            throw new RestartResponseException(new RedirectPage(targetUrl));
        }
    }

    @Override
    protected void cascadeToChild() {
        int distance = getLevel() + (isStartPage() ? 0 : 1);
        if (distance < getUrlFragments().length) {
            PageParameters parameters = getPageParameters();
            if (parameters == null) {
                parameters = new PageParameters();
            }
            contributeParameters(parameters);
            String childName = getUrlFragment(distance);
            PageDescription description = getChildren().get(childName);
            if (description != null) {
                throw new RestartResponseException(description.getPageClass(), parameters);
            }
        } else {
            initPage();
        }
    }

    @Override
    protected void initPage() {
        super.initPage();
        if (isStartPage()) {
            add(getNavigationPanel());
        } else {
            add(getIdNavigationPanel());
        }
    }

    protected Panel getIdNavigationPanel() {
        return new IdNavigationPanel();
    }

    protected Panel getNavigationPanel() {
        return new NavigationPanel();
    }

    protected String getMissingResourceMessage(String key) {
        return "Missing resource for '" + key + "' in " + this.getClass().getSimpleName();
    }

    class IdNavigationPanel extends Panel {

        private static final long serialVersionUID = 7613175508890943159L;

        protected IdNavigationPanel() {
            super(RESTcascadePage.ID_NAVIGATION_PANEL);
            final SubmitLink submitLink = new SubmitLink("submit") {

                private static final long serialVersionUID = 4913648667116290496L;

                @Override
                public boolean isEnabled() {
                    return (StringUtils.isNotBlank(getIdValue()));
                }
            };
            submitLink.setOutputMarkupId(true);

            Form<String> navigationForm = new Form<String>("navigationForm") {
                private static final long serialVersionUID = 4524604506262081812L;

                @Override
                protected void onSubmit() {
                    if (StringUtils.isNotBlank(getId())) {
                        final String targetUrl = composeUrl(getLevel(), getIdValue(), getNextStep());
                        setResponsePage(new RedirectPage(targetUrl));
                    }
                }

            };
            navigationForm.setOutputMarkupId(true);

            final TextField<String> idField = new TextField<String>("idField", new PropertyModel<String>(RESTcascadePage.this, "idValue"));
            idField.add(new OnChangeAjaxBehavior() {

                private static final long serialVersionUID = 6255926118715512652L;

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    target.addComponent(submitLink);
                }
            });
            idField.add(new FocusOnLoadBehavior());
            navigationForm.add(idField);

            final Map<String, PageDescription> children = getChildren();
            List<String> names = new ArrayList<String>(children.keySet());
            if (names.size() > 0)
                setNextStep(names.get(0));
            RadioGroup<String> group = new RadioGroup<String>("group", new PropertyModel<String>(RESTcascadePage.this, "nextStep"));
            navigationForm.add(group);
            ListView<String> nextSteps = new ListView<String>("nextSteps", names) {

                private static final long serialVersionUID = -3965717042556385072L;

                @Override
                protected void populateItem(ListItem<String> item) {
                    PageDescription description = children.get(item.getModelObject());
                    item.add(new Radio<String>("radio", new Model<String>(description.getName())));
                    item.add(new Label("name", description.getName()));
                    String key = description.getResourceKey() + ".description";
                    item.add(new Label("description", new ResourceModel(key, getMissingResourceMessage(key))));
                }
            };
            group.add(nextSteps);

            navigationForm.add(submitLink);

            add(navigationForm);
        }
    }

    class NavigationPanel extends Panel {

        private static final long serialVersionUID = -9193954715825852225L;

        public NavigationPanel() {
            super(RESTcascadePage.ID_NAVIGATION_PANEL);
            final Map<String, PageDescription> children = getChildren();
            List<String> names = new ArrayList<String>(children.keySet());
            ListView<String> listView = new ListView<String>("collections", names) {

                private static final long serialVersionUID = -2450869630441848088L;

                @Override
                protected void populateItem(ListItem<String> item) {
                    PageDescription description = children.get(item.getModelObject());
                    final String targetUrl = composeUrl(getLevel(), description.getName());
                    ExternalLink eLink = new ExternalLink("collection", targetUrl, description.getName());
                    item.add(eLink);
                    String key = description.getResourceKey() + ".description";
                    item.add(new Label("description", new ResourceModel(key, getMissingResourceMessage(key))));
                }

            };
            add(listView);
        }
    }
}
