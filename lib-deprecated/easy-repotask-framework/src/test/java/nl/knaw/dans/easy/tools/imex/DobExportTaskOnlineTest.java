package nl.knaw.dans.easy.tools.imex;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.easy.tools.ApplicationOnlineTest;
import nl.knaw.dans.easy.tools.imex.DobExportTask;

import org.junit.Test;

public class DobExportTaskOnlineTest extends ApplicationOnlineTest {

    public static final String[] EASY_APP_NAMESPACES = {"easy-model", "easy-sdef", "easy-sdep", "easy-data", "easy-discipline", "easy-collection",
            "easy-research-area", "easy-interest-area"};

    @Test
    public void dumpEasyAppObjects() throws Exception {
        DobExportTask deTask = new DobExportTask();
        for (String ns : EASY_APP_NAMESPACES) {
            deTask.addNamespace(new DmoNamespace(ns));
        }
        deTask.run(null);
    }

}
