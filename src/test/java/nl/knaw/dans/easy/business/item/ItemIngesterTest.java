package nl.knaw.dans.easy.business.item;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.business.item.ItemIngester.ListFilter;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ItemIngester.class, Data.class})
public class ItemIngesterTest
{
    private static final String ACCENT_XML = //
    new String(new byte[] {109, 101, 116, 45, 97, 99, 99, -61, -87, 110, 116, 46, 120, 109, 108});
    private static final String DIACRITIC_ACCENT_XML = //
    new String(new byte[] {109, 101, 116, 45, 97, 99, 99, 101, -52, -127, 110, 116, 46, 120, 109, 108});
    private static final String GREEK_FOLDER_NAME = "src/test/resources/test-files/greekMixed";
    private static final File GREEK_FOLDER = new File(GREEK_FOLDER_NAME);
    private Dataset datasetMock;
    private EasyUser userMock;
    private File rootFileMock;
    private DatasetItemContainer parentContainerMock;
    private EasyUnitOfWork unitOfWorkMock;
    private FileStoreAccess fileStoreAccessMock;
    private File fileMock;
    private FileItem fileItemMock;

    @Before
    public void setUp() throws Exception
    {
        PowerMock.resetAll();
        datasetMock = PowerMock.createMock(Dataset.class);
        userMock = PowerMock.createMock(EasyUser.class);
        rootFileMock = PowerMock.createMock(File.class);
        parentContainerMock = PowerMock.createMock(DatasetItemContainer.class);
        unitOfWorkMock = PowerMock.createMock(EasyUnitOfWork.class);
        PowerMock.expectNew(EasyUnitOfWork.class, isA(EasyUser.class)).andReturn(unitOfWorkMock).once();
        PowerMock.mockStatic(Data.class);
        fileStoreAccessMock = PowerMock.createMock(FileStoreAccess.class);
        expect(Data.getFileStoreAccess()).andReturn(fileStoreAccessMock).anyTimes();
        fileMock = PowerMock.createMock(File.class);
        fileItemMock = PowerMock.createMock(FileItem.class);

        AbstractDmoFactory.register(FileItem.NAMESPACE, new AbstractDmoFactory<FileItem>()
        {
            @Override
            public FileItem newDmo()
            {
                return fileItemMock;
            }

            @Override
            public DmoNamespace getNamespace()
            {
                return null;
            }

            @Override
            public FileItem createDmo(String storeId)
            {
                return null;
            }
        });
    }

    @Test
    public void creationOfIngesterForDepositorDoesNotCrash()
    {
        normalUserLoggedIn();
        creatorRoleIsDepositor();
        datasetHasStoreId("easy-dataset:1");
        datasetHasAccessCategory(AccessCategory.OPEN_ACCESS);
        replayAll();
        new ItemIngester(datasetMock, userMock, null);
    }

    private void normalUserLoggedIn()
    {
        expect(userMock.isAnonymous()).andReturn(false).anyTimes();
        expect(userMock.getId()).andReturn("normal").anyTimes();
    }

    private void creatorRoleIsDepositor()
    {
        expectCreatorRole(CreatorRole.DEPOSITOR);
    }

    private void datasetHasStoreId(String id)
    {
        expect(datasetMock.getStoreId()).andReturn(id).anyTimes();
        expect(datasetMock.getDmoStoreId()).andReturn(new DmoStoreId(id)).anyTimes();
    }

    private void datasetHasAccessCategory(AccessCategory category)
    {
        expect(datasetMock.getAccessCategory()).andReturn(category).anyTimes();
    }

    private void expectCreatorRole(CreatorRole role)
    {
        expect(userMock.getCreatorRole()).andReturn(role).anyTimes();
    }

    @Test
    @Ignore
    public void issue700a() throws Exception
    {
        normalUserLoggedIn();
        creatorRoleIsDepositor();
        datasetHasStoreId("easy-dataset:1");
        datasetHasAccessCategory(AccessCategory.OPEN_ACCESS);
        parentContainerHasStoreId();
        uowMethodsCalled();
        noFilesAndFoldersUnderParentContainer();
        fileItemMock.setFile(EasyMock.isA(File.class));
        EasyMock.expectLastCall().times(2);
        fileItemMock.setCreatorRole(CreatorRole.DEPOSITOR);
        EasyMock.expectLastCall().times(2);
        fileItemMock.setDatasetId(new DmoStoreId("easy-dataset:1"));
        EasyMock.expectLastCall().times(2);
        fileItemMock.setOwnerId("normal");
        EasyMock.expectLastCall().times(2);
        fileItemMock.setParent(parentContainerMock);
        EasyMock.expectLastCall().times(2);
        fileItemMock.setVisibleTo(VisibleTo.ANONYMOUS);
        EasyMock.expectLastCall().times(2);
        fileItemMock.setAccessibleTo(AccessibleTo.KNOWN);
        EasyMock.expectLastCall().times(2);
        EasyMock.expect(unitOfWorkMock.saveAndDetach(fileItemMock)).andReturn(fileItemMock);
        EasyMock.expectLastCall().times(2);

        replayAll();

        ItemIngester ii = new ItemIngester(datasetMock, userMock, null);
        ii.workAddDirectoryContents(parentContainerMock, GREEK_FOLDER, createFilter(ACCENT_XML));

        PowerMock.verifyAll();
    }

    /**
     * Illustrates correct filter processing by
     * {@link ItemIngester#workAddDirectoryContents(DatasetItemContainer, File, FileFilter).
     */
    @Test
    public void issue700b()
    {
        System.out.println(ACCENT_XML);
        System.out.println(DIACRITIC_ACCENT_XML);
        assertThat(GREEK_FOLDER.listFiles(createFilter(ACCENT_XML)).length, is(2));
        assertThat(GREEK_FOLDER.listFiles(createFilter(DIACRITIC_ACCENT_XML)).length, is(1));
    }

    private ListFilter createFilter(String fileNameWithAccent)
    {
        List<File> fileList = new ArrayList<File>();
        fileList.add(new File(GREEK_FOLDER_NAME + "/" + fileNameWithAccent));
        fileList.add(new File(GREEK_FOLDER_NAME + "/πῶϋ.xml"));
        return new ItemIngester.ListFilter(fileList);
    }

    @Test
    public void noItemsUnderParentContainerAndNoFilesInRootFolder() throws Exception
    {
        normalUserLoggedIn();
        creatorRoleIsDepositor();
        datasetHasStoreId("easy-dataset:1");
        datasetHasAccessCategory(AccessCategory.OPEN_ACCESS);
        parentContainerHasStoreId();
        uowMethodsCalled();
        rootFileIsCalled("myRootFile");
        rootFileIsDirectory();
        noFilesAndFoldersUnderParentContainer();
        noFilesUnderRootFolder();

        replayAll();

        ItemIngester ii = new ItemIngester(datasetMock, userMock, null);
        ii.addDirectoryContents(parentContainerMock, rootFileMock);

        PowerMock.verifyAll();
    }

    private void parentContainerHasStoreId()
    {
        expect(parentContainerMock.getDmoStoreId()).andReturn(new DmoStoreId("easy-folder:1")).anyTimes();
        expect(parentContainerMock.getStoreId()).andReturn("easy-folder:1").anyTimes();
    }

    private void rootFileIsCalled(String name)
    {
        expect(rootFileMock.getName()).andReturn(name).anyTimes();
    }

    private void rootFileIsDirectory()
    {
        expect(rootFileMock.isDirectory()).andReturn(true).anyTimes();
    }

    private void noFilesAndFoldersUnderParentContainer() throws Exception
    {
        final List<ItemVO> empty = new LinkedList<ItemVO>();
        DmoStoreId folderId = new DmoStoreId("easy-folder:1");
        expect(fileStoreAccessMock.getFilesAndFolders(folderId, -1, -1, null, null)).andReturn(empty);
    }

    private void noFilesUnderRootFolder() throws Exception
    {
        expect(rootFileMock.listFiles(isA(FileFilter.class))).andReturn(new File[] {});
    }

    private void uowMethodsCalled() throws Exception
    {
        unitOfWorkMock.attach(isA(DataModelObject.class));
        EasyMock.expectLastCall().anyTimes();
        unitOfWorkMock.commit();
        EasyMock.expectLastCall().anyTimes();
        unitOfWorkMock.close();
        EasyMock.expectLastCall().anyTimes();
    }

    @Test
    public void noItemsUnderParentContainerAndOneFileInRootFolder() throws Exception
    {
        // Set up preconditions

        normalUserLoggedIn();
        creatorRoleIsDepositor();
        datasetHasStoreId("easy-dataset:1");
        datasetHasAccessCategory(AccessCategory.OPEN_ACCESS);
        parentContainerHasStoreId();
        uowMethodsCalled();
        rootFileIsCalled("myRootFile");
        rootFileIsDirectory();
        noFilesAndFoldersUnderParentContainer();
        oneFileUnderRootFolder();

        // Set up expectations
        fileItemMock.setFile(fileMock);
        fileItemMock.setCreatorRole(CreatorRole.DEPOSITOR);
        fileItemMock.setDatasetId(new DmoStoreId("easy-dataset:1"));
        fileItemMock.setOwnerId("normal");
        fileItemMock.setParent(parentContainerMock);
        fileItemMock.setVisibleTo(VisibleTo.ANONYMOUS);
        fileItemMock.setAccessibleTo(AccessibleTo.KNOWN);
        EasyMock.expect(unitOfWorkMock.saveAndDetach(fileItemMock)).andReturn(fileItemMock);

        replayAll();

        ItemIngester ii = new ItemIngester(datasetMock, userMock, null);
        ii.addDirectoryContents(parentContainerMock, rootFileMock);

        PowerMock.verifyAll();

    }

    private void oneFileUnderRootFolder()
    {
        fileHasName("myOnlyFile");
        fileIsDirectory(false);
        expect(rootFileMock.listFiles(isA(FileFilter.class))).andReturn(new File[] {fileMock});
    }

    private void fileHasName(String name)
    {
        expect(fileMock.getName()).andReturn(name).anyTimes();
    }

    private void fileIsDirectory(boolean isDirectory)
    {
        expect(fileMock.isDirectory()).andReturn(isDirectory).anyTimes();
    }
}
