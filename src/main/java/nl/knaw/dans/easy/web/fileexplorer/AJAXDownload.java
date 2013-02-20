package nl.knaw.dans.easy.web.fileexplorer;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;

public abstract class AJAXDownload extends AbstractAjaxBehavior
{
    private static final long serialVersionUID = 1L;

    /**
     * Call this method to initiate the download.
     */
    public void initiate(AjaxRequestTarget target)
    {
        CharSequence url = getCallbackUrl();

        target.appendJavascript("window.location.href='" + url + "'");
    }

    public void onRequest()
    {
        getComponent().getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(getResourceStream(), getFileName()));
    }

    /**
     * @see ResourceStreamRequestTarget#getFileName()
     */
    protected String getFileName()
    {
        return null;
    }

    /**
     * Hook method providing the actual resource stream.
     */
    protected abstract IResourceStream getResourceStream();
}
