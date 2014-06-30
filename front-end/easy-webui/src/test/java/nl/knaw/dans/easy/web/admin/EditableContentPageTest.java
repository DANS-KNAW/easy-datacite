package nl.knaw.dans.easy.web.admin;

import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.web.editabletexts.EditableTextPage;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.util.tester.WicketTesterHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class EditableContentPageTest
{
    private EasyApplicationContextMock applicationContext;
    private EasyUserImpl sessionUser;

    @Before
    public void mockApplicationContext() throws Exception
    {
        sessionUser = new EasyUserTestImpl("mocked-user:archivist");
        sessionUser.setInitials("Archi");
        sessionUser.setSurname("Vist");
        sessionUser.addRole(Role.ARCHIVIST);
        sessionUser.setState(User.State.ACTIVE);

        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity(false);
        applicationContext.expectDefaultResources();
        applicationContext.expectNoDatasets();
        applicationContext.expectAuthenticatedAs(sessionUser);
    }

    @After
    public void reset()
    {
        PowerMock.resetAll();
    }

    @Test
    public void smokeTest() throws Exception
    {
        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, EditableContentPage.class);
        tester.dumpPage();
        tester.assertRenderedPage(EditableContentPage.class);
        tester.debugComponentTrees();
        final Page page = tester.getLastRenderedPage();
        for (final WicketTesterHelper.ComponentData obj : WicketTesterHelper.getComponentData(page))
        {
            if (obj.path.endsWith("EditLink"))
            {
                final Component component = page.get(obj.path);
                // TODO markup contains the file that should exist but how to get it?
            }
        }
        // just try one that does have a file
        tester.clickLink("AccessRightsEditLink");
        tester.assertRenderedPage(EditableTextPage.class);
        tester.dumpPage("edit");
    }
}
