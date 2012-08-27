package nl.knaw.dans.easy.sword.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import nl.knaw.dans.easy.sword.EasyBusinessFacade;

import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.SWORDException;
import org.purl.sword.base.SwordValidationInfo;

public class SubmitFixture extends EasySwordServerTester
{
    public static final String INPUT_DIR  = "src/test/resources/input/";
    public static final String PROPER_ZIP = new File(INPUT_DIR + "data-plus-meta.zip").getPath();

    protected static String getZip(String string)
    {
        return new File(INPUT_DIR + string + ".zip").getPath();
    }

    public static File getFile(String string)
    {
        return new File(INPUT_DIR + string );
    }

    protected SwordValidationInfo execute(boolean verbose, boolean noOp, final String zip) throws Exception, SWORDException
    {
        final Deposit deposit = new Deposit();
        deposit.setUsername(MockUtil.VALID_USER_ID);
        deposit.setPassword(MockUtil.PASSWORD);
        deposit.setLocation(LOCATION);
        deposit.setVerbose(verbose);
        deposit.setNoOp(noOp);
        deposit.setFile(new FileInputStream(zip));

        return execute(deposit, "_" + new File(zip).getName().replace(".zip", ""));
    }

    protected SwordValidationInfo execute(final Deposit deposit, final String zip) throws Exception
    {
        EasyBusinessFacade.resetNoOpSubmitCounter();
        final String regexp = "-- CreationDate: .*--"; // iText generates creation date as comment,
                                                       // ignore that
        DepositResponse depositResponse = easySwordServer.doDeposit(deposit);
        final String actualResults = depositResponse.toString().replaceAll(regexp, "");
        assertAsExpected(actualResults, "deposit_" + deposit.isVerbose() + deposit.isNoOp() + zip + ".xml");
        return depositResponse.unmarshall(actualResults, new Properties());
    }
}
