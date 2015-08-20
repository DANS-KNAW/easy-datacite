package nl.knaw.dans.easy.tools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import nl.knaw.dans.common.fedora.Fedora;
import nl.knaw.dans.common.lang.exception.ConfigurationException;
import nl.knaw.dans.common.lang.repo.DmoStores;
import nl.knaw.dans.common.lang.util.Args;
import nl.knaw.dans.easy.data.Data;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationOnlineTest {

    public static final String INCLUDE_ALL_CONTEXT = "cfg/app/include-all-context.xml";

    private static final Logger logger = LoggerFactory.getLogger(ApplicationOnlineTest.class);

    @BeforeClass
    public static void beforeClass() {
        Args vmArgs = new Args();
        vmArgs.setApplicationContext(INCLUDE_ALL_CONTEXT);
        try {
            if (!Application.isInitialized())
                Application.initialize(vmArgs);
        }
        catch (ConfigurationException e) {
            logger.error("Unable to start test", e);
            fail(e.getMessage());
        }
    }

    @Test
    public void getFedora() throws Exception {
        Fedora fedora = Application.getFedora();
        String serverVersion = fedora.getRepository().getServerVersion();
        logger.debug("Fedora version=" + serverVersion);
        assertNotNull(serverVersion);
    }

}
