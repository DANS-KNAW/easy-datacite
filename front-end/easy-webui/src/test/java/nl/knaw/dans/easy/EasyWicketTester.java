package nl.knaw.dans.easy;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.AbstractRestartResponseException;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.util.tester.ITestPageSource;
import org.apache.wicket.util.tester.WicketTester;
import org.powermock.api.easymock.PowerMock;

/**
 * One of the two helper classes to ease unit testing of the easy webui application, see also
 * {@link EasyApplicationContextMock}.<br>
 * <br>
 * Pages may call setResponsePage or throw a {@link AbstractRestartResponseException}. The expected
 * rendered page would be another than the started page. In those cases you can't assert the rendered
 * page when the page was started with {@link #startPage(ITestPageSource)}. Consider to create a subclass
 * of the page under test to invoke the desired constructor.<br>
 * <br>
 * Below a common pattern, the tests in the packages nl.dans.easy.web.authn.* may serve as the first
 * working examples. The method tester.debugComponentTrees() shows the paths and contents of the fields
 * on the rendered page, a very handy resource to set up your expectations.
 * 
 * <pre>
 * // preparation
 * EasyUser user = new EasyUserImpl(&quot;sessionUserId&quot;);
 * EasyApplicationContextMock contextMock = new EasyApplicationContextMock();
 * contextMock.expectStandardSecurity(false);
 * contextMock.expectDefaultResources();
 * contextMock.expectNoDepositsBy(user);
 * contextMock.expectAuthenticatedAs(user);
 * 
 * // execution
 * tester = EasyWicketTester.create(contextMock);
 * Powermock.replayAll();
 * tester.startPage(RegistrationPage.class);
 * 
 * // verification
 * tester.dumpPage();
 * tester.assertRenderedPage(RegistrationPage.class);
 * tester.assertLabel(&quot;registrationForm:editablePanel:form:content:text&quot;, &quot;In order to be registered, ...&quot;);
 * 
 * // further execution (of course to be followed by further verifications)
 * tester.clickLink(&quot;registrationForm:editablePanel:form:cancelLink&quot;);
 * 
 * // cleanup
 * PowerMock.resetAll();
 * </pre>
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
     * {@link EasySession#setLoggedIn}. A usage scenario is given at class level.
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
            public Session newSession(final Request request, final Response response)
            {
                final UsernamePasswordAuthentication authentication = applicationContextMock.getAuthentication();
                final EasySession session = new EasySession(request);
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
     * Convenience method for create(...), Powermock.replayAll(), startPage()
     * 
     * @param applicationContext
     *        mock for the file applicationContext.xml
     * @param pageClass
     * @return
     */
    public static EasyWicketTester startPage(final EasyApplicationContextMock applicationContext, final Class<? extends WebPage> pageClass)
    {
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        PowerMock.replayAll();
        tester.startPage(pageClass);
        return tester;
    }

    /**
     * Convenience method for create(...), Powermock.replayAll(), startPage()
     * 
     * @param applicationContext
     *        mock for the file applicationContext.xml
     * @param pageClass
     * @param parameters
     * @return
     */
    public static EasyWicketTester startPage(final EasyApplicationContextMock applicationContext, final Class<? extends WebPage> pageClass,
            final PageParameters parameters)
    {
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        PowerMock.replayAll();
        tester.startPage(pageClass, parameters);
        return tester;
    }

    /**
     * Dumps the source of the last rendered <code>Page</code> in
     * target/pageDumps/[package]/[test-class]/[calling-method].html
     */
    @Override
    public void dumpPage()
    {
        final StackTraceElement caller = new Exception().getStackTrace()[1];
        dump(createDumpFileName("", caller));
    }

    /**
     * Dumps the source of the last rendered <code>Page</code> in
     * target/pageDumps/[package]/[test-class]/[calling-method][-suffix].html
     * 
     * @param suffix the last portion of the created file name
     * @throws Exception
     */
    public void dumpPage(final String suffix) throws Exception
    {
        final StackTraceElement caller = new Exception().getStackTrace()[1];
        dump(createDumpFileName("-" + suffix, caller));
    }

    private File createDumpFileName(final String suffix, final StackTraceElement caller)
    {
        final String testPackage = caller.getClassName().replaceAll("[.][^.]*$", "");
        final String testClass = caller.getClassName().replaceAll(".*[.]", "");
        final String testMethod = caller.getMethodName();
        final File file = new File("target/pageDumps/" + testPackage.replaceAll("\\.", "-") + "/" + testClass + "/" + testMethod + suffix + ".html");
        return file;
    }

    private void dump(final File file)
    {
        try
        {
            final String document = hackLookAndFeel(getServletResponse().getDocument());
            file.getParentFile().mkdirs();
            FileUtils.write(file, document);
        }
        catch (final IOException e)
        {
            super.dumpPage();
        }
    }

    private String hackLookAndFeel(final String document) throws IOException
    {
        final String webuiHome = new File(".").getCanonicalPath().replaceAll("/\\.$", "");
        final String dansWicketHome = new File("../../lib/dans-wicket").getCanonicalPath().replaceAll("/\\.$", "");
        return document//
                // next seems to work only for <a href=...> not within the head section
                // .replace("<head>", "<head><base href='XXX'>")
                .replaceAll("( src=.)\\?", "#")// avoid recursive IFrame's
                .replaceAll("( src=.)../images/", "$1" + webuiHome + "/src/main/webapp/images/")//
                .replaceAll("( href=.)../images/", "$1" + webuiHome + "/src/main/webapp/images/")//
                .replaceAll("( href=.)resources/nl.knaw.dans.easy.web.template.Style", "$1" + webuiHome + "/src/main/java/nl/knaw/dans/easy/web/template")//
                .replaceAll("( src=.)resources/nl.knaw.dans.easy.web.template.Style", "$1" + webuiHome + "/src/main/java/nl/knaw/dans/easy/web/template")//
                .replaceAll("( href=.)resources/nl.knaw.dans.common.wicket.components.explorer.style.",
                        "$1" + dansWicketHome + "/lib/dans-wicket/src/main/java/nl/knaw/dans/common/wicket/components/explorer/style/")//
                // next are incomplete (dots/slashes, packages may even live in other projects)
                // .replaceAll("( href=.)resources/", "$1../../../../src/main/resources/")//
                // .replaceAll("( src=.)resources/", "$1../../../../src/main/resources/")//
                .replaceAll("( href=.)../css/", "$1" + webuiHome + "/src/main/webapp/css/")//
                .replaceAll("( href=.)../yui/", "$1" + webuiHome + "/src/main/webapp/yui/")//
                .replaceAll("( src=.)../yui/", "$1" + webuiHome + "/src/main/webapp/yui/")//
                // next is dirty: in theory different packages may use the same file names
                .replace("href=\"../styles.css", "href=\"" + webuiHome + "/src/main/java/nl/knaw/dans/easy/web/authn/login/styles.css");
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
