package nl.knaw.dans.easy.sword.util;

import java.io.File;

import nl.knaw.dans.easy.sword.Context;
import nl.knaw.dans.easy.sword.EasyBusinessFacade;

import org.junit.BeforeClass;
import org.purl.sword.base.Deposit;

public class SubmitFixture extends EasySwordServerTester {
    public static final String INPUT_DIR = "src/test/resources/input/";
    public static final String PROPER_ZIP = new File(INPUT_DIR + "data-plus-meta.zip").getPath();

    @BeforeClass
    static public void setTemp() {
        new Context().setUnzip("target/tmp");
    }

    protected static String getZip(String string) {
        return new File(INPUT_DIR + string + ".zip").getPath();
    }

    public static File getFile(String string) {
        return new File(INPUT_DIR + string);
    }

    protected void execute(final Deposit deposit, final String zip) throws Exception {
        EasyBusinessFacade.resetNoOpSubmitCounter();
        easySwordServer.doDeposit(deposit);
    }
}
