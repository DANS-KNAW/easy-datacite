package nl.knaw.dans.common.lang.log;

import java.io.File;

import org.junit.Test;

public class ReporterTest {

    @Test
    public void reporting() throws Exception {
        File file = new File("target/test-reports");
        file.mkdirs();
        Reporter reporter = new Reporter(file, false);
        reporter.addReport(new OverviewReport());
        reporter.addReport(new LoggerReport());

        reporter.info(new Event("test", "foo", "bar"));

        reporter.warn(new Event("test2", "bla", "blaa"));

        reporter.error(new Event("test3", new RuntimeException("knal!"), "bla", "blaaa"));
    }

}
