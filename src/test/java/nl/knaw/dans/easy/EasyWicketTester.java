package nl.knaw.dans.easy;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;

public class EasyWicketTester extends WicketTester
{
    private final StringBuffer errors = new StringBuffer();

    private EasyWicketTester(final WebApplication application)
    {
        super(application);
    }

    /**
     * @param applicationContextMock
     * @param authentications
     *        an optional authentication with the desired user for the session. Without an authentication
     *        the created tester works with an anonymous session. Note: a partial static mock of
     *        EasySession.getSessionUser() would not affect EasySession.get().getUser(), it would still
     *        return an anonymous user.
     * @return
     */
    public static EasyWicketTester create(final ApplicationContextMock applicationContextMock, final Authentication... authentications)
    {
        final EasyWicketApplication application = new EasyWicketApplication()
        {
            @Override
            public Session newSession(Request request, Response response)
            {
                EasySession session = new EasySession(request);
                if (authentications != null && authentications.length > 0 && authentications[0] != null)
                    session.setLoggedIn(authentications[0]);
                return session;
            }

            @Override
            protected ISessionStore newSessionStore()
            {
                // Copied via StackOverflow from WicketTester:
                // Don't use a filestore, or we spawn lots of threads, which makes things slow.
                // It also appears leave the folder target/work empty what saves the need for cleanup.
                return new HttpSessionStore(this);
            }
        };
        application.setApplicationContext(applicationContextMock);
        return new EasyWicketTester(application);
    }

    @Override
    public void dumpPage()
    {
        try
        {
            final StackTraceElement caller = new Exception().getStackTrace()[1];
            final String testClass = caller.getClassName().replaceAll(".*[.]", "");
            final String testMethod = caller.getMethodName();
            final File file = new File("target/pageDumps/" + testClass + "/" + testMethod + ".html");
            final String document = getServletResponse().getDocument()// hack for the look and feel
                    .replace("src='/images/", "src='../../../src/main/webapp/images/")//
                    .replace("src=\"../images/", "src=\"../../../src/main/webapp/images/")//
                    .replace("href=\"../images/", "href=\"../../../src/main/webapp/images/")//
                    .replace("href=\"resources/", "href=\"../../../src/main/resources/")//
                    .replace("src=\"resources/", "src=\"../../../src/main/resources/")//
                    .replace("href=\"../css/", "href=\"../../../src/main/webapp/css/")//
                    .replace("href=\"../yui/", "href=\"../../../src/main/webapp/yui/")//
                    .replace("src=\"../yui/", "src=\"../../../src/main/webapp/yui/")//
                    // next is dirty: in theory different packages may use the same file names
                    .replace("href=\"../styles.css", "href=\"../../../src/main/java/nl/knaw/dans/easy/web/authn/login/styles.css");
            file.getParentFile().mkdirs();
            FileUtils.write(file, document);
        }
        catch (final IOException e)
        {
            super.dumpPage();
        }
    }

    @Override
    public void assertLabel(final String path, final String expected)
    {
        final String label = getLabelValue(path, expected);
        if (label != null && !label.equals(expected))
            errors.append("\n" + path + " expected to equal [" + expected + "] but got [" + label + "]");
    }

    public void assertLabelContains(final String path, final String expected)
    {
        final String label = getLabelValue(path, expected);
        if (label != null && !label.contains(expected))
            errors.append("\n" + path + " expected to contain [" + expected + "] but got [" + label + "]");
    }

    private String getLabelValue(final String path, final String expected)
    {
        final Component component = getComponentFromLastRenderedPage(path);
        if (component == null)
        {
            errors.append("\n" + path + " expected [" + expected + "] but was not found ");
            return null;
        }
        final String label = component.getDefaultModelObjectAsString();
        if (label == null)
            errors.append("\n" + path + " expected [" + expected + "] but did not find modelObject");
        return label;
    }

    /** verify the results of collected asserts */
    public void verify()
    {
        if (errors.length() != 0)
            fail(errors.toString());
    }
}
