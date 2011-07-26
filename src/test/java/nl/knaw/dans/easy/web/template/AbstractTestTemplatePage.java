package nl.knaw.dans.easy.web.template;

import static org.easymock.EasyMock.createMock;
import junit.framework.TestCase;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IFormSubmittingComponent;
import org.apache.wicket.util.tester.WicketTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests to perform on any page that extends TemplatePage.
 * 
 * @see nl.knaw.dans.easy.web.main.AbstractEasyNavPage
 * @author Herman Suijs
 */
public abstract class AbstractTestTemplatePage extends TestCase
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTestTemplatePage.class);
    protected WicketTester tester;
    protected UserService userServiceMock;
    protected ItemService itemServiceMock;
    protected DatasetService datasetServiceMock;
    protected DatasetService depositDatasetServiceMock;

    @Override
    public void setUp()
    {
        createMockContext();

        LOGGER.debug("Create application for test " + getName());
        EasyWicketApplication application = new EasyWicketApplication();

        LOGGER.debug("Create wicket tester");
        this.tester = new WicketTester(application);
    }

    /**
     * Create mockContext with all mock services.
     */
    private void createMockContext()
    {
        LOGGER.debug("Create mock application context");

        this.userServiceMock = createMock(UserService.class);
        this.datasetServiceMock = createMock(DatasetService.class);

        new Services().setUserService(this.userServiceMock);
        new Services().setDatasetService(datasetServiceMock);
    }

    /**
     * Check if an Easy form component is rendered properly.
     * 
     * @param wicketId
     *        Wicket id.
     * @param expectedClass
     *        Expected class of component.
     */
    protected void assertEasyFormComponent(final String wicketId, final Class<? extends FormComponent> expectedClass)
    {
        this.tester.assertComponent(wicketId, expectedClass);
        if (IFormSubmittingComponent.class.isAssignableFrom(expectedClass))
        {
            LOGGER.debug("Submitting component has no feedback: " + wicketId);
        }
        else
        {
            this.tester.assertInvisible(wicketId + AbstractEasyForm.SEPARATOR + AbstractEasyForm.FEEDBACK);
        }

    }

}
