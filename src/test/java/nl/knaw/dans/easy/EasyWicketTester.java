package nl.knaw.dans.easy;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.Component;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;

public class EasyWicketTester extends WicketTester
{
    private final StringBuffer errors = new StringBuffer();

    private EasyWicketTester(final EasyWicketApplication application)
    {
        super(application);
    }

    public static EasyWicketTester create(final ApplicationContextMock applicationContext)
    {
        final EasyWicketApplication application = new EasyWicketApplication();
        application.setApplicationContext(applicationContext);
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
            file.getParentFile().mkdirs();
            FileUtils.write(file, getServletResponse().getDocument());
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
