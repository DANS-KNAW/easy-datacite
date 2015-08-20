package nl.knaw.dans.easy.tools;

import static org.junit.Assert.*;
import nl.knaw.dans.common.lang.util.Args;

import org.junit.Test;

public class DefaultConfigurationOnlineTest {

    private static final String INCLUDE_ALL_CONTEXT = "cfg/dump-metadata-context.xml";

    @Test
    public void configure() throws Exception {
        assertFalse(Application.isInitialized());

        Args vmArgs = new Args();
        vmArgs.setApplicationContext(INCLUDE_ALL_CONTEXT);

        DefaultConfiguration conf = new DefaultConfiguration(vmArgs);
        conf.configure();

        assertTrue(Application.isInitialized());
    }

}
