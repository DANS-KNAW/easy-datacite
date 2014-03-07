package nl.knaw.dans.common.fedora;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryAccessorOnlineTest extends AbstractRepositoryOnlineTest
{

    private static RepositoryAccessor repositoryAccessor;

    private static final Logger logger = LoggerFactory.getLogger(RepositoryAccessorOnlineTest.class);

    private boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforeClass() throws RepositoryException
    {
        repositoryAccessor = new RepositoryAccessor(getRepository());
    }

    @Test
    public void describeRepository() throws RepositoryException
    {
        String desc = repositoryAccessor.describeRepository().toString();
        if (verbose)
            logger.debug(desc + "\n");
    }

}
