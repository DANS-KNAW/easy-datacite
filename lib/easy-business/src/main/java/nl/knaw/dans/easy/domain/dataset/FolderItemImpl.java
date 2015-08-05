package nl.knaw.dans.easy.domain.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.bean.BeanFactory;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.collections.AbstractDmoContainer;
import nl.knaw.dans.common.lang.repo.collections.DmoCollection;
import nl.knaw.dans.common.lang.repo.collections.DmoContainer;
import nl.knaw.dans.common.lang.repo.collections.DmoContainerItem;
import nl.knaw.dans.common.lang.repo.exception.NoUnitOfWorkAttachedException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.Constants;
import nl.knaw.dans.easy.domain.model.DatasetItem;
import nl.knaw.dans.easy.domain.model.FolderItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolderItemImpl extends AbstractDatasetItemImpl implements FolderItem, DmoContainer {
    private static final long serialVersionUID = 2687520091667217809L;

    private ItemContainerMetadataImpl itemContainerMetadata;

    private ContainerPlaceHolder container = new ContainerPlaceHolder(this);

    private DublinCoreMetadata dc;

    public FolderItemImpl(String storeId) {
        super(storeId);
    }

    public DmoNamespace getDmoNamespace() {
        return NAMESPACE;
    }

    @Override
    public Set<String> getContentModels() {
        Set<String> contentModels = super.getContentModels();
        contentModels.add(Constants.CM_FOLDER_1);
        return contentModels;
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
        getDatasetItemMetadata().setName(label);
    }

    public DublinCoreMetadata getDublinCoreMetadata() {
        if (dc == null) {
            dc = new JiBXDublinCoreMetadata();
        }
        List<String> label = new ArrayList<String>(1);
        label.add(getLabel());
        dc.setTitle(label);
        dc.setDirty(this.isDirty());

        return dc;
    }

    public ItemContainerMetadataImpl getDatasetItemMetadata() {
        return getDatasetItemContainerMetadata();
    }

    public ItemContainerMetadataImpl getDatasetItemContainerMetadata() {
        if (itemContainerMetadata == null) {
            itemContainerMetadata = new ItemContainerMetadataImpl();
        }
        return itemContainerMetadata;
    }

    /**
     * DO NOT USE. Needed for deserialization in Store.
     * 
     * @param itemContainerMetadata
     *        ItemContainerMetadataImpl
     */
    public void setItemContainerMetadata(ItemContainerMetadataImpl itemContainerMetadata) {
        this.itemContainerMetadata = itemContainerMetadata;
    }

    public List<MetadataUnit> getMetadataUnits() {
        List<MetadataUnit> metadataUnits = super.getMetadataUnits();

        metadataUnits.add(getDublinCoreMetadata());
        metadataUnits.add(getDatasetItemContainerMetadata());

        return metadataUnits;
    }

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    public boolean isDeletable() {
        try {
            return !Data.getFileStoreAccess().hasMember(getDmoStoreId(), FileItemVO.class)
                    && !Data.getFileStoreAccess().hasMember(getDmoStoreId(), FolderItemVO.class);
        }
        catch (StoreAccessException e) {
            // also used in a toString so not everybody van catch, false seems a safe response
            logger.error(e.getMessage(), e);
            return (false);
        }
    }

    public void addChild(DmoContainerItem item) throws RepositoryException {
        container.addChild(item);
    }

    public void removeChild(DmoContainerItem item) throws RepositoryException {
        container.removeChild(item);
    }

    public List<? extends DmoContainerItem> getChildren() throws RepositoryException {
        return container.getChildren();
    }

    public void setChildren(List<? extends DmoContainerItem> children) throws RepositoryException {
        container.setChildren(children);
    }

    public Set<DmoStoreId> getChildSids() throws RepositoryException {
        return container.getChildSids();
    }

    public void removeAndDeleteChild(DmoContainerItem item) throws RepositoryException {
        container.removeAndDeleteChild(item);
    }

    public void addFileOrFolder(DatasetItem item) throws DomainException {
        try {
            container.addChild((DmoContainerItem) item);
            item.setParent(this);
        }
        catch (RepositoryException e) {
            throw new DomainException(e);
        }
    }

    @Override
    public String getAutzStrategyName() {
        return "nl.knaw.dans.easy.security.authz.EasyItemContainerAuthzStrategy";
    }

    /**
     * This object would not exist if multiple inheritance would have existed
     * 
     * @author lobo
     */
    class ContainerPlaceHolder extends AbstractDmoContainer {
        private static final long serialVersionUID = 134823904823L;
        private FolderItemImpl container;

        public ContainerPlaceHolder(FolderItemImpl folderItemImpl) {
            super(folderItemImpl.getStoreId());
            container = folderItemImpl;
        }

        public DmoNamespace getDmoNamespace() {
            return container.getDmoNamespace();
        }

        public boolean isDeletable() {
            return container.isDeletable();
        }

        @Override
        protected DmoContainer getThisDmo() {
            return container;
        }

        public Set<DmoCollection> getCollections() {
            return container.getCollections();
        }

        @Override
        public UnitOfWork getUnitOfWork() throws NoUnitOfWorkAttachedException {
            return container.getUnitOfWork();
        }
    }
}
