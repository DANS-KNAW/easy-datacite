package nl.knaw.dans.easy.tools.imex;

import org.junit.Ignore;
import org.junit.Test;

import nl.knaw.dans.easy.tools.ApplicationOnlineTest;

public class DobIngestTaskOnlineTest extends ApplicationOnlineTest {

    @Test
    @Ignore("Test mutates data in Fedora.")
    public void ingestEasyAppObjects() throws Exception {
        DobIngestTask diTask = new DobIngestTask();
        diTask.setDobDirectory("dob/easy-dobs");
        diTask.setPurgeBeforeIngest(true);
        diTask.run(null);
    }

}
