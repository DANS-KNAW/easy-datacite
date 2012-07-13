package nl.knaw.dans.easy.store.fedora;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.store.DummyFileStoreAccess;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.db.DbUtil;
import nl.knaw.dans.easy.db.exceptions.DbException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemAccessibleTo;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemCreatorRole;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVisibleTo;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.AccessibleToFieldFilter;
import nl.knaw.dans.easy.domain.dataset.item.filter.CreatorRoleFieldFilter;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.dataset.item.filter.VisibleToFieldFilter;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FedoraFileStoreAccessTest
{
	private static final Logger logger = LoggerFactory
			.getLogger(FedoraFileStoreAccessTest.class);

	private static DummyFileStoreAccess		dummyFileStore;

	private static FedoraFileStoreAccess	fileStoreAccess;
	
	@BeforeClass
	public static void beforeClass() throws StoreAccessException
	{
		FedoraDbTestSchema.init(); 
		dummyFileStore  = new DummyFileStoreAccess();
		fileStoreAccess = new FedoraFileStoreAccess();
	}

	@AfterClass
	public static void afterClass()
	{
		FedoraDbTestSchema.reset();
	}
	
	private void compareFolderItems(FolderItemVO folder1, FolderItemVO folder2)
	{
		assertTrue(
			"Folders have different amount of creator roles",  
			folder1.getCreatorRoles().size() == folder2.getCreatorRoles().size()
		);
		
		assertTrue(
			"Folders have different amount of visible to statusses",  
			folder1.getVisibleToList().size() == folder2.getVisibleToList().size()
		);

		for (FolderItemCreatorRole role1 : folder1.getCreatorRoles())
		{
			boolean found = false;
			for (FolderItemCreatorRole role2 : folder2.getCreatorRoles())
			{
				if (role1.getCreatorRole().equals(role2.getCreatorRole()))
				{
					assertTrue(
						"Same roles found but with different sids ", 
						role1.getFolderSid().equals(role2.getFolderSid()) 
					);
					found = true;
					break;
				}
			}
			
			assertTrue("Folder with sid " + folder1.getSid() +
				" has the role "+ role1.getCreatorRole().toString() +
				" which folder2 with sid "+ folder2.getSid() +
				" does not have ", found
			);
		}

		for (FolderItemVisibleTo visibleTo1 : folder1.getVisibleToList())
		{
			boolean found = false;
			for (FolderItemVisibleTo visibleTo2 : folder2.getVisibleToList())
			{
				if (visibleTo1.getVisibleTo().equals(visibleTo2.getVisibleTo()))
				{
					assertTrue(
						"Same visible to statusses found but with different sids ", 
						visibleTo1.getFolderSid().equals(visibleTo2.getFolderSid()) 
					);
					found = true;
					break;
				}
			}
			
			assertTrue("Folder with sid " + folder1.getSid() +
				" has the visible to status "+ visibleTo1.getVisibleTo().toString() +
				" which folder2 with sid "+ folder2.getSid() +
				" does not have ", found
			);
		} 

		for (FolderItemAccessibleTo accessibleTo1 : folder1.getAccessibleToList())
		{
			boolean found = false;
			for (FolderItemAccessibleTo accessibleTo2 : folder2.getAccessibleToList())
			{
				if (accessibleTo1.getAccessibleTo().equals(accessibleTo2.getAccessibleTo()))
				{
					assertTrue(
						"Same accessible to statusses found but with different sids ", 
						accessibleTo1.getFolderSid().equals(accessibleTo2.getFolderSid()) 
					);
					found = true;
					break;
				}
			}
			
			assertTrue("Folder with sid " + folder1.getSid() +
				" has the accessibility to status "+ accessibleTo1.getAccessibleTo().toString() +
				" which folder2 with sid "+ folder2.getSid() +
				" does not have ", found
			);
		} 
	}
	
	private void compareFileItems(FileItemVO file1, FileItemVO file2)
	{
		assertTrue("File items differ", file1.equals(file2));
	}

	
	/**
	 * Compares two item lists regardless of order
	 */
	private void compareItemLists(List<? extends ItemVO> list1,
			List<? extends ItemVO> list2)
	{
		if (list1.size() != list2.size())
			assertTrue("Lists are not of the same size", list1.size() == list2.size());
		for (int i = 0; i < list1.size(); i++)
		{
			ItemVO item1 = list1.get(i);
			boolean found = false;
			for (int j = 0; j < list1.size(); j++)
			{
				ItemVO item2 = list2.get(j);
				if (item1.getSid().equals(item2.getSid()))
				{
					assertTrue(
						"Items not of the same type", 
						item2.getClass().getName().equals(item1.getClass().getName())
					);

					if (item1 instanceof FolderItemVO)
					{
						compareFolderItems((FolderItemVO) item1, (FolderItemVO) item2);
					} 
					else if (item1 instanceof FileItemVO)
					{
						compareFileItems((FileItemVO) item1, (FileItemVO) item2);
					}

					found = true;
					break;
				}
			}

			assertTrue("Item with sid " + list1.get(i).getSid()
					+ " not found in list2.", found);
		}
	}

	private void insertItems(List<? extends ItemVO> items)
	{
		// save files to database
		Session session = null;
		try
		{
			session = DbUtil.getSessionFactory().openSession();
			for (ItemVO item : items)
			{
				Transaction tx = session.beginTransaction();
				tx.begin();
				session.save(item);
				tx.commit();
			}
		} finally
		{
			if (session != null)
			{
				session.flush();
				session.close();
			}
		}
	}

	private void deleteItems(List<? extends ItemVO> items)
	{
		// delete files in database
		Session session = null;
		try
		{
			session = DbUtil.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			for (ItemVO item : items)
			{
				Query query = session.createQuery("DELETE FROM "
						+ item.getClass().getName() + " WHERE pid=:sid");
				query.setString("sid", item.getSid());
				query.executeUpdate();
			}
			tx.commit();
		} finally
		{
			session.flush();
			session.close();
		}
	}

	private void insertDummyItems(String parentSid) throws StoreAccessException
	{
		// get folders from dummy file access store
		 List<FolderItemVO> folders = dummyFileStore.getFolders(
			new DmoStoreId(parentSid),
			-1,
			-1,
			null,
			null);
		 	 

		 // save folders to database
		insertItems(folders);
		
		List<FileItemVO> files = dummyFileStore.getFiles(
			new DmoStoreId(parentSid),
			-1,
			-1,
			null,
			null);
		insertItems(files) ;
		
		// recursive insert
		for (FolderItemVO folder : folders)
		{
			insertDummyItems(folder.getSid());
		} 
	}

	
	
	/*-------------------------------
	 *------------ TESTS ------------
	 *------------------------------*/
	
	private void compareDummyToDb(DmoStoreId parentSid, boolean recursive, ItemFilters filters) throws StoreAccessException
	{
		// get files from database and check if they are equal
		List<ItemVO> filesAndfolders = fileStoreAccess.getFilesAndFolders(
			parentSid,
			-1,
			-1,
			null,
			filters);

		List<ItemVO> dummyFilesAndFolders = dummyFileStore.getFilesAndFolders(
				parentSid,
				-1,
				-1,
				null,
				filters
			);

		compareItemLists(filesAndfolders, dummyFilesAndFolders);
		
		if (recursive)
		{
			for (ItemVO item : filesAndfolders)
			{
				if (item instanceof FolderItemVO)
				{
					compareDummyToDb(new DmoStoreId(item.getSid()), recursive, filters);
				}
			}
		}
	}
	
	@Test
	public void insertSelectAndCompareItemVOs() throws RepositoryException,
			HibernateException, DbException, CloneNotSupportedException
	{
		logger.debug("starting test: insertSelectAndCompareFileItemVOs");
	
		insertDummyItems(DummyFileStoreAccess.DUMMY_DATASET_SID);
		
		// compare without filters
		compareDummyToDb(new DmoStoreId(DummyFileStoreAccess.DUMMY_DATASET_SID), true, null);
		
		ItemFilters filters = new ItemFilters();

		// compare with accessible to filter
		AccessibleToFieldFilter aFilter = new AccessibleToFieldFilter(); 
		aFilter.addDesiredValues(AccessibleTo.KNOWN);
		aFilter.addDesiredValues(AccessibleTo.ANONYMOUS);
		aFilter.addDesiredValues(AccessibleTo.RESTRICTED_GROUP);
		filters.setAccessibleToFilter(aFilter);

		compareDummyToDb(new DmoStoreId(DummyFileStoreAccess.DUMMY_DATASET_SID), true, filters);

		// compare with creator role filter
		filters.clear();
		CreatorRoleFieldFilter cFilter = new CreatorRoleFieldFilter();
		cFilter.addDesiredValues(CreatorRole.ARCHIVIST);
		filters.setCreatorRoleFilter(cFilter);

		compareDummyToDb(new DmoStoreId(DummyFileStoreAccess.DUMMY_DATASET_SID), true, filters);

		// compare with visible to filter
		VisibleToFieldFilter vFilter = new VisibleToFieldFilter();
		vFilter.addDesiredValues(VisibleTo.ANONYMOUS);
		filters.setVisibleToFilter(vFilter);
		filters.setCreatorRoleFilter(null);

		compareDummyToDb(new DmoStoreId(DummyFileStoreAccess.DUMMY_DATASET_SID), true, filters);

		// compare with accessible to, visible to and creator role filter
		filters.clear();
		filters.setAccessibleToFilter(aFilter);
		filters.setVisibleToFilter(vFilter);
		filters.setCreatorRoleFilter(cFilter);

		compareDummyToDb(new DmoStoreId(DummyFileStoreAccess.DUMMY_DATASET_SID), true, filters);
	}

	@Test
	public void getFilenamesTest() throws RepositoryException
	{
		logger.debug("starting test: getFilenamesTest");

		List<String> filenames = fileStoreAccess.getFilenames(
				new DmoStoreId(DummyFileStoreAccess.DUMMY_DATASET_SID), 
				true
		);
		
		List<String> dummyFilenames = dummyFileStore.getFilenames(
				new DmoStoreId(DummyFileStoreAccess.DUMMY_DATASET_SID), 
				true
		);
		
		assertEquals(dummyFilenames.size(), filenames.size());
	}

	@Ignore("What is this test trying to proof?")
	@Test
	public void hasChildItemsTest() throws StoreAccessException
	{
	    DmoStoreId dummyDatasetId = new DmoStoreId(DummyFileStoreAccess.DUMMY_DATASET_SID);
	    boolean hasChildren = fileStoreAccess.hasChildItems(dummyDatasetId);
		assertTrue(hasChildren);
			
	}
	
	
}
