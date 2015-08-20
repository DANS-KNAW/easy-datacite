package nl.knaw.dans.easy.task;

import nl.knaw.dans.common.lang.util.Args;
import nl.knaw.dans.easy.tools.Application;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

/**
 * First build and unzip target/tar.gz<br>
 * Copy your applications.properties for easy-webui into unzipped<br>
 * JVM argument: -DEASY_EXPORT_HOME=target/task-easy-export-1.0-SNAPSHOT<br>
 */
public class OnlineTest {
    @Before
    public void resetApplication() {
        Whitebox.setInternalState(Application.class, "INSTANCE", (Application) null);
    }

    @Test
    public void arg() throws Exception {
        String inputPropertyKey = "dataset.ids";
        // System.setProperty(inputPropertyKey, "src/test/resources/input.txt");
        System.setProperty(inputPropertyKey, "easy-dataset:1 easy-dataset:1");

        String folderPropertyKey = "export.folder";
        System.setProperty(folderPropertyKey, "target/export");

        try {
            Args prArgs = new Args();
            Application.initialize(prArgs);
            Application.run();
        }
        finally {
            System.getProperties().remove(inputPropertyKey);
            System.getProperties().remove(folderPropertyKey);
        }
    }
}
