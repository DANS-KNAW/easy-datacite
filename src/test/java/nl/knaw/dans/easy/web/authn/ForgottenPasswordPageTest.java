package nl.knaw.dans.easy.web.authn;

import org.junit.Test;

import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.web.template.AbstractTestPage;

public class ForgottenPasswordPageTest extends AbstractTestPage
{

    private static final long serialVersionUID = 419412054309668629L;

    @Test
    public void checkStateKeyResources()
    {
        startOfTest("checkStateKeyResources");
        tester.startPage(ForgottenPasswordPage.class);
        checkStateKeyResources(new ForgottenPasswordMessenger(), tester.getLastRenderedPage());
    }

}
