package nl.knaw.dans.easy.web.authn;

import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.template.AbstractTestPage;

import org.junit.Before;
import org.junit.Test;


public class ChangePasswordPageTest extends AbstractTestPage
{

    private static final long serialVersionUID = 1260018331379899584L;
    
    @Test
    public void forbiddenPage()
    {
        startOfTest("forbiddenPage");
        tester.startPage(ChangePasswordPage.class);
        tester.assertRenderedPage(HomePage.class);
    }
    
//    @Test
//    public void checkStateKeyResources()
//    {
//        startOfTest("checkStateKeyResources");
//        EasyUser user = getValidUser();
//        tester.startPage(HomePage.class);
//        HomePage homePage = (HomePage) tester.getLastRenderedPage();
//        ((EasySession)homePage.getSession()).setLoggedIn(user);
//        tester.startPage(ChangePasswordPage.class);       
//        checkStateKeyResources(new ChangePasswordMessenger(user, false), tester.getLastRenderedPage());
//    }

}
