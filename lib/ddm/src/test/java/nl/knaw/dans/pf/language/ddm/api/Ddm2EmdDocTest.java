package nl.knaw.dans.pf.language.ddm.api;

import org.junit.Test;

import java.io.File;

public class Ddm2EmdDocTest {
    @Test
    public void createSwordPackagingDocFragment() throws Exception {
        File file = new File("target/pageDumps/swordPackagingFragmentHelp.html");
        file.getParentFile().mkdirs();
        Ddm2EmdDoc.main(new String[]{file.getAbsolutePath()});
    }
}

