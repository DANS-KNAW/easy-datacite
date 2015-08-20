package nl.knaw.dans.easy.tools.dmo;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.ApplicationOnlineTest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmoIteratorOnlineTest extends ApplicationOnlineTest {

    private static final Logger logger = LoggerFactory.getLogger(DmoIteratorOnlineTest.class);

    @Test
    public void fileIterator() throws Exception {
        DmoIterator<Dataset> iter = new DmoIterator<Dataset>(Dataset.NAMESPACE);
        while (iter.hasNext()) {
            Dataset dataset = iter.next();
            logger.debug(dataset.getStoreId());
        }
    }

}
