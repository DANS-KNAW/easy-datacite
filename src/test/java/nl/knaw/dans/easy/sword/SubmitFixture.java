package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileInputStream;

import nl.knaw.dans.easy.util.EasyHome;

import org.junit.Before;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDException;

public class SubmitFixture extends EasySwordServerTester
{

    protected static final String PROPER_ZIP = new File("src/test/resources/input/data-plus-meta.zip").getPath();

    @Before
    public void setupMocking() throws Exception
    {
        EasyHome.setValue(System.getProperty("easy.home"));
        MockUtil.mockAll();
    }

    protected String getZip(String string)
    {
        return new File("src/test/resources/input/" + string + ".zip").getPath();
    }

    protected void execute(boolean verbose, boolean noOp, final String zip) throws Exception, SWORDException
    {
        final Deposit deposit = new Deposit();
        deposit.setUsername(MockUtil.VALID_USER_ID);
        deposit.setPassword(MockUtil.PASSWORD);
        deposit.setLocation(LOCATION);
        deposit.setVerbose(verbose);
        deposit.setNoOp(noOp);
        deposit.setFile(new FileInputStream(zip));
        
        execute(deposit,"_"+new File(zip).getName().replace(".zip", ""));
    }

    private void execute(final Deposit deposit, final String zip) throws Exception
    {
        EasyBusinessFacade.resetNoOpSubmitCounter();
        final String regexp = "-- CreationDate: .*--"; // iText generates creation date as comment, ignore that
        final String actualResults = easySwordServer.doDeposit(deposit).toString().replaceAll(regexp, "");
        assertAsExpected(actualResults, "deposit_"+deposit.isVerbose()+deposit.isNoOp()+zip+".xml");
    }

}
