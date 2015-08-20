package nl.knaw.dans.easy.tools.task.am.permisssions;

import nl.knaw.dans.common.lang.exception.ConfigurationException;
import nl.knaw.dans.common.lang.util.Args;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.task.am.permissions.PermissionRequestTask;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class PermissionRequestTaskOnlineTest {

    private String dmsaaLocation = "/Users/ecco/Public/AIPstore/xmldata/dmsaa.xml";

    @BeforeClass
    public static void beforeClass() throws ConfigurationException {
        String[] arguments = {"application.context=cfg/test-context.xml"};
        Args args = new Args(arguments);
        Application.initialize(args);
    }

    @Test
    @Ignore("Contains local path")
    public void run() throws Exception {
        PermissionRequestTask prTask = new PermissionRequestTask(dmsaaLocation) {

            @Override
            protected IdMap getMostRecentIdMap(String aipId) throws FatalTaskException {
                return new IdMap("easy-dataset:12", aipId, "urn:thepid");
            }
        };

        prTask.run(new JointMap());
    }

}
