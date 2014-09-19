package nl.knaw.dans.easy.store.fedora;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.db.testutil.InMemoryDatabase;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileItemVOAttribute;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FedoraCountFilesTest
{
    private static DmoStoreId fileStoreId;
    private static DmoStoreId folderStoreId;
    private static DmoStoreId datasetStoreId;
    private static FileStoreAccess fileStoreAccess;
    private static InMemoryDatabase inMemoryDB;

    @BeforeClass
    public static void initDB() throws Exception
    {
        inMemoryDB = new InMemoryDatabase();
        final DatasetItemContainer dataset = new DatasetImpl(new DmoStoreId(Dataset.NAMESPACE, "1").getStoreId());
        final FolderItem folder = inMemoryDB.insertFolder(1, dataset, "a");
        final FileItem file = inMemoryDB.insertFile(1, folder, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        fileStoreId = file.getDmoStoreId();
        folderStoreId = folder.getDmoStoreId();
        datasetStoreId = dataset.getDmoStoreId();

        inMemoryDB.flush();
        fileStoreAccess = new FedoraFileStoreAccess();
    }

    @AfterClass
    public static void closeDB()
    {
        inMemoryDB.close();
    }

    @Test
    public void folderItemImpl_getChildCount() throws Exception
    {
        final int count = fileStoreAccess.getDirectMemberCount(folderStoreId, FileItemVO.class)
                + fileStoreAccess.getDirectMemberCount(folderStoreId, FolderItemVO.class);
        assertThat(count, equalTo(1));
    }

    @Test
    public void folderItemImpl_getChildFileCount() throws Exception
    {
        final int count = fileStoreAccess.getDirectMemberCount(folderStoreId, FileItemVO.class);
        assertThat(count, equalTo(1));
    }

    @Test
    public void folderItemImpl_getChildFolderCount() throws Exception
    {
        final int count = fileStoreAccess.getDirectMemberCount(folderStoreId, FolderItemVO.class);
        assertThat(count, equalTo(0));
    }

    @Test
    public void folderItemImpl_getTotalFileCount() throws Exception
    {
        final int count = fileStoreAccess.getTotalMemberCount(folderStoreId, FileItemVO.class);
        assertThat(count, equalTo(1));
    }

    @Test
    public void folderItemImpl_getTotalFolderCount() throws Exception
    {
        final int count = fileStoreAccess.getTotalMemberCount(folderStoreId, FolderItemVO.class);
        assertThat(count, equalTo(0));
    }

    @Test
    public void folderItemImpl_getCreatorRoleFileCount() throws Exception
    {
        final int count = fileStoreAccess.getTotalMemberCount(folderStoreId, FileItemVO.class, CreatorRole.DEPOSITOR);
        assertThat(count, equalTo(1));
    }

    @Test
    public void folderItemImpl_getVisibleToFileCount() throws Exception
    {
        final int count = fileStoreAccess.getTotalMemberCount(folderStoreId, FileItemVO.class, VisibleTo.ANONYMOUS);
        assertThat(count, equalTo(1));
    }

    @Test
    public void folderItemImpl_getAccessibleToFileCount() throws Exception
    {
        final int count = fileStoreAccess.getTotalMemberCount(folderStoreId, FileItemVO.class, AccessibleTo.KNOWN);
        assertThat(count, equalTo(1));
    }

    @Test
    public void datasetImpl_getChildFileCount() throws Exception
    {
        final int count = fileStoreAccess.getDirectMemberCount(datasetStoreId, FileItemVO.class);
        assertThat(count, equalTo(0));
    }

    @Test
    public void datasetImpl_getChildFolderCount() throws Exception
    {
        final int count = fileStoreAccess.getDirectMemberCount(datasetStoreId, FolderItemVO.class);
        assertThat(count, equalTo(1));
    }

    @Test
    public void datasetImpl_getTotalFileCount() throws Exception
    {
        final int count = fileStoreAccess.getTotalMemberCount(datasetStoreId, FileItemVO.class);
        assertThat(count, equalTo(1));
    }

    @Test
    public void datasetImpl_getTotalFolderCount() throws Exception
    {
        final int count = fileStoreAccess.getTotalMemberCount(datasetStoreId, FolderItemVO.class);
        assertThat(count, equalTo(1));
    }

    @Test
    public void datasetImpl_getCreatorRoleFileCount() throws Exception
    {
        final int count = fileStoreAccess.getTotalMemberCount(datasetStoreId, FileItemVO.class, CreatorRole.DEPOSITOR);
        assertThat(count, equalTo(1));
    }

    @Test
    public void datasetImpl_getVisibleToFileCount() throws Exception
    {
        final int count = fileStoreAccess.getTotalMemberCount(datasetStoreId, FileItemVO.class, VisibleTo.ANONYMOUS);
        assertThat(count, equalTo(1));
    }

    @Test
    public void datasetImpl_getAccessibleToFileCount() throws Exception
    {
        final int count = fileStoreAccess.getTotalMemberCount(datasetStoreId, FileItemVO.class, AccessibleTo.KNOWN);
        assertThat(count, equalTo(1));
    }

    @Test
    public void datasetImpl_hasPermissionRestrictedItems() throws Exception
    {
        final boolean count = fileStoreAccess.hasMember(datasetStoreId, FileItemVO.class, AccessibleTo.RESTRICTED_REQUEST);
        assertThat(count, equalTo(false));
    }

    @Test
    public void datasetImpl_hasGroupRestrictedItems() throws Exception
    {
        final boolean count = fileStoreAccess.hasMember(datasetStoreId, FileItemVO.class, AccessibleTo.RESTRICTED_GROUP);
        assertThat(count, equalTo(false));
    }

    @Test
    public void datasetImpl_hasVisibleItems() throws Exception
    {
        // TODO logic differs from counts in DatasetViewPage.createTitleModel() and
        // IntermediatePage.generatePrePublishWarnings
        // TODO a copy-paste typo might have slipped through in the above methods
        // TODO called hasVisibleFiles needs the most thorough double check.
        // TODO correct place for this method?
        // last two arguments need (expensive?) admin-metadata, but they might not be used
        // calling FileStoreAcces from DatasetImpl does not feel good either
        // Required for DatsetFilesPanel and AbstractDatasetAutzStrategy.
        // Als dit dubelop is (Authz en tests in panels/page worden niet consequent toegepast),
        // kan de aanroep/method misschien ook beperkt worden tot AuthZ.
        assertThat(fileStoreAccess.hasVisibleFiles(datasetStoreId, true, true, true), equalTo(true));
        assertThat(fileStoreAccess.hasVisibleFiles(datasetStoreId, false, false, false), equalTo(true));
    }

    // @formatter:off
    
// TODO eliminate the calls below
// TODO task (see also issue EASY-774) clean up <foxml:xmlContent><icmd:item-container-md

//    $ egrep -r \\.get[a-zA-Z]+Count  ~/git/service/easy/easy-app/front-end/easy-webui/src/test/java
//    $ egrep -r \\.has[a-zA-Z]+Items  ~/git/service/easy/easy-app/front-end/easy-webui/src/test/java

//        lib/easy-business/.../business/item/ItemWorker.java:                        updateLine += "totalFileCount=" + itemContainer.getTotalFileCount() + "; ";
//        lib/easy-business/.../business/item/ItemWorker.java:                                updateLine += v.toString() + "=" + itemContainer.getVisibleToFileCount(v) + " ";
//        lib/easy-business/.../business/item/ItemWorker.java:                                updateLine += v.toString() + "=" + itemContainer.getAccessibleToFileCount(v) + " ";
//        lib/easy-business/.../domain/dataset/DatasetImpl.java:        return getDatasetItemContainerMetadata().getChildFileCount();
//        lib/easy-business/.../domain/dataset/DatasetImpl.java:        return getDatasetItemContainerMetadata().getChildFolderCount();
//        lib/easy-business/.../domain/dataset/DatasetImpl.java:        return getDatasetItemContainerMetadata().getTotalFileCount();
//        lib/easy-business/.../domain/dataset/DatasetImpl.java:        return getDatasetItemContainerMetadata().getTotalFolderCount();
//        lib/easy-business/.../domain/dataset/DatasetImpl.java:        return getDatasetItemContainerMetadata().getCreatorRoleFileCount(creatorRole);
//        lib/easy-business/.../domain/dataset/DatasetImpl.java:        return getDatasetItemContainerMetadata().getVissibleToFileCount(visibleTo);
//        lib/easy-business/.../domain/dataset/DatasetImpl.java:        return getDatasetItemContainerMetadata().getAccessibleToFileCount(accessibleTo);
//        lib/easy-business/.../domain/dataset/FolderItemImpl.java:        return getDatasetItemContainerMetadata().getChildFileCount();
//        lib/easy-business/.../domain/dataset/FolderItemImpl.java:        return getDatasetItemContainerMetadata().getChildFolderCount();
//        lib/easy-business/.../domain/dataset/FolderItemImpl.java:        return getDatasetItemContainerMetadata().getTotalFileCount();
//        lib/easy-business/.../domain/dataset/FolderItemImpl.java:        return getDatasetItemContainerMetadata().getTotalFolderCount();
//        lib/easy-business/.../domain/dataset/FolderItemImpl.java:        return getDatasetItemContainerMetadata().getCreatorRoleFileCount(creatorRole);
//        lib/easy-business/.../domain/dataset/FolderItemImpl.java:        return getDatasetItemContainerMetadata().getVissibleToFileCount(visibleTo);
//        lib/easy-business/.../domain/dataset/FolderItemImpl.java:        return getDatasetItemContainerMetadata().getAccessibleToFileCount(accessibleTo);
//        lib/easy-business/.../domain/dataset/ItemContainerMetadataImpl.java:                totalFileCount += (folderItem.getTotalFileCount() * count);
//        lib/easy-business/.../domain/dataset/ItemContainerMetadataImpl.java:                totalFolderCount += (folderItem.getTotalFolderCount() * count);
//        lib/easy-business/.../domain/dataset/ItemContainerMetadataImpl.java:                    creatorRoleArray[i] = Math.max(0, creatorRoleArray[i] + (folderItem.getCreatorRoleFileCount(CreatorRole.values()[i]) * count));
//        lib/easy-business/.../domain/dataset/ItemContainerMetadataImpl.java:                    visibleToArray[i] = Math.max(0, visibleToArray[i] + (folderItem.getVisibleToFileCount(VisibleTo.values()[i]) * count));
//        lib/easy-business/.../domain/dataset/ItemContainerMetadataImpl.java:                    accessibleToArray[i] = Math.max(0, accessibleToArray[i] + (folderItem.getAccessibleToFileCount(AccessibleTo.values()[i]) * count));
    // @formatter:on

    @Test
    public void itemService_hasChildItems() throws Exception
    {
        // previously implemented with FedoraFileStoreAccess.getChildCount
        final boolean count = fileStoreAccess.hasMember(datasetStoreId, FileItemVO.class) || fileStoreAccess.hasMember(datasetStoreId, FolderItemVO.class);
        assertThat(count, equalTo(true));
    }

    @Test
    public void codeCoverage() throws Exception
    {
        final FileItemVOAttribute[] empty = {};
        assertThat(fileStoreAccess.getDirectMemberCount(datasetStoreId, FileItemVO.class, (FileItemVOAttribute[]) null), equalTo(0));
        assertThat(fileStoreAccess.getDirectMemberCount(datasetStoreId, FileItemVO.class, (FileItemVOAttribute[]) empty), equalTo(0));
        assertThat(fileStoreAccess.hasDirectMember(datasetStoreId, FolderItemVO.class), equalTo(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void hasVisibleForFolder() throws Exception
    {
        fileStoreAccess.hasVisibleFiles(folderStoreId, true, true, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noMembersForFiles() throws Exception
    {
        fileStoreAccess.getDirectMemberCount(fileStoreId, FileItemVO.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void noFolderPropertyFilters() throws Exception
    {
        fileStoreAccess.getDirectMemberCount(datasetStoreId, FolderItemVO.class, VisibleTo.ANONYMOUS);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void atMostOnePropertyFilters() throws Exception
    {
        fileStoreAccess.getDirectMemberCount(datasetStoreId, FileItemVO.class, VisibleTo.ANONYMOUS, VisibleTo.RESTRICTED_GROUP);
    }
}
