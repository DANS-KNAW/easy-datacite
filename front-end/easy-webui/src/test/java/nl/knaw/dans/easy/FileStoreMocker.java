package nl.knaw.dans.easy;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;

import net.rkbloom.logdriver.LogDriver;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemMetadataImpl;
import nl.knaw.dans.easy.domain.dataset.FolderItemImpl;
import nl.knaw.dans.easy.domain.dataset.item.AbstractItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;

import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.HSQLDialect;
import org.slf4j.Logger;

public class FileStoreMocker implements Closeable, Serializable {

    private final FileStoreAccess fileStoreAccess;
    private final SessionFactory sessionFactory;

    /**
     * Creates access to an in memory database for testing purposes.
     * 
     * @throws Exception
     */
    public FileStoreMocker() throws Exception {
        URL url = new File("../../lib/easy-fedora/src/main/resources/conf/hibernate.cfg.xml").toURI().toURL();
        final Configuration configuration = new Configuration().configure(url);
        configuration.setProperty("hibernate.dialect", HSQLDialect.class.getName());
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.connection.driver_class", LogDriver.class.getName());
        configuration.setProperty("hibernate.connection.username", "sa");
        configuration.setProperty("hibernate.connection.password", "");
        configuration.setProperty("hibernate.connection.pool_size", "1");
        configuration.setProperty("hibernate.connection.autocommit", "true");
        configuration.setProperty("hibernate.connection.url", "jdbc:log:org.hsqldb.jdbcDriver:hsqldb:mem:easyfedoradb");

        final StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        fileStoreAccess = createFileStoreAccess();
    }

    /**
     * Gets a FileStoreAccess using the in memory database. Prepare tests with the insert methods of this {@link FileStoreMocker}. Note that
     * {@link EasyApplicationContextMock#getItemService()} returns a mock with delegates for those methods that call {@link FedoraFileStoreAccess} methods.
     * Other service methods will still need calls to {@link EasyMock}.expect methods.
     * 
     * @return to be used for {@link EasyApplicationContextMock#putBean()}
     * @throws StoreAccessException
     */
    public FileStoreAccess getFileStoreAccess() throws StoreAccessException {
        return fileStoreAccess;
    }

    private FedoraFileStoreAccess createFileStoreAccess() {
        try {
            return new FedoraFileStoreAccess() {
                protected boolean hasLocalConfig() {
                    return true;
                }

                protected Session getLocalSession() {
                    return getSession();
                }
            };
        }
        catch (final StoreAccessException e) {
            return null;
        }
    }

    /** Logs the files and folders in the database at debug level. */
    public void logContent(Logger logger) {
        getSession().beginTransaction();
        if (logger.isDebugEnabled()) {
            final String[] qs = {"select sid, parentSid, datasetSid, path, name from " + FolderItemVO.class.getName(),
                    "select sid, parentSid, datasetSid, path, name, size, mimetype, creatorRole, visibleTo, accessibleTo from " + FileItemVO.class.getName()};
            for (final String q : qs)
                logger.debug(Arrays.deepToString((Object[]) getSession().createQuery(q).list().toArray()));
        }
        getSession().getTransaction().commit();
    }

    /**
     * Inserts a folder item into the in-memory database.
     * 
     * @param id
     *        identification of the folder
     * @param parent
     *        folder item or dataset that has been inserted
     * @param label
     *        folder name, for backward compatibility no parent path is added if the label contains a "/"
     * @return
     * @throws DomainException
     */
    public FolderItem insertFolder(final int id, final DatasetItemContainer parent, final String label) throws DomainException {
        final String folderId = new DmoStoreId(FolderItem.NAMESPACE, id + "").getStoreId();
        return insertFolderItem(folderId, getDatasetId(parent), label, parent);
    }

    /**
     * Inserts the root folder for a dataset item into the in-memory database.
     * 
     * @param dataset
     * @return
     * @throws DomainException
     */
    public FolderItemImpl insertRootFolder(final Dataset dataset) throws DomainException {
        return insertFolderItem(dataset.getStoreId(), dataset.getDmoStoreId(), "", dataset);
    }

    private FolderItemImpl insertFolderItem(final String folderId, final DmoStoreId datasetId, final String label, final DatasetItemContainer parent)
            throws DomainException
    {
        final FolderItemImpl item = new FolderItemImpl(folderId);
        item.setDatasetId(datasetId);
        item.setLabel(buildPath(parent, label));
        item.setParent(parent);
        saveItem(new FolderItemVO(item));
        return item;
    }

    /**
     * Inserts a file item into the in-memory database.
     * 
     * @param id
     *        identification of the file.
     * @param parent
     *        folder item or dataset that has been inserted
     * @param label
     *        file name, for backward compatibility no parent path is added if the label contains a "/"
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

        final FileItemImpl item = new FileItemImpl(new DmoStoreId(FileItem.NAMESPACE, id + "").getStoreId()) {
            @Override
            public DmoStoreId getDatasetId() {
                return fileItemMetadata.getDatasetDmoStoreId();
            }

            @Override
            public DmoStoreId getParentId() {
                return parent.getDmoStoreId();
            }

            @Override
            public byte[] calcMd5() {
                return "xyz".getBytes();
            }
        };
        item.setFileItemMetadata(fileItemMetadata);
        item.setLabel(buildPath(parent, label));
        item.setMimeType("text");
        item.setSize(1);

        saveItem(new FileItemVO(item));
        return item;
    }

    private void saveItem(AbstractItemVO itemVO) {
        getSession().beginTransaction();
        getSession().save(itemVO);
        getSession().flush();
        getSession().getTransaction().commit();
    }

    private String buildPath(final DatasetItemContainer parent, final String label) {
        if (label.contains("/"))
            return label;
        else if (!(parent instanceof FolderItem))
            return label;
        else
            return parent.getLabel() + "/" + label;
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

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void close() throws IOException {
        sessionFactory.getCurrentSession().close();
        sessionFactory.close();
    }
}
