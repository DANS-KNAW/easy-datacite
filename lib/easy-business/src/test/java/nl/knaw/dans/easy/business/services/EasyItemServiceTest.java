package nl.knaw.dans.easy.business.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemDescription;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.dataset.FolderItemImpl;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.domain.worker.WorkReporter;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.util.TestHelper;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AbstractDmoFactory.class)
public class EasyItemServiceTest extends TestHelper
{

    private static EasyStore easyStore;
    private static FileStoreAccess fileStoreAccess;

    private static EasyItemService service;

    @BeforeClass
    public static void beforeClass()
    {
        new Security(new CodedAuthz());

        easyStore = EasyMock.createMock(EasyStore.class);
        fileStoreAccess = EasyMock.createMock(FileStoreAccess.class);

        Data data = new Data();
        data.setEasyStore(easyStore);
        data.setFileStoreAccess(fileStoreAccess);

        service = new EasyItemService();
    }

    @AfterClass
    public static void afterClass()
    {
        // the next test class should not inherit from this one
        Data data = new Data();
        data.setEasyStore(null);
        data.setFileStoreAccess(null);
    }

    @Test
    public void getFileItemDescription() throws Exception
    {
        EasyUser sessionUser = getTestUser();
        Dataset dataset = new DatasetImpl("easy-dataset:1");
        dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.PUBLISHED);
        FileItem fileItem = new FileItemImpl("easy-file:1");
        fileItem.setDatasetId(dataset.getDmoStoreId());

        DmoStoreId fileItemId = new DmoStoreId("easy-file:1");
        EasyMock.reset(easyStore);
        EasyMock.expect(easyStore.retrieve(fileItemId)).andReturn(fileItem);
        EasyMock.replay(easyStore);

        FileItemDescription fid = service.getFileItemDescription(sessionUser, dataset, fileItemId);

        EasyMock.verify(easyStore);

        assertEquals(fid.getFileItemMetadata().getDmoStoreId(), fileItemId);
        assertNotNull(fid.getDescriptiveMetadata());
        assertNotNull(fid.getDescriptiveMetadata().getProperties());
    }

    @Ignore("SydSynchronizer is not mockable")
    @Test
    public void addDirectoryContents() throws ResourceNotFoundException, ServiceException, ObjectNotInStoreException, RepositoryException
    {
        TestReporter reporter = new TestReporter();
        EasyUser sessionUser = getTestUser();
        List<File> filesToIngest = new ArrayList<File>();
        filesToIngest.add(Tester.getFile("test-files/EasyItemService/filesToIngest/folder1"));
        filesToIngest.add(Tester.getFile("test-files/EasyItemService/filesToIngest/folder1/file1A.txt"));

        File rootFile = Tester.getFile("test-files/EasyItemService/filesToIngest");
        Dataset dataset = new DatasetImpl("easy-dataset:1");
        dataset.getAdministrativeMetadata().setDepositor(sessionUser);
        DmoStoreId parentId = dataset.getDmoStoreId();
        DmoStoreId datasetId = new DmoStoreId("easy-dataset:1");
        DmoStoreId folderItemId = new DmoStoreId("easy-folder:original");

        PowerMock.mockStatic(AbstractDmoFactory.class);
        EasyMock.reset(easyStore, fileStoreAccess);

        EasyMock.expect(easyStore.retrieve(datasetId)).andReturn(dataset);
        EasyMock.expect(fileStoreAccess.getFilesAndFolders(datasetId, -1, -1, null, null)).andReturn(new ArrayList<ItemVO>()).times(1);
        EasyMock.expect(AbstractDmoFactory.newDmo(FolderItem.NAMESPACE)).andReturn(new FolderItemImpl("easy-folder:original"));
        EasyMock.expect(fileStoreAccess.getFilesAndFolders(folderItemId, -1, -1, null, null)).andReturn(new ArrayList<ItemVO>()).times(1);

        EasyMock.expect(AbstractDmoFactory.newDmo(FolderItem.NAMESPACE)).andReturn(new FolderItemImpl("easy-folder:1"));
        EasyMock.expect(AbstractDmoFactory.newDmo(FileItem.NAMESPACE)).andReturn(new FileItemImpl("easy-file:1"));

        EasyMock.expect(easyStore.ingest(EasyMock.isA(FileItem.class), EasyMock.eq("Ingested by  (testUser)"))).andReturn("easy-file:1");
        EasyMock.expect(easyStore.ingest(EasyMock.isA(FolderItem.class), EasyMock.eq("Ingested by  (testUser)"))).andReturn("easy-folder:1");
        EasyMock.expect(easyStore.ingest(dataset, "Ingested by  (testUser)")).andReturn("easy-dataset:1");

        EasyMock.replay(easyStore, fileStoreAccess);
        PowerMock.replay(AbstractDmoFactory.class);
        service.addDirectoryContents(sessionUser, dataset, parentId, rootFile, filesToIngest, reporter);

        EasyMock.verify(easyStore, fileStoreAccess);
        PowerMock.verify(AbstractDmoFactory.class);

        assertTrue(reporter.workStarted);
        assertEquals(3, reporter.getIngestedObjectCount());
        assertTrue(reporter.workEnded);
    }

    @Test(expected = CommonSecurityException.class)
    public void updateObjects() throws ServiceException
    {
        Dataset dataset = new DatasetImpl("easy-dataset:1");

        List<DmoStoreId> sidList = new ArrayList<DmoStoreId>();
        sidList.add(new DmoStoreId("foo:21"));
        UpdateInfo info = new UpdateInfo(VisibleTo.NONE, AccessibleTo.ANONYMOUS, "bla", true);
        service.updateObjects(getTestUser(), dataset, sidList, info, null);
    }

    @Test(expected = ServiceException.class)
    public void addDirectoryContentsWithRuntimeException() throws ResourceNotFoundException, ObjectNotInStoreException, RepositoryException, ServiceException
    {
        TestReporter reporter = new TestReporter();
        EasyUser sessionUser = getTestUser();
        List<File> filesToIngest = new ArrayList<File>();
        filesToIngest.add(Tester.getFile("test-files/EasyItemService/filesToIngest/folder1"));
        filesToIngest.add(Tester.getFile("test-files/EasyItemService/filesToIngest/folder1/file1A.txt"));

        File rootFile = Tester.getFile("test-files/EasyItemService/filesToIngest");
        Dataset dataset = new DatasetImpl("easy-dataset:1");
        dataset.getAdministrativeMetadata().setDepositor(sessionUser);
        DmoStoreId parentId = dataset.getDmoStoreId();
        DmoStoreId datasetId = new DmoStoreId("easy-dataset:1");

        EasyMock.reset(easyStore, fileStoreAccess);

        EasyMock.expect(easyStore.retrieve(datasetId)).andReturn(dataset);
        EasyMock.expect(fileStoreAccess.getFilesAndFolders(datasetId, -1, -1, null, null)).andThrow(new ApplicationException("I'm too tired to run."));

        EasyMock.replay(easyStore, fileStoreAccess);
        service.addDirectoryContents(sessionUser, dataset, parentId, rootFile, filesToIngest, reporter);

        EasyMock.verify(easyStore, fileStoreAccess);

        // no way to get a hold on EasyUnitOfWork. The log should contain a line like
        // Closed UnitOfWork. [close] nl.knaw.dans.easy.store.EasyUnitOfWork.
    }

    @Test(expected = CommonSecurityException.class)
    public void testEmbargoOngetZippedContent() throws Exception
    {
        EasyUser testUser = getTestUser();
        Dataset dataset = EasyMock.createMock(Dataset.class);
        EasyMock.expect(dataset.getAdministrativeState()).andReturn(DatasetState.PUBLISHED).anyTimes();
        EasyMock.expect(dataset.isUnderEmbargo(EasyMock.isA(DateTime.class))).andReturn(true).anyTimes();
        EasyMock.expect(dataset.isUnderEmbargo()).andReturn(true).anyTimes();
        EasyMock.expect(dataset.hasDepositor(testUser)).andReturn(false).anyTimes();
        EasyMock.expect(dataset.getOwnerId()).andReturn("owner_id");

        EasyMock.replay(dataset);
        service.getZippedContent(testUser, dataset, null);
        EasyMock.verify(dataset);
    }

    @Test(expected = CommonSecurityException.class)
    public void testEmbargoOngetContent() throws Exception
    {
        EasyUser testUser = getTestUser();
        Dataset dataset = EasyMock.createMock(Dataset.class);
        EasyMock.expect(dataset.getAdministrativeState()).andReturn(DatasetState.PUBLISHED).anyTimes();
        EasyMock.expect(dataset.isUnderEmbargo(EasyMock.isA(DateTime.class))).andReturn(true).anyTimes();
        EasyMock.expect(dataset.isUnderEmbargo()).andReturn(true).anyTimes();
        EasyMock.expect(dataset.hasDepositor(testUser)).andReturn(false).anyTimes();
        EasyMock.expect(dataset.getOwnerId()).andReturn("owner_id");
        EasyMock.replay(dataset);
        service.getContent(testUser, dataset, null);
        EasyMock.verify(dataset);
    }

    private EasyUser getTestUser()
    {
        EasyUser sessionUser = new EasyUserImpl("testUser");
        sessionUser.setState(State.ACTIVE);
        return sessionUser;
    }

    private static class TestReporter extends WorkReporter
    {

        List<Throwable> reportedExceptions = new ArrayList<Throwable>();
        boolean workStarted;
        boolean workEnded;

        @Override
        public void onException(Throwable t)
        {
            super.onException(t);
            reportedExceptions.add(t);
        }

        @Override
        public boolean onWorkStart()
        {
            workStarted = true;
            return super.onWorkStart();
        }

        @Override
        public void onWorkEnd()
        {
            workEnded = true;
            super.onWorkEnd();
        }
    }

}
