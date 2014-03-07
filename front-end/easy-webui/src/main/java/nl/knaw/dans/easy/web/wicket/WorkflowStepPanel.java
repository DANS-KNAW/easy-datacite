package nl.knaw.dans.easy.web.wicket;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.joda.time.DateTime;

public class WorkflowStepPanel extends AbstractEasyPanel
{

    public static final String COMPLETED = "completed";

    public static final String AJAX_EVENT_WORKFOWSTEP_COMPLETED_CHANGED = WorkflowStepPanel.class.getName() + " workflowstep completed changed";

    public static final String POSTFIX_CSS = ".css";

    public static final String POSTFIX_CHECKBOX = ".check";

    public static final String POSTFIX_TITLE_KEY = ".wfsname";

    private static final long serialVersionUID = 7682413775887633507L;

    private final WorkflowStep workflowStep;
    private final Properties displayProps;
    private final String cssClass;
    private final String resourceKeyForTitle;
    private final boolean checkBoxVisisble;
    private final boolean leaf;

    private Set<Component> targetableComponents = new HashSet<Component>();
    private boolean initiated;

    public WorkflowStepPanel(String wicketId, WorkflowStep workflowStep, Properties displayProps)
    {
        super(wicketId);
        this.workflowStep = workflowStep;
        this.displayProps = displayProps;

        cssClass = displayProps.getProperty(workflowStep.getId() + POSTFIX_CSS);
        checkBoxVisisble = "true".equals(displayProps.getProperty(workflowStep.getId() + POSTFIX_CHECKBOX, "false"));
        leaf = workflowStep.isLeaf();
        resourceKeyForTitle = workflowStep.getId() + POSTFIX_TITLE_KEY;
    }

    public WorkflowStep getWorkflowStep()
    {
        return workflowStep;
    }

    public Properties getDisplayProps()
    {
        return displayProps;
    }

    public String getResourceKeyForTitle()
    {
        return resourceKeyForTitle;
    }

    public String getCssClass()
    {
        return cssClass;
    }

    public boolean isCheckBoxVisisble()
    {
        return checkBoxVisisble;
    }

    public boolean isLeaf()
    {
        return leaf;
    }

    public void addTargetableComponent(Component component)
    {
        targetableComponents.add(component);
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init()
    {
        // Title visible if there is no checkbox
        Label title = new Label("title", new ResourceModel(getResourceKeyForTitle(), "title of this step"));
        if (cssClass != null)
        {
            title.add(new SimpleAttributeModifier("class", cssClass));
        }
        title.setVisible(!isCheckBoxVisisble());
        add(title);

        // Time spent visible if this step timeSpentWritable returns true
        final TextField timeSpent = new TextField("timeSpent", new Model()
        {

            private static final long serialVersionUID = -8549305949084351344L;
            private Serializable object;

            @Override
            public Serializable getObject()
            {
                double ts = workflowStep.getTimeSpent();
                if (ts != 0.0D)
                    object = new Double(ts);
                return object;
            }

            @Override
            public void setObject(Serializable object)
            {
                this.object = object;
                if (object == null)
                {
                    workflowStep.setTimeSpent(0.0D);
                }
                else
                {
                    String s = (String) object;
                    try
                    {
                        s = s.replace(",", ".");
                        double ts = Double.valueOf(s);
                        workflowStep.setTimeSpent(ts);
                    }
                    catch (NumberFormatException e)
                    {
                        //
                    }
                }
            }

        });
        timeSpent.setOutputMarkupId(true);
        timeSpent.add(new OnChangeAjaxBehavior()
        {

            private static final long serialVersionUID = 8835491109129296731L;

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                // we do nothing here, the model is set for us and rendered on blur.
            }

        });

        timeSpent.add(new AjaxEventBehavior("onBlur")
        {

            private static final long serialVersionUID = 9180858550623731076L;

            @Override
            protected void onEvent(AjaxRequestTarget target)
            {
                target.addComponent(timeSpent.getForm());
            }

        });

        timeSpent.setVisible(workflowStep.isTimeSpentWritable());
        add(timeSpent);

        // Checkbox, label for checkbox, step required. Visible if checkBoxVisisble
        WebMarkupContainer checkBoxContainer = new WebMarkupContainer("checkBoxContainer");
        if (cssClass != null)
        {
            checkBoxContainer.add(new SimpleAttributeModifier("class", cssClass));
        }
        final CheckBox checkBox = new CheckBox(COMPLETED, new PropertyModel(workflowStep, "completed"));

        checkBox.add(new OnChangeAjaxBehavior()
        {

            private static final long serialVersionUID = 8980654540330989888L;

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                if (workflowStep.isCompleted())
                {
                    workflowStep.setWhoDidIt(getSessionUser());
                }
                else
                {
                    workflowStep.setWhoDidIt(null);
                }
                target.addComponent(checkBox.getForm());
                WorkflowStepPanel.this.handleAjaxEvent(AJAX_EVENT_WORKFOWSTEP_COMPLETED_CHANGED, target);
            }

        });
        checkBox.setOutputMarkupId(true);
        checkBoxContainer.add(checkBox);

        Label checkBoxLabel = new Label("completedLabel", new ResourceModel(getResourceKeyForTitle(), "checkBoxLabel"));
        checkBoxLabel.add(new SimpleAttributeModifier("for", checkBox.getMarkupId()));
        checkBoxContainer.add(checkBoxLabel);

        Label required = new Label("required", "*");
        required.setVisible(workflowStep.isRequired());
        checkBoxContainer.add(required);

        checkBoxContainer.setVisible(isCheckBoxVisisble());
        add(checkBoxContainer);

        Label doneBy = new Label("doneBy", new Model()
        {

            private static final long serialVersionUID = -6036707328494787135L;

            @Override
            public Serializable getObject()
            {
                EasyUser whoDidIt = workflowStep.getWhoDidIt();
                return whoDidIt == null ? null : whoDidIt.getDisplayName();
            }

        });
        doneBy.setVisible(isLeaf());
        add(doneBy);

        DateLabel doneOnDate = DateLabel.forDatePattern("doneOnDate", new Model()
        {

            private static final long serialVersionUID = 1139426060975374951L;

            @Override
            public Serializable getObject()
            {
                DateTime dateTime = workflowStep.getCompletionTimeAllSteps();
                return dateTime == null ? null : dateTime.toDate();
            }

        }, "yyyy-MM-dd");
        add(doneOnDate);

        // substeps
        add(new ListView("kidSteps", workflowStep.getSteps())
        {

            private static final long serialVersionUID = -3448706650046104801L;

            @Override
            protected void populateItem(ListItem item)
            {
                WorkflowStep kidStep = (WorkflowStep) item.getDefaultModelObject();
                item.add(new WorkflowStepPanel("kidStep", kidStep, displayProps));
            }

        });

    }

}
