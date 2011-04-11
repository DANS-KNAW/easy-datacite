package nl.knaw.dans.easy.sword;

import org.junit.BeforeClass;

public class SubmitTest extends SubmitTester
{
    @BeforeClass
    public static void setupMocking() throws Exception {
        new MockUtil().mockAll();
    }

}
