package nl.knaw.dans.common.wicket.components;

import nl.knaw.dans.common.wicket.WicketUtil;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

/**
 * Do not use this class directly. Use CommonPanel or CommonGPanel instead.
 * 
 * @param <T>
 *        the internal model data object
 */
class CommonBasePanel<T> extends GPanel<T>
{
    private static final long serialVersionUID = 3906988013645820611L;

    public CommonBasePanel(String id)
    {
        super(id);
    }

    public CommonBasePanel(String id, IModel<T> model)
    {
        super(id, model);
    }

    protected FeedbackPanel addCommonFeedbackPanel()
    {
        return WicketUtil.addCommonFeedbackPanel(this);
    }

    protected FeedbackPanel addCommonFeedbackPanel(IFeedbackMessageFilter filter)
    {
        return WicketUtil.addCommonFeedbackPanel(this, filter);
    }

    public String infoMessage(final String messageKey)
    {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.INFO);
    }

    public String infoMessage(final String messageKey, final String... param)
    {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.INFO, param);
    }

    public String warningMessage(final String messageKey)
    {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.WARNING);
    }

    public String warningMessage(final String messageKey, final String param)
    {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.WARNING, param);
    }

    public String errorMessage(final String messageKey)
    {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.ERROR);
    }

    public String errorMessage(final String messageKey, final String... param)
    {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.ERROR, param);
    }

    public String fatalMessage(final String messageKey)
    {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.FATAL);
    }

    public String fatalMessage(final String messageKey, final String... param)
    {
        return WicketUtil.commonMessage(this, messageKey, FeedbackMessage.FATAL, param);
    }

    public void hide(String wicketId)
    {
        WicketUtil.hide(this, wicketId);
    }
}
