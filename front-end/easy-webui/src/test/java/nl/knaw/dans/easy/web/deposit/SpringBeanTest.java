package nl.knaw.dans.easy.web.deposit;

import static org.junit.Assert.fail;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

// http://docs.spring.io/spring-batch/reference/html/testing.html
// @ContextConfiguration(locations = {"applicationContext.xml"})
// @TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
// @RunWith(SpringJUnit4ClassRunner.class)
public class SpringBeanTest {

    EasyDepositService depositService;

    public static class DebugPage extends AbstractEasyPage {

        //@SpringBean(name = "depositService")
        private DepositService depositService;

        public DepositService getDepositService() {
            return depositService;
        }
    }

    @Test
    public void realService() {

        depositService = new EasyDepositService();
        EasyApplicationContextMock applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity();
        applicationContext.setDepositService(depositService);
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        PowerMock.replayAll();
        // the right hand of the equasion produces a org.apache.wicket.proxy.LazyInitProxyFactory$JdkHandler
        Assume.assumeTrue(depositService == ((DebugPage) tester.startPage(DebugPage.class)).getDepositService());
        Assume.assumeTrue(depositService == ((DebugPage) tester.startPage(new DebugPage())).getDepositService());
        fail("SpringBean injection problem seems fixed, apply in DepositPage and remove calls to Services.set in DepositTest");
        PowerMock.resetAll();
    }
}
