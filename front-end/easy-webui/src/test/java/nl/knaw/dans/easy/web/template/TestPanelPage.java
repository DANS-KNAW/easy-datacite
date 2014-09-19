package nl.knaw.dans.easy.web.template;

import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.wicket.util.tester.ITestPanelSource;

public class TestPanelPage extends AbstractEasyPage {
    private static final long serialVersionUID = 1L;

    public TestPanelPage(final ITestPanelSource testPanelSource) {
        add(testPanelSource.getTestPanel("panel"));
    }
}
