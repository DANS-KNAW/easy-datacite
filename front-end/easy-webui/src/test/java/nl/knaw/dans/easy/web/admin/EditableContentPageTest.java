package nl.knaw.dans.easy.web.admin;

import static org.apache.wicket.util.tester.WicketTesterHelper.getComponentData;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.web.editabletexts.EditableTextPage;

import org.apache.wicket.util.tester.WicketTesterHelper.ComponentData;
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
    public void workflow() throws Exception
    {
        final EasyWicketTester tester = startContentPage();
        tester.dumpPage();
        tester.assertRenderedPage(EditableContentPage.class);

        // try one that does have a file with content
        tester.clickLink("AccessRightsEditLink");

        final String cancelPath = "editablePanel:form:cancelLink";
        final String modePath = "editablePanel:form:modeLink";
        final String modeLabelPath = "editablePanel:form:modeLink:modeLinkLabel";
        final String editLabel = "[edit]";
        final String saveLabel = "[save &amp; display]";

        tester.assertRenderedPage(EditableTextPage.class);
        tester.assertLabel(modeLabelPath, editLabel);
        tester.assertInvisible(cancelPath);

        tester.clickLink(modePath);
        tester.assertLabel(modeLabelPath, saveLabel);
        tester.assertVisible(cancelPath);
        tester.dumpPage("edit");

        tester.clickLink(cancelPath);
        tester.assertLabel(modeLabelPath, editLabel);
        tester.assertInvisible(cancelPath);

        tester.clickLink(modePath);
        tester.assertLabel(modeLabelPath, saveLabel);
        tester.assertVisible(cancelPath);

        // skipped update for now as it changes line breaks
        //
        // tester.clickLink(modePath);
        // tester.assertLabel(modeLabelPath, "[edit]");
        // tester.assertInvisible(cancelPath);
    }

    @Test
    public void allTemplates() throws Exception
    {
        for (final ComponentData obj : getComponentData(startContentPage().getLastRenderedPage()))
        {
            final EasyWicketTester tester = startContentPage();
            final String path = obj.path;
            if (path.endsWith("EditLink"))
            {
                tester.clickLink(path);
                tester.dumpPage(path);
            }
        }
    }

    private EasyWicketTester startContentPage()
    {
        return EasyWicketTester.startPage(applicationContext, EditableContentPage.class);
    }
}
