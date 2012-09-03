package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.template.emd.atomic.*;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;

public abstract class SkeletonPanel extends AbstractEasyPanel
{

    private static final long serialVersionUID = -8285574501580736346L;

    private String            labelResourceKey;
    private String            shortHelpResourceKey;
    private String            helpItem;
    private boolean           required;

    private boolean           initiated;

    private boolean 		  inEditMode;
    
    public SkeletonPanel(final String id)
    {
        super(id);
    }

    public SkeletonPanel(final String id, final IModel<EasyMetadata> model)
    {
        super(id, model);
    }

    /**
     * Set the fields of this SkeletonPanel in accordance with the given definition.
     *
     * @param definition
     *        definition to set
     */
    public void setDefinition(final StandardPanelDefinition definition)
    {
        this.labelResourceKey = definition.getLabelResourceKey();
        this.shortHelpResourceKey = definition.getShortHelpResourceKey();
        this.helpItem = definition.getHelpItem();
        this.required = definition.isRequired();
    }

    /**
     * Provide the helpItem for this RepeaterPanel.
     *
     * @param helpItem
     */
    public void setHelpItem(final String helptem)
    {
        this.helpItem = helptem;
    }

    /**
     * Are the components on this RepeaterPanel required; the effect is only visual.
     *
     * @return <code>true</code> if required, <code>false</code> otherwise
     */
    public boolean isRequired()
    {
        return required;
    }

    /**
     * Set whether the components on this RepeaterPanel are required; the effect is only visual.
     *
     * @param required
     *        <code>true</code> if required, <code>false</code> otherwise
     */
    public void setRequired(final boolean required)
    {
        this.required = required;
    }

    public boolean isInEditMode() {
		return inEditMode;
	}

	public void setInEditMode(boolean editMode) {
		this.inEditMode = editMode;
	}

	public boolean takesErrorMessages()
    {
        return isInEditMode();
    }


    /**
     * Is this SkeletonPanel initiated (rendered) yet?
     *
     * @return <code>true</code> if initiated, <code>false</code> otherwise
     */
    protected boolean isInitiated()
    {
        return initiated;
    }

    /**
     * Set the state of this SkeletonPanel to initiated, after rendering.
     */
    protected void setInitiated()
    {
        initiated = true;
    }

    @Override
    protected void onBeforeRender()
    {
        if (!isInitiated())
        {
            init();
            setInitiated();
        }
        super.onBeforeRender();
    }

    protected void init()
    {
        add(createFeedbackPanel());
        add(createSimpleLabel(labelResourceKey, helpItem));
        add(createShortHelp(shortHelpResourceKey));
    }

    private Label createShortHelp(String shortHelpResourceKey)
    {
        final Label label = new Label("shortHelp", new ResourceModel(shortHelpResourceKey, ""));
        label.setEscapeModelStrings(false);
        return label;
    }

    private SimpleLabelPanel createSimpleLabel(String labelResourceKey, String helpItem)
    {
        SimpleLabelPanel simpleLabelPanel = new SimpleLabelPanel("label", labelResourceKey, helpItem, isRequired());
        simpleLabelPanel.setPopUpButtonIsVisible(isInEditMode());
        return simpleLabelPanel;
    }

    private FeedbackPanel createFeedbackPanel()
    {
        return new FeedbackPanel("panelFeedback", new IFeedbackMessageFilter()
        {

            private static final long serialVersionUID = 6414413128618876823L;

            public boolean accept(FeedbackMessage message)
            {
            	if (message.getReporter() == null)
            		return false;
                if (SkeletonPanel.this.getPath().endsWith(message.getReporter().getPath()))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }

        });
    }
}
