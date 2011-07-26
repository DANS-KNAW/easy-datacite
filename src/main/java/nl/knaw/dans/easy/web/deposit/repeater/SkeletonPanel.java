package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;
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
    private String            helpFile;
    private boolean           required;

    private boolean           initiated;

    private boolean 		  inEditMode;
    
    public SkeletonPanel(final String id)
    {
        super(id);
    }

    public SkeletonPanel(final String id, final IModel model)
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
        this.helpFile = definition.getHelpFile();
        this.required = definition.isRequired();
    }

    /**
     * Get the labelResourceKey.
     *
     * @return labelResourceKey
     */
    public String getLabelResourceKey()
    {
        return labelResourceKey;
    }

    /**
     * Provide the labelResourceKey for this RepeaterPanel.
     *
     * @param labelResourceKey
     *        resource key
     */
    public void setLabelResourceKey(final String labelResourceKey)
    {
        this.labelResourceKey = labelResourceKey;
    }

    /**
     * @return the shortHelpResourceKey
     */
    public String getShortHelpResourceKey()
    {
        return shortHelpResourceKey;
    }

    /**
     * @param shortHelpResourceKey
     *        the shortHelpResourceKey to set
     */
    public void setShortHelpResourceKey(final String shortHelpResourceKey)
    {
        this.shortHelpResourceKey = shortHelpResourceKey;
    }

    /**
     * @return the helpFile
     */
    public String getHelpFile()
    {
        return helpFile;
    }

    /**
     * @param helpFile
     *        the helpFile to set
     */
    public void setHelpFile(final String helpFile)
    {
        this.helpFile = helpFile;
    }

    /**
     * Get the helpItem.
     *
     * @return helpItem
     */
    public String getHelpItem()
    {
        return helpItem;
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
        add(new FeedbackPanel("panelFeedback", new IFeedbackMessageFilter()
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

        }));
        
        SimpleLabelPanel simpleLabelPanel = new SimpleLabelPanel("label", getLabelResourceKey(), getHelpItem(), isRequired());
        simpleLabelPanel.setPopUpButtonIsVisible(isInEditMode());
        add(simpleLabelPanel);
         
        final Label label = new Label("shortHelp", new ResourceModel(getShortHelpResourceKey(), ""));
        label.setEscapeModelStrings(false);
        add(label);
    }
}
