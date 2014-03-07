package nl.knaw.dans.common.lang.log;

import nl.knaw.dans.common.lang.exception.ConfigurationException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class RL_MAIN
{

    /**
     * @param args
     * @throws ConfigurationException 
     */
    public static void main(String[] args) throws ConfigurationException
    {
        ApplicationContext context = new FileSystemXmlApplicationContext("src/test/resources/test-files/log/test-log-context.xml");

        //RL rl = RL.initialize("/home/easy/batch/reports/test2/app/enz", true);
        //rl.setReporter((Reporter) context.getBean("reporter"));

        //        RL rl = RL.initialize("/home/easy/batch/reports/test2/app/enz", true);
        //        RLTestReporter testReporter = new RLTestReporter();
        //        testReporter.addReport(new OverviewReport("test-overview.csv"));
        //        rl.setReporter(testReporter);

        RL.info(new Event("main", "message2"));

    }

}
