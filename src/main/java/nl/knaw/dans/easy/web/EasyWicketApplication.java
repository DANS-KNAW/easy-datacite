package nl.knaw.dans.easy.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.common.wicket.CommonWicketApplication;
import nl.knaw.dans.common.wicket.components.jumpoff.DansTinyMCESettings;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadProcesses;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadWebRequest;
import nl.knaw.dans.easy.web.authn.login.LoginPage;
import nl.knaw.dans.easy.web.rest.RESTstartPage;

import org.apache.wicket.Request;
import org.apache.wicket.Resource;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.javascript.DefaultJavascriptCompressor;
import org.apache.wicket.markup.html.EmptySrcAttributeCheckFilter;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.coding.BookmarkablePageRequestTargetUrlCodingStrategy;
import org.apache.wicket.resource.ContextRelativeResource;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.value.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Application object for your web application. If you want to run this application without deploying,
 * run the Start class.
 * 
 * @see nl.knaw.dans.easy.Start#main(String[])
 */
public class EasyWicketApplication extends CommonWicketApplication implements ApplicationContextAware
{
    public static final int DEFAULT_MAX_UPLOAD_SIZE_MB = 256;

    /**
     * Use this in resource urls instead of org.apache.wicket.application
     */
    public static final String WICKET_APPLICATION_ALIAS = "easy";

    public static final String DOWNLOAD_FILE_URL = "downloadfile";

    private static final Logger LOGGER = LoggerFactory.getLogger(EasyWicketApplication.class);

    private ApplicationContext applicationContext;

    /**
     * Initialize the application.
     */
    @Override
    protected void init()
    {
        super.init();
        assert applicationContext != null;
        addComponentInstantiationListener(new SpringComponentInjector(this, applicationContext, true));
        LOGGER.debug("Init of Easy Wicket Application");

        initConfiguration();
        initSecurity();
        setAliases();
        mountBookmarkables();
        mountRestService();
        LOGGER.debug("Init of Easy Wicket Application finished successfully");
    }

    private void initConfiguration()
    {
        getApplicationSettings().setPageExpiredErrorPage(ExpiredPage.class);
        getApplicationSettings().setAccessDeniedPage(LoginPage.class);
        getApplicationSettings().setInternalErrorPage(ErrorPage.class);
        // TODO: get from easy config (see EasyPropertyPlaceholderConfigurer)
        getApplicationSettings().setDefaultMaximumUploadSize(Bytes.megabytes(DEFAULT_MAX_UPLOAD_SIZE_MB));

        if (isInDevelopmentMode())
        {
            getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
            getDebugSettings().setComponentUseCheck(true);
            getMarkupSettings().setStripWicketTags(false);
            getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_EXCEPTION_PAGE);
            getDebugSettings().setAjaxDebugModeEnabled(false);
            getDebugSettings().setDevelopmentUtilitiesEnabled(true);
            getDebugSettings().setOutputMarkupContainerClassName(true);
            getResourceSettings().setJavascriptCompressor(null);
            getRequestCycleSettings().addResponseFilter(EmptySrcAttributeCheckFilter.INSTANCE);
        }
        else
        {
            getResourceSettings().setResourcePollFrequency(null);
            getDebugSettings().setComponentUseCheck(false);
            getMarkupSettings().setStripComments(true);
            getMarkupSettings().setStripWicketTags(true);
            getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
            getDebugSettings().setAjaxDebugModeEnabled(false);
            getDebugSettings().setDevelopmentUtilitiesEnabled(false);
            getResourceSettings().setJavascriptCompressor(new DefaultJavascriptCompressor());
        }

    }

    private void initSecurity()
    {
        getSecuritySettings().setAuthorizationStrategy(new EasyAuthorizationStrategy());
        SecurePackageResourceGuard guard = (SecurePackageResourceGuard) getResourceSettings().getPackageResourceGuard();
        guard.addPattern("+*.htm");
    }

    private void setAliases()
    {
        // changes http://localhost:8080/resources/org.apache.wicket.Application/...
        // into http://localhost:8080/resources/easy/...

        // must be done *before* registerSharedResources and mountBookmarkablePages
        getSharedResources().putClassAlias(org.apache.wicket.Application.class, WICKET_APPLICATION_ALIAS);
    }

    private void mountBookmarkables()
    {
        for (final ResourceBookmark bookmark : ResourceBookmark.values())
        {
            final Resource instance = getResourceInstance(bookmark);
            if (instance != null)
            {
                getSharedResources().add(bookmark.getAlias(), instance);
            }
        }

        ContextRelativeResource jumpoffMarkupCss = new ContextRelativeResource("css/jumpoff-markupo.css");
        getSharedResources().add(DansTinyMCESettings.JUMPOFF_MARKUP_CSS, jumpoffMarkupCss);

        for (final PageBookmark value : PageBookmark.values())
        {
            mountBookmarkablePage("/" + value.getAlias(), value.getAliasClass());
        }
    }

    private void mountRestService()
    {
        mount(new BookmarkablePageRequestTargetUrlCodingStrategy("rest", RESTstartPage.class, null)
        {
            @Override
            protected ValueMap decodeParameters(String urlFragment, Map<String, ?> urlParameters)
            {
                // Do nothing.
                return new ValueMap();
            }
        });
    }

    private Resource getResourceInstance(final ResourceBookmark bookmark)
    {
        Class<? extends Resource> resourceClass = bookmark.getAliasClass();
        try
        {
            return resourceClass.newInstance();
        }
        catch (final InstantiationException exception)
        {
            LOGGER.error("could not create " + resourceClass.getName(), exception);
        }
        catch (final IllegalAccessException exception)
        {
            LOGGER.error("could not create " + resourceClass.getName(), exception);
        }
        return null;
    }

    /**
     * @return The applications Home page.
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<HomePage> getHomePage()
    {
        return HomePage.class;
    }

    /**
     * Determine login page for application.
     * 
     * @return The login page.
     */
    public Class<LoginPage> getLoginPage()
    {
        return LoginPage.class;
    }

    /**
     * Create a new Session.
     * 
     * @param request
     *        Current Request
     * @param response
     *        Current Response
     * @return New Session
     */
    @Override
    public Session newSession(final Request request, final Response response)
    {
        final Session session = new EasySession(request);

        LOGGER.info("Created new session for user (" + getUserIpAddress() + ").");

        return session;
    }

    @Override
    public void sessionDestroyed(final String sessionId)
    {
        LOGGER.info("Destroying session with id " + sessionId);

        super.sessionDestroyed(sessionId);
    }

    /**
     * This method override is necessary for the EasyUpload component to work.
     */
    @Override
    protected WebRequest newWebRequest(final HttpServletRequest servletRequest)
    {
        return new EasyUploadWebRequest(servletRequest);
    }

    @Override
    protected void onDestroy()
    {
        // this makes sure that all data is rolledback before the application finishes
        // TODO: NOT TESTED!
        LOGGER.info("Closing " + this.getClass().getSimpleName());
        EasyUploadProcesses.getInstance().cancelAllUploads();
        super.onDestroy();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
