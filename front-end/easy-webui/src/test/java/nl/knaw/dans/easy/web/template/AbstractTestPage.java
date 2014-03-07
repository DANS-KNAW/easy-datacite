package nl.knaw.dans.easy.web.template;

import static org.easymock.EasyMock.createMock;

import java.io.Serializable;
import java.util.MissingResourceException;

import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.util.Messenger;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.wicketutil.EasyMessageTester;

import org.apache.wicket.Page;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTestPage implements Serializable
{

    private static final long serialVersionUID = 7303398658389815055L;

    private static Logger logger = LoggerFactory.getLogger(AbstractTestPage.class);

    private static final String LINE = "-----------------------------------------------------------------------------------------------------";

    protected UserService userService;
    protected DepositService depositService;

    protected EasyMessageTester tester;

    @Before
    public void before()
    {
        createMockContext();
        System.setProperty("easy.home", "../easy-home");
        EasyWicketApplication application = new EasyWicketApplication();
        tester = new EasyMessageTester(application);
        new Security(new CodedAuthz());
    }

    /**
     * Create mockContext with all mock services.
     */
    private void createMockContext()
    {
        logger.debug("Create mock application context");

        userService = createMock(UserService.class);
        new Services().setUserService(userService);

        depositService = createMock(DepositService.class);
        new Services().setDepositService(depositService);
    }

    protected void startOfTest(String name)
    {
        logger.debug(LINE);
        logger.debug("     TEST " + name + "         ");
        logger.debug(LINE);
    }

    protected void checkStateKeyResources(Messenger<?> messenger, Page page)
    {
        StringBuilder sb = new StringBuilder();
        for (String stateKey : messenger.getAllStateKeys())
        {
            try
            {
                page.getString(stateKey);
            }
            catch (MissingResourceException e)
            {
                sb.append(stateKey + "\n");
            }
        }
        if (sb.length() > 0)
        {
            logger.error(LINE);
            logger.error("MISSING RESOURCES! missing keys are:\n" + sb.toString());
            logger.error(LINE);
            final String pageName = page.getClass().getName();
            throw new MissingResourceException("Missing resources in " + pageName + ".", pageName, "See log.");
        }
    }

    protected EasyUser getValidUser()
    {
        EasyUser jan = new EasyUserImpl();
        // set required data
        jan.setId("jan01");
        jan.setInitials("J.A.N.");
        jan.setFirstname("Jan");
        jan.setSurname("Jansen");
        jan.setPassword("secret");
        jan.setEmail("jan@jansen.com");
        return jan;
    }

}
