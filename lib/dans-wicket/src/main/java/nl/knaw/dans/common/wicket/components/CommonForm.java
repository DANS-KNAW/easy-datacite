package nl.knaw.dans.common.wicket.components;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.wicket.WicketUtil;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackIndicator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class CommonForm<T> extends Form<T> {
    private static final long serialVersionUID = 3289096671803030890L;

    public final List<String> myComponentsWithFeedBack = new ArrayList<String>();

    /**
     * Partial wicket id for FormComponentFeedbackIndicator.
     * 
     * @see #add(FormComponent, IModel)
     */
    public static final String FEEDBACK = "feedback";

    /**
     * Separator for feedback components.
     */
    public static final String SEPARATOR = "-";

    /**
     * Partial wicket id for FeedbackPanel with ComponentFeedbackMessageFilter.
     * 
     * @see #addWithComponentFeedback(FormComponent, IModel)
     */
    public static final String COMPONENT_FEEDBACK = "componentFeedback";

    public CommonForm(String id) {
        super(id);
    }

    public CommonForm(String id, IModel<T> model) {
        super(id, model);
    }

    /**
     * Add a common feedbackPanel. Feedback messages will show at
     * 
     * <pre>
     *    &lt;span wicket:id=&quot;commonFeedbackPanel&quot;&gt;feedback&lt;/span&gt;
     * </pre>
     * 
     * This feedback panel has a filter to exclude messages already shown in component feedback panels.
     * 
     * @return the common feedback panel
     * @see #addWithComponentFeedback(FormComponent, IModel)
     */
    protected FeedbackPanel addCommonFeedbackPanel() {
        return WicketUtil.addCommonFeedbackPanel(this, new CommonLevelFeedbackFilter());
    }

    protected FeedbackPanel addCommonFeedbackPanel(IFeedbackMessageFilter filter) {
        return WicketUtil.addCommonFeedbackPanel(this, filter);
    }

    public String infoMessage(final String messageKey) {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.INFO);
    }

    public String infoMessage(final String messageKey, final String... param) {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.INFO, param);
    }

    public String warningMessage(final String messageKey) {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.WARNING);
    }

    public String warningMessage(final String messageKey, final String param) {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.WARNING, param);
    }

    public String errorMessage(final String messageKey) {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.ERROR);
    }

    public String errorMessage(final String messageKey, final String... param) {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.ERROR, param);
    }

    public String fatalMessage(final String messageKey) {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.FATAL);
    }

    public String fatalMessage(final String messageKey, final String... param) {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.FATAL, param);
    }

    /**
     * Disable all components in the form, make any submit component invisible and make a link visible.
     * 
     * @param enableComponents
     *        list of all components
     */
    protected void disableForm(final String[] enableComponents) {
        // Disable all components.
        final AttributeModifier disabler = new AttributeModifier("disabled", true, new Model<String>("true"));
        this.visitChildren(FormComponent.class, new IVisitor<Component>() {
            public Object component(final Component component) {
                // Submitlinks and buttons are removed.
                if (SubmitLink.class.isAssignableFrom(component.getClass()) || Button.class.isAssignableFrom(component.getClass())) {
                    component.setVisible(false);
                } else {
                    // Others disabled.
                    component.add(disabler);
                }

                return IVisitor.CONTINUE_TRAVERSAL;
            }
        });
        // Disable the form.
        this.setEnabled(false);

        // Enable some specific components
        for (String componentName : enableComponents) {
            this.get(componentName).setVisible(true);
        }
    }

    /**
     * Add a FormComponent and FormComponentFeedbackIndicator. A red star will show at
     * 
     * <pre>
     *    &lt;span wicket:id=&quot;[formComponent.id]-feedback&quot;&gt;feedback&lt;/span&gt;
     * </pre>
     * 
     * where [formComponent.id] is the id of <code>formComponent</code>.
     * <p/>
     * The FormComponent itself is at
     * 
     * <pre>
     *    &lt;input id=&quot;tagId&quot; wicket:id=&quot;[formComponent.id]&quot; type=&quot;text&quot;/&gt;
     * </pre>
     * 
     * @param formComponent
     *        FormComponent to add
     * @param label
     *        label used in feedback messages
     */
    protected void add(final FormComponent<String> formComponent, final IModel<String> label) {
        // Add the component to the form
        super.add(formComponent);

        // Set its label
        formComponent.setLabel(label);

        // Add feedback panel to display
        FormComponentFeedbackIndicator feedbackIndicator = new FormComponentFeedbackIndicator(formComponent.getId() + SEPARATOR + FEEDBACK);
        feedbackIndicator.setIndicatorFor(formComponent);
        // LOGGER.debug("FeedbackIndicator " + feedbackIndicator.getId() + " added to the form " +
        // this.getId());
        feedbackIndicator.setOutputMarkupId(true);

        super.add(feedbackIndicator);
    }

    /**
     * Add a FormComponent and FormComponent feedback panel. Feedback messages will show at
     * 
     * <pre>
     *    &lt;span wicket:id=&quot;[formComponent.id]-componentFeedback&quot;&gt;feedback&lt;/span&gt;
     * </pre>
     * 
     * where [formComponent.id] is the id of <code>formComponent</code>.
     * <p/>
     * The FormComponent itself is at
     * 
     * <pre>
     *    &lt;input id=&quot;tagId&quot; wicket:id=&quot;[formComponent.id]&quot; type=&quot;text&quot;/&gt;
     * </pre>
     * 
     * @param formComponent
     *        FormComponent to add
     * @param labelModel
     *        label used in feedback messages
     * @return the feedbackPanel set on the given formComponent
     */
    protected FeedbackPanel addWithComponentFeedback(final FormComponent<?> formComponent, final IModel<String> labelModel) {
        ComponentFeedbackMessageFilter filter = new ComponentFeedbackMessageFilter(formComponent);

        FeedbackPanel feedBackPanel = new FeedbackPanel(formComponent.getId() + SEPARATOR + COMPONENT_FEEDBACK, filter) {
            private static final long serialVersionUID = -521216440119152641L;

            @Override
            public boolean isVisible() {
                return this.anyMessage();
            }

        };
        feedBackPanel.setOutputMarkupId(true);
        formComponent.setLabel(labelModel);
        super.add(formComponent);
        super.add(feedBackPanel);
        myComponentsWithFeedBack.add(formComponent.getId());

        return feedBackPanel;
    }

    /**
     * IFeedbackMessageFilter that filters out messages already shown in component feedback panels.
     * 
     * @author ecco Feb 25, 2009
     */
    public class CommonLevelFeedbackFilter implements IFeedbackMessageFilter {

        private static final long serialVersionUID = -4625910785421379795L;

        public boolean accept(FeedbackMessage message) {
            return !myComponentsWithFeedBack.contains(message.getReporter().getId());
        }
    }

    public void hide(String wicketId) {
        WicketUtil.hide(this, wicketId);
    }
}
