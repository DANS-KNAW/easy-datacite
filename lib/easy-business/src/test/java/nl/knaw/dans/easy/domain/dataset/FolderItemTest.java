package nl.knaw.dans.easy.domain.dataset;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

import org.easymock.EasyMock;
import org.junit.Test;

public class FolderItemTest {
    @Test
    public void addChildEarlyBinding() throws XMLSerializationException, RepositoryException, DomainException {

        mockFileStoreAccess(true);

        FolderItem fo1 = new FolderItemImpl("folder:1");

        FileItem fi1 = new FileItemImpl("file:1");
        fo1.addFileOrFolder(fi1);

        fi1.setCreatorRole(CreatorRole.ARCHIVIST);
        fi1.setAccessibleTo(AccessibleTo.ANONYMOUS);

        FolderItem fo2 = new FolderItemImpl("folder:2");

        FileItem fi2 = new FileItemImpl("file:2");
        fo2.addFileOrFolder(fi2);
        fo1.addFileOrFolder(fo2);

        fi2.setCreatorRole(CreatorRole.DEPOSITOR);
        fi2.setVisibleTo(VisibleTo.KNOWN);
    }

    private void mockFileStoreAccess(boolean hasMember) throws StoreAccessException {
        FileStoreAccess fileStoreAccess = createMock(FileStoreAccess.class);
        expect(fileStoreAccess.hasMember(isA(DmoStoreId.class), EasyMock.eq(FileItemVO.class))).andStubReturn(hasMember);
        expect(fileStoreAccess.hasMember(isA(DmoStoreId.class), EasyMock.eq(FolderItemVO.class))).andStubReturn(hasMember);
        new Data().setFileStoreAccess(fileStoreAccess);
        replayAll();
    }

    @Test
    public void setParentTest() throws XMLSerializationException, RepositoryException, DomainException {
        FolderItem fo1 = new FolderItemImpl("folder:1");
        FolderItem fo2 = new FolderItemImpl("folder:2");

        FileItem fi1 = new FileItemImpl("file:1");
        FileItem fi2 = new FileItemImpl("file:2");

        fi1.setCreatorRole(CreatorRole.ARCHIVIST);
        fi1.setVisibleTo(VisibleTo.NONE);

        fi1.setParent(fo1);

        fi2.setCreatorRole(CreatorRole.ARCHIVIST);
        fi2.setVisibleTo(VisibleTo.ANONYMOUS);

        fi2.setParent(fo2);
        fo2.setParent(fo1);
    }
}
