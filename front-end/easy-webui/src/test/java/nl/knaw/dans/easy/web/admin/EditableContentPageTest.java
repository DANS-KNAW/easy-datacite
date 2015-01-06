package nl.knaw.dans.easy.web.admin;

import static org.apache.wicket.util.tester.WicketTesterHelper.getComponentData;
import static org.easymock.EasyMock.isA;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.common.wicket.model.TextFileModel;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.web.EditableInfoPage;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.authn.RegistrationPage;
import nl.knaw.dans.easy.web.deposit.DepositIntroPage;
import nl.knaw.dans.easy.web.deposit.DepositPanel;
import nl.knaw.dans.easy.web.editabletexts.EditableTextPage;
import nl.knaw.dans.easy.web.search.pages.MyDatasetsSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.MyRequestsSearchResultPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.util.tester.WicketTesterHelper.ComponentData;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EditableContentPageTest {
    private static final String MOCKED_EDITABLE = "target/mockedEditable";
    private static final String ORIGINAL_EDITABLE = "src/main/assembly/dist/res/example/editable";
    private EasyApplicationContextMock applicationContext;
    private EasyWicketTester tester;

    private static EasyUser sessionUser; // static for the inner Wrapper class

    public static class EditableInfoPageWrapper extends EditableInfoPage {
        public EditableInfoPageWrapper() {
            super(DepositPanel.INFO_PAGE, DepositPanel.EDITABLE_DEPOSIT_COMPLETE_TEMPLATE, sessionUser);
        }
    }

    @Before
    public void mockApplicationContext() throws Exception {
        sessionUser = new EasyUserTestImpl("mocked-user:archivist");
        sessionUser.setInitials("Archi");
        sessionUser.setSurname("Vist");
        sessionUser.addRole(Role.ARCHIVIST);
        sessionUser.setState(User.State.ACTIVE);

        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity();

        // expect custom resources, might be required to test other pages with edit-links
        applicationContext.setEditableContentHome(mockEditableFiles());
        applicationContext.setStaticContentBaseUrl("http://mocked/base/url");

        applicationContext.expectNoDatasetsInToolBar();
        applicationContext.expectAuthenticatedAs(sessionUser);
    }

    private FileSystemHomeDirectory mockEditableFiles() throws IOException {
        final File mockedEditable = new File(MOCKED_EDITABLE);
        FileUtils.copyDirectory(new File(ORIGINAL_EDITABLE), mockedEditable);
        return new FileSystemHomeDirectory(mockedEditable);
    }

    @After
    public void reset() {
        TestUtil.cleanup();
    }

    @Test
    public void commonWorkflow() throws Exception {
        startPage(EditableContentPage.class);
        tester.dumpPage();

        // try just one
        tester.clickLink("AccessRightsEditLink");
        tester.assertRenderedPage(EditableTextPage.class);

        doClicks("editablePanel");
        assertWhiteSpaceCHanges("help/AccessRights.template");
        tester.dumpPage("edit");
    }

    @Test
    public void editBanner() throws Exception {
        startPage(EditableContentPage.class);
        tester.dumpPage();
        doClicks("adminBanner");
        assertSavedContent("pages/AdminBanner.template");
        tester.dumpPage("edit");
    }

    @Test
    public void editHomePage() throws Exception {
        startPage(HomePage.class);
        tester.dumpPage();
        doClicks("editablePanel");
        assertSavedContent("pages/HomePage.template");
        tester.dumpPage("edit");
    }

    @Test
    public void depositIntroPage() throws Exception {
        applicationContext.expectDepositDisciplines();
        startPage(DepositIntroPage.class);
        tester.dumpPage();
        doClicks("editablePanel");
        assertSavedContent("pages/DepositIntro.template");
        tester.dumpPage("edit");
    }

    @Test
    public void editableInfoPage() throws Exception {
        applicationContext.expectDepositDisciplines();
        startPage(EditableInfoPageWrapper.class);
        tester.dumpPage();
        doClicks("editablePanel");
        assertSavedContent(DepositPanel.EDITABLE_DEPOSIT_COMPLETE_TEMPLATE);
        tester.dumpPage("edit");
    }

    @Ignore(value = "webui changed. Weird: edit button while in edit mode?")
    @Test
    public void registrationForm() throws Exception {
        // unusual use case but it is a bookmarkable page
        // so a logged-in archivist can reach this page
        // with easy.dans.knaw.nl/ui/register
        applicationContext.expectDisciplineChoices();
        startPage(RegistrationPage.class);
        tester.dumpPage();
        doClicks("registrationForm:editablePanel");
        assertSavedContent("pages/Registration.template");
        tester.dumpPage("edit");
    }

    @Test
    public void myDatasetsSearchResultPage() throws Exception {
        EasyMock.expect(applicationContext.getSearchService().searchMyDataset(isA(SearchRequest.class), isA(EasyUser.class))).andStubReturn(null);
        startPage(MyDatasetsSearchResultPage.class);
        tester.dumpPage();
        doClicks("myDatasetsHelpPopup:popupModal:popupHTML");
        assertSavedContent("pages/MyDatasetsSearchResult.template");
        tester.dumpPage("edit");
    }

    @Test
    public void myRequestSearchResultPage() throws Exception {
        EasyMock.expect(applicationContext.getSearchService().searchMyRequests(isA(SearchRequest.class), isA(EasyUser.class))).andStubReturn(null);
        startPage(MyRequestsSearchResultPage.class);
        tester.dumpPage();
        doClicks("editablePanel");
        assertSavedContent("pages/MyRequestsSearchResult.template");
        tester.dumpPage("edit");
    }

    @Test
    public void permissionRequestEditPanel() throws Exception {
        /*
         * The panel also uses a template. However, apply doClinks to DatasetPermissionTest.issueFirstRequest with an archivist as sessionUser is hardly worth
         * the trouble. Available test at least shows the content of the template.
         */
    }

    @Test
    public void dwonloadPanel() throws Exception {
        /*
         * TODO consider applying doClicks to the DownloadPanel when DatasetViewPage tests are implemented and cover the DescriptionPanel.
         */
    }

    @Test
    public void licensePanel() throws Exception {
        /*
         * TODO consider applying doClicks when Deposit tests are implemented and cover the EmdPanelFactory.createLicensePanel.
         */
    }

    private void doClicks(final String containerPath) throws Exception {
        final String cancelPath = containerPath + ":form:cancelLink";
        final String modePath = containerPath + ":form:modeLink";
        final String modeLabelPath = containerPath + ":form:modeLink:modeLinkLabel";
        final String editLabel = "[edit]";
        final String saveLabel = "[save &amp; display]";

        tester.assertLabel(modeLabelPath, editLabel);
        tester.assertInvisible(cancelPath);

        tester.clickLink(modePath);
        tester.assertLabel(modeLabelPath, saveLabel);
        tester.assertVisible(cancelPath);

        tester.clickLink(cancelPath);
        tester.assertLabel(modeLabelPath, editLabel);
        tester.assertInvisible(cancelPath);

        tester.clickLink(modePath);
        tester.assertLabel(modeLabelPath, saveLabel);
        tester.assertVisible(cancelPath);

        tester.debugComponentTrees();
        tester.clickLink(modePath);
        tester.assertLabel(modeLabelPath, "[edit]");
        tester.assertInvisible(cancelPath);

        // prepare to dump the page in the proper folder by the calling method
        tester.clickLink(modePath);
    }

    private void assertWhiteSpaceCHanges(final String template) throws IOException {
        final String saved = assertSavedContent(template);
        final String original2 = new TextFileModel(new File(ORIGINAL_EDITABLE, template)).getObject();
        Assert.assertThat(saved + "\n", equalTo(original2));
        // readFileToString reads "\r\n" from original
        // TextFileModel reads line by line and adds line feeds with
        // buffer.append(String.format("%n"));
    }

    private String assertSavedContent(String template) throws IOException {
        final String saved = FileUtils.readFileToString(new File(MOCKED_EDITABLE, template));
        final String original = FileUtils.readFileToString(new File(ORIGINAL_EDITABLE, template));
        Assert.assertThat(saved, equalToIgnoringWhiteSpace(original));
        return saved;
    }

    @Ignore(value = "runs for ever")
    @Test
    public void allTemplates() throws Exception {
        checkProvidedTemplateFiles(checkLinkedTemplates());
    }

    private void checkProvidedTemplateFiles(final Collection<String> usedFiles) {
        final Iterator<File> availableFiles = FileUtils.iterateFiles(new File(ORIGINAL_EDITABLE), null, true);
        for (final Iterator<File> iter = availableFiles; iter.hasNext();) {
            final String availableFile = iter.next().toString().replace(ORIGINAL_EDITABLE, "");
            if (!ignore(availableFile))
                assertTrue("obsolete template: " + availableFile, usedFiles.contains(availableFile));
        }
    }

    /**
     * Detects missing example files and misspelled place holders. Please overwrite "src/main/assembly/dist/res/example/editable" with
     * "easy.dans.knaw.nl:/opt/easy-webui-editable/" after updates.
     * 
     * @throws Exception
     */
    private Collection<String> checkLinkedTemplates() throws Exception {
        final Collection<String> usedFiles = new ArrayList<String>();
        for (final ComponentData obj : getComponentData(startPage(EditableContentPage.class).getLastRenderedPage())) {
            final String path = obj.path;
            if (path.endsWith("EditLink")) {
                tester.clickLink(path);
                tester.assertRenderedPage(EditableTextPage.class);
                tester.dumpPage(path);
                tester.debugComponentTrees();
                usedFiles.add(tester.getComponentFromLastRenderedPage("filename").getDefaultModelObjectAsString());

                // kind of "page back" for the next link:
                startPage(EditableContentPage.class);
            }
        }
        return usedFiles;
    }

    private boolean ignore(final String availableFile) {
        // for example .DS_Store
        return availableFile.startsWith("/mail/images/") || availableFile.startsWith("/.") || availableFile.endsWith(".properties");
    }

    private EasyWicketTester startPage(final Class<? extends AbstractEasyPage> pageClass) {
        tester = EasyWicketTester.startPage(applicationContext, pageClass);
        tester.assertRenderedPage(pageClass);
        return tester;
    }
}
