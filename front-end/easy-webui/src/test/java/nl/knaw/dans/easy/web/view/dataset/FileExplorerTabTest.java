package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;

import org.junit.BeforeClass;
import org.junit.Test;

public class FileExplorerTabTest extends TabTestFixture {
    private static final String TAB_PATH = "tabs:tabs-container:tabs:2:link";

    @BeforeClass
    public static void fillDatabase() throws Exception {
        DatasetImpl dataset = new DatasetImpl(DATASET_STORE_ID.getStoreId());
        FolderItem folder = inMemoryDatabase.insertFolder(1, dataset, "folder");
        inMemoryDatabase.insertFile(1, dataset, "file.txt", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);
        inMemoryDatabase.insertFile(2, folder, "folder/subfile.txt", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);
        inMemoryDatabase.flush();
    }

    @Test
    public void FileExplorerTab() throws Exception {
        applicationContext.expectAuthenticatedAsVisitor().addRole(Role.ARCHIVIST);
        final EasyWicketTester tester = startPage();
        tester.clickLink(TAB_PATH);
        tester.dumpPage();
        tester.debugComponentTrees();
    }
}
