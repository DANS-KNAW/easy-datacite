package nl.knaw.dans.common.fedora;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryOnlineTest extends AbstractRepositoryOnlineTest
{

    private static final Logger logger = LoggerFactory.getLogger(RepositoryOnlineTest.class);

    private boolean verbose = Tester.isVerbose();

    @Test
    public void getUploadURL() throws RepositoryException
    {
        String uploadURL = getRepository().getUploadURL();
        if (verbose)
            logger.debug("uploadURL=" + uploadURL);
    }

}
