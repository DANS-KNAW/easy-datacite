/**
 *
 */
package nl.knaw.dans.easy.web.template;

import java.util.Map;

import nl.knaw.dans.common.wicket.components.CommonForm;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Superclass of form to use for every form within the Easy application.
 */
public abstract class AbstractEasyForm<T> extends CommonForm<T>
{
    private static final long serialVersionUID = 3422879786964201174L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEasyForm.class);

    public AbstractEasyForm(final String wicketId, final IModel<T> model)
    {
        super(wicketId, model);
    }

    public AbstractEasyForm(String wicketId)
    {
        super(wicketId);
    }

    @Override
    protected void onError()
    {
        error(getString(EasyResources.DEFAULT_FORM_ERROR));
        super.onError();
    }

    /**
     * Get the current user of the session or <code>null</code> if no user is logged in.
     *
     * @return current user or null
     */
    protected EasyUser getSessionUser()
    {
        return ((EasySession) getSession()).getUser();
    }

    /**
     * Create the url to the specified page.
     *
     * @param pageClass
     *        Page to create url for.
     * @param parameterMap
     *        parameterMap
     * @return Url string
     */
    protected String createPageURL(Class<? extends WebPage> pageClass, final Map<String, String> parameterMap)
    {
        // this works
        //String pageUrl = getBaseUrl() + this.urlFor(pageClass, new PageParameters(parameterMap));
        //pageUrl = pageUrl.replaceAll("\\.\\./", "");

        // this should be better
        String absUrl = RequestUtils.toAbsolutePath((String) this.urlFor(pageClass, new PageParameters(parameterMap)));
        LOGGER.debug("absolute url=" + absUrl);
        final String pageUrl = absUrl;

        LOGGER.debug("URL: " + pageUrl);
        return pageUrl;
    }

}
