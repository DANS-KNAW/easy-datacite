package nl.knaw.dans.easy.sword;

import java.io.FileInputStream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class OnlineTester
{
    private static final Logger logger              = LoggerFactory.getLogger(OnlineTester.class);
    private static ApplicationContext context;

    @BeforeClass
    public static void launchServer() {
        final String configLocation = "src/main/webapp/WEB-INF/include-easy.xml";
        logger.info("initializing " + configLocation);
        context = new FileSystemXmlApplicationContext(configLocation);
        logger.info("initialized " + context.getDisplayName());
    }
    
    //@Test
    public void deposit() throws Exception {
        final Deposit deposit = new Deposit();
        deposit.setUsername("migration");
        deposit.setPassword("migration");
        deposit.setFile(new FileInputStream("src/test/resources/input/data-plus-meta.zip"));
        deposit.setContentType("application/zip");
        deposit.setLocation("http://a:a@localhost:8080/easy-sword/deposit");
        final DepositResponse response = new EasySwordServer().doDeposit(deposit );
        logger.info("submitted " + response.toString());
        logger.info("submitted " + response.getHttpResponse());
    }
}
