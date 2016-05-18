package nl.knaw.dans.easy.web.admin;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class Ddm2EmdDocTest {
    @Test
    public void createSwordPackagingDocFragment() throws IOException {
        File file = new File("target/pageDumps/swordPackagingFragmentHelp.html");
        file.getParentFile().mkdirs();
        Ddm2EmdDoc.main(new String[] {file.getAbsolutePath()});
    }
}
