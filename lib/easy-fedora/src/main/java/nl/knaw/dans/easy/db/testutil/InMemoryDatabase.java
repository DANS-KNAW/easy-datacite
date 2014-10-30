package nl.knaw.dans.easy.db.testutil;

import java.io.Closeable;
import java.util.Arrays;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.db.ThreadLocalSessionFactory;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemMetadataImpl;
import nl.knaw.dans.easy.domain.dataset.FolderItemImpl;
import nl.knaw.dans.easy.domain.dataset.item.AbstractItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;

import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryDatabase implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDatabase.class);

    private static final ThreadLocalSessionFactory SESSION_FACTORY = ThreadLocalSessionFactory.instance();

    private final Session session;

    /**
     * Creates access to an in memory database for testing purposes. Must be called before creating the {@link FedoraFileStoreAccess} bean. Typically called
     * from a BeforeClass, Before or Test method.
     */
    public InMemoryDatabase() {
        FedoraDbTestSchema.init();
        session = SESSION_FACTORY.openSession();
    }

    /**
     * Clears the content created in the in-memory database. Typically called from an AfterClass, After or Test method.
     * 
     * @param fileStoreAccess
     *        the instance created by {@link InMemoryDatabase#initDB()}
     */
    public void close() {
        SESSION_FACTORY.closeSession();
        FedoraDbTestSchema.reset();
    }

    /** Makes the inserted items available for retrieval. */
    public void flush() {
        session.flush();
        if (LOGGER.isDebugEnabled()) {
            final String[] qs = {"select sid, parentSid, datasetSid, path, name from " + FolderItemVO.class.getName(),
                    "select sid, parentSid, datasetSid, path, name, size, mimetype, creatorRole, visibleTo, accessibleTo from " + FileItemVO.class.getName()};
            for (final String q : qs)
                LOGGER.debug(Arrays.deepToString((Object[]) session.createQuery(q).list().toArray()));
        }
    }

    /**
     * Inserts a folder item into the in-memory database.
     * 
     * @param id
     *        identification of the folder
     * @param parent
     *        folder item or dataset
     * @param label
     *        path and name
     * @return
     * @throws DomainException
     */
    public FolderItem insertFolder(final int id, final DatasetItemContainer parent, final String label) throws DomainException {
        final FolderItemImpl item = new FolderItemImpl(new DmoStoreId(FolderItem.NAMESPACE, id + "").getStoreId());
        item.setLabel(label);
        item.setParent(parent);
        item.setDatasetId(getDatasetId(parent));

        session.save(new FolderItemVO(item));
        return item;
    }

    /**
     * Inserts a file item into the in-memory database.
     * 
     * @param id
     *        identification of the file.
     * @param parent
     *        folder item or dataset
     * @param label
     *        path and name
     * @param creatorRole
     * @param visibleTo
     * @param accessibleTo
     * @return
     */
    public FileItem insertFile(final int id, final DatasetItemContainer parent, final String label, final CreatorRole creatorRole, final VisibleTo visibleTo,
            final AccessibleTo accessibleTo)
    {
        final FileItemMetadataImpl fileItemMetadata = new FileItemMetadataImpl(new DmoStoreId(FileItem.NAMESPACE, id + ""));
        fileItemMetadata.setAccessibleTo(accessibleTo);
        fileItemMetadata.setVisibleTo(visibleTo);
        fileItemMetadata.setCreatorRole(creatorRole);
        fileItemMetadata.setParentDmoStoreId(parent.getDmoStoreId());
        fileItemMetadata.setDatasetDmoStoreId(getDatasetId(parent));

        final FileItemImpl item = new FileItemImpl(new DmoStoreId(FileItem.NAMESPACE, id + "").getStoreId());
        item.setFileItemMetadata(fileItemMetadata);
        item.setLabel(label);
        item.setMimeType("text");
        item.setSize(1);

        session.save(new FileItemVO(item));
        return item;
    }

    private static DmoStoreId getDatasetId(final DatasetItemContainer parent) {
        if (parent instanceof FileItem)
            throw new IllegalArgumentException("a file item cannot be a parent");
        if (parent instanceof FolderItem)
            return ((FolderItem) parent).getDatasetId();
        if (!(parent instanceof DatasetImpl))
            throw new IllegalArgumentException(parent.getClass().getName() + "can not be a parent");
        return parent.getDmoStoreId();
    }

    public void deleteAll(Class<? extends AbstractItemVO> itemClass) {

        for (final Object id : session.createQuery("SELECT sid FROM " + itemClass.getName()).list())
            session.delete(itemClass.getName(), session.get(itemClass, (String) id));
    }
}
