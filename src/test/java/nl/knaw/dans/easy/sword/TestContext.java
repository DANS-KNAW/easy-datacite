package nl.knaw.dans.easy.sword;

import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.sword.base.SWORDException;

// @Ignore//attempt to increase code coverage but only works without previous tests
public class TestContext
{
    @BeforeClass
    public static void reset()
    {
        // make the test independent of other tests
        new Context()
        {
            void reset()
            {
                setCollectionAbstract(null);
                setCollectionPolicy(null);
                setCollectionTitle(null);
                setCollectionTreatment(null);
                setDatasetPath(null);
                setdepositTreatment(null);
                setUnzip(null);
                setWorkspaceTitle(null);
            }
        }.reset();
    }

    @Test(expected = SWORDException.class)
    public void getUnzip() throws SWORDException
    {
        Context.getUnzip();
    }

    @Test(expected = SWORDException.class)
    public void getWorkspaceTitle() throws SWORDException
    {
        Context.getWorkspaceTitle();
    }

    @Test(expected = SWORDException.class)
    public void getCollectionTreatment() throws SWORDException
    {
        Context.getCollectionTreatment();
    }

    @Test(expected = SWORDException.class)
    public void getCollectionPolicy() throws SWORDException
    {
        Context.getCollectionPolicy();
    }

    @Test(expected = SWORDException.class)
    public void getCollectionTitle() throws SWORDException
    {
        Context.getCollectionTitle();
    }

    @Test(expected = SWORDException.class)
    public void getCollectionAbstract() throws SWORDException
    {
        Context.getCollectionAbstract();
    }

    @Test(expected = SWORDException.class)
    public void getDepositTreatment() throws SWORDException
    {
        Context.getDepositTreatment();
    }

    @Test(expected = SWORDException.class)
    public void getDatasetPath() throws SWORDException
    {
        Context.getDatasetPath();
    }

}
