package nl.knaw.dans.easy.task;

import java.io.File;

import nl.knaw.dans.common.lang.util.Args;
import nl.knaw.dans.easy.tools.Application;

import org.junit.Test;

public class ConfigOnlineTest {
    @Test
    public void withUnzippedBuild() throws Exception {
        File projectDir = new File(".").getAbsoluteFile().getParentFile();
        // String homeEnvironmentVariable = "EASY_" + projectDir.getName().toUpperCase().replaceAll("-", "_") + "_HOME";
        String homeEnvironmentVariable = "EASY_EXPORT_HOME";
        File unzippedBuild = new File(projectDir, "target/" + projectDir.getName() + "-1.0-SNAPSHOT");
        if (!unzippedBuild.exists())
            throw new IllegalStateException("please unzip the build: " + unzippedBuild);
        System.setProperty(homeEnvironmentVariable, unzippedBuild.toString());
        Application.initialize(new Args());
    }
}
