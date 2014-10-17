package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;

import org.junit.BeforeClass;
import org.junit.Test;

public class AdminTabTest extends TabTestFixture {
    private static final String TAB_PATH = "tabs:tabs-container:tabs:3:link";

    @BeforeClass
    public static void fillDatabase() throws Exception {
        inMemoryDatabase.insertFile(1, new DatasetImpl(DATASET_STORE_ID.getStoreId()), "tmp.txt", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS,
                AccessibleTo.ANONYMOUS);
        inMemoryDatabase.flush();
    }

    @Test
    public void adminTabNotVisible() throws Exception {
        applicationContext.expectAuthenticatedAsVisitor();
        final EasyWicketTester tester = startPage();
        tester.assertInvisible(TAB_PATH);
    }

    @Test
    public void adminTabVisible() throws Exception {
        applicationContext.expectAuthenticatedAsVisitor().addRole(Role.ARCHIVIST);
        final EasyWicketTester tester = startPage();
        tester.dumpPage();
        tester.assertVisible(TAB_PATH);
        tester.assertEnabled(TAB_PATH);
        tester.assertLabel(TAB_PATH + ":title", "Administration");
    }

    @Test
    public void adminTab() throws Exception {
        applicationContext.expectAuthenticatedAsVisitor().addRole(Role.ARCHIVIST);
        final EasyWicketTester tester = startPage();
        tester.clickLink(TAB_PATH);
        tester.dumpPage();// note that the IFrame for the upload is patched
        tester.debugComponentTrees();
    }
}
