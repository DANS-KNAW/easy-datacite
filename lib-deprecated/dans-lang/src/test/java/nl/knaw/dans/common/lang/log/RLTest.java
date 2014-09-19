package nl.knaw.dans.common.lang.log;

import org.junit.Ignore;
import org.junit.Test;

public class RLTest {

    @Ignore("test creates directories in DEFAULT_REPORT_LOCATION ")
    @Test
    public void noInitialisation() {
        RL.reset();
        RL.info(new Event("not initialized", new RuntimeException("knal!"), "message"));
    }

    @Ignore("local environment")
    @Test
    public void initialized() throws Exception {
        RL.reset();
        RL rl = RL.initialize("/home/easy/batch/reports/test/enz/en/meer", true);
        TestReporter reporter = new TestReporter();
        reporter.addReport(new OverviewReport());
        rl.setReporter(reporter);
        RL.info(new Event("initialized", new RuntimeException("knal!"), "message2"));
    }

    private class TestReporter extends Reporter {
        @Override
        public void info(Event event) {
            event.setResourceId("test-id");
            super.info(event);
        }
    }

}
