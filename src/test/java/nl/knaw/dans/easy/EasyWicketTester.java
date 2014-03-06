package nl.knaw.dans.easy;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.AbstractRestartResponseException;
import org.apache.wicket.Component;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.util.tester.WicketTester;

/**
 * A helper class to ease unit testing of the easy webui application. The tests in the packages
 * nl.dans.easy.web.authn.* may serve as the initial working examples. A common pattern (see also
 * {@link WicketTester}):
 * 
 * <pre>
 * // preparation
 * EasyUser user = new EasyUserImpl(&quot;sessionUserId&quot;);
 * EasyApplicationContextMock contectMock = new EasyApplicationContextMock(false);
 * contectMock.expectNoDepositsBy(user);
 * contectMock.expectAuthenticatedAs(user);
 * 
 * // execution
 * tester = EasyWicketTester.create(contectMock);
 * Powermock.replayAll();
 * tester.startPage(RegistrationPage.class);
 * 
 * // verification
 * tester.dumpPage();
 * tester.assertRenderedPage(RegistrationPage.class);
 * tester.assertLabel(&quot;registrationForm:editablePanel:form:content:text&quot;, &quot;In order to be registered, ...&quot;);
 * 
 * // further execution (af course to be followed by further verifications)
 * tester.clickLink(&quot;registrationForm:editablePanel:form:cancelLink&quot;);
 * 
 * // cleanup
 * PowerMock.resetAll();
 * </pre>
 * 
 * The method tester.debugComponentTrees() shows the paths and contents of the fields on the page, a very
 * handy resource to set up your expectations. <br>
 * <br>
 * The expectXxx methods and the non-default constructors of {@link EasyApplicationContextMock} add
 * mocked beans that are commonly required for most pages, think of the tool bars. You can extend the
 * expectations for these beans or put alternative and/or additional mocked beans. The provided mocking
 * assumes the use of annotated SpringBean injection. You may have to replace the deprecated use of
 * Services.getXyZ() in the page under test and its components by:
 * 
 * <pre>
 * &#064;SpringBean(name = &quot;xyZ&quot;)
 * private XyZ xyZ;
 * </pre>
 * 
 * Pages may call setResponsePage or throw a {@link AbstractRestartResponseException}. The expected
 * rendered page would be another than the started page. In those cases you can't assert the rendered
 * page when the page was started with
 * {@link EasyWicketTester#startPage(org.apache.wicket.util.tester.ITestPageSource)}. Consider to create
 * a subclass of the page under test to invoke the desired constructor.
 */
public class EasyWicketTester extends WicketTester
{
    private EasyWicketTester(final WebApplication application)
    {
        super(application);
    }

    /**
     * Creates a WicketTester that does not flood target/work with files, collects rendered pages at
     * target/pageDumps and gives a few clearer messages in case of failed assertions. A user provided to
     * {@link EasyApplicationContextMock#expectAuthenticatedAs} is forwarded to
     * {@link EasySession#setLoggedIn}.
     * 
     * @param applicationContextMock
     *        mock for the file applicationContext.xml
     * @return
     */
    public static EasyWicketTester create(final EasyApplicationContextMock applicationContextMock)
    {
        final EasyWicketApplication application = new EasyWicketApplication()
        {
            @Override
            public Session newSession(Request request, Response response)
            {
                UsernamePasswordAuthentication authentication = applicationContextMock.getAuthentication();
                EasySession session = new EasySession(request);
                if (authentication != null)
                    session.setLoggedIn(authentication);
                return session;
            }

            @Override
            protected ISessionStore newSessionStore()
            {
                // Copied via stackoverflow.com from WicketTester:
                // "Don't use a filestore, or we spawn lots of threads, which makes things slow."
                // It also appears it leaves the folder target/work empty what saves the need for
                // cleanup. While testing without "mvn clean" the number of files can add up quick,
                // what slows down a refresh in eclipse.
                return new HttpSessionStore(this);
            }
        };
        application.setApplicationContext(applicationContextMock);
        return new EasyWicketTester(application);
    }

    /**
     * Dumps the source of the last rendered <code>Page</code> in
     * target/pageDumps/[package]/[test-class]/[calling-method].html
     */
    @Override
    public void dumpPage()
    {
        try
        {
            final StackTraceElement caller = new Exception().getStackTrace()[1];
            final String testPackage = caller.getClassName().replaceAll("[.][^.]*$", "");
            final String testClass = caller.getClassName().replaceAll(".*[.]", "");
            final String testMethod = caller.getMethodName();
            final File file = new File("target/pageDumps/" + testPackage + "/" + testClass + "/" + testMethod + ".html");
            // hack for the look and feel
            final String document = getServletResponse().getDocument()//
                    // next seems to work only for <a href=...> not within the head section
                    // .replace("<head>", "<head><base href='XXX'>")
                    .replaceAll("( src=.)../images/", "$1../../../../src/main/webapp/images/")//
                    .replaceAll("( href=.)../images/", "$1../../../../src/main/webapp/images/")//
                    // next are incomplete (dots/slashes, packages may even live in other projects)
                    // .replaceAll("( href=.)resources/", "$1../../../../src/main/resources/")//
                    // .replaceAll("( src=.)resources/", "$1../../../../src/main/resources/")//
                    .replaceAll("( href=.)../css/", "$1../../../../src/main/webapp/css/")//
                    .replaceAll("( href=.)../yui/", "$1../../../../src/main/webapp/yui/")//
                    .replaceAll("( src=.)../yui/", "$1../../../../src/main/webapp/yui/")//
                    // next is dirty: in theory different packages may use the same file names
                    .replace("href=\"../styles.css", "href=\"../../../../src/main/java/nl/knaw/dans/easy/web/authn/login/styles.css");
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
            fail(path + " expected to equal [" + expected + "] but got [" + label + "]");
    }

    public void assertLabelContains(final String path, final String expected)
    {
        final String label = getLabelValue(path, expected);
        if (label != null && !label.contains(expected))
            fail(path + " expected to contain [" + expected + "] but got [" + label + "]");
    }

    private String getLabelValue(final String path, final String expected)
    {
        final Component component = getComponentFromLastRenderedPage(path);
        if (component == null)
        {
            fail(path + " expected [" + expected + "] but was not found ");
            return null;
        }
        final String label = component.getDefaultModelObjectAsString();
        if (label == null)
            fail(path + " expected [" + expected + "] but did not find modelObject");
        return label;
    }
}
