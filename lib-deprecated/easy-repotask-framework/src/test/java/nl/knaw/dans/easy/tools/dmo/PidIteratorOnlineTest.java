package nl.knaw.dans.easy.tools.dmo;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.easy.tools.ApplicationOnlineTest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PidIteratorOnlineTest extends ApplicationOnlineTest {

    private static final Logger logger = LoggerFactory.getLogger(PidIteratorOnlineTest.class);

    @Test
    public void doPidIterator() throws Exception {
        PidIterator iter = new PidIterator(new DmoNamespace("easy-sdef"));
        while (iter.hasNext()) {
            logger.debug(iter.next());
        }
    }

}
