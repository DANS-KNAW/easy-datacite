package nl.knaw.dans.easy.domain.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.collections.AbstractDmoContainer;
import nl.knaw.dans.common.lang.repo.collections.DmoCollection;
import nl.knaw.dans.common.lang.repo.collections.DmoContainer;
import nl.knaw.dans.common.lang.repo.collections.DmoContainerItem;
import nl.knaw.dans.common.lang.repo.exception.NoUnitOfWorkAttachedException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Constants;
import nl.knaw.dans.easy.domain.model.DatasetItem;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public class FolderItemImpl extends AbstractDatasetItemImpl implements FolderItem, DmoContainer
{
    private static final long         serialVersionUID = 2687520091667217809L;

    private ItemContainerMetadataImpl itemContainerMetadata;

    private ContainerPlaceHolder      container        = new ContainerPlaceHolder(this);

    private DublinCoreMetadata        dc;

    public FolderItemImpl(String storeId)
    {
        super(storeId);
    }

    public String getObjectNamespace()
    {
        return NAMESPACE;
    }

    public int getChildFileCount()
    {
        return getDatasetItemContainerMetadata().getChildFileCount();
    }

    public int getChildFolderCount()
    {
        return getDatasetItemContainerMetadata().getChildFolderCount();
    }

    public int getTotalFileCount()
    {
        return getDatasetItemContainerMetadata().getTotalFileCount();
    }

    public int getTotalFolderCount()
    {
        return getDatasetItemContainerMetadata().getTotalFolderCount();
    }

    public int getCreatorRoleFileCount(CreatorRole creatorRole)
    {
        return getDatasetItemContainerMetadata().getCreatorRoleFileCount(creatorRole);
    }

    public int getVisibleToFileCount(VisibleTo visibleTo)
    {
        return getDatasetItemContainerMetadata().getVissibleToFileCount(visibleTo);
    }

    public int getAccessibleToFileCount(AccessibleTo accessibleTo)
    {
        return getDatasetItemContainerMetadata().getAccessibleToFileCount(accessibleTo);
    }

    @Override
    public Set<String> getContentModels()
    {
        Set<String> contentModels = super.getContentModels();
        contentModels.add(Constants.CM_FOLDER_1);
        return contentModels;
    }

    @Override
    public void setLabel(String label)
    {
        super.setLabel(label);
        getDatasetItemContainerMetadata().setName(label);
    }

    public DublinCoreMetadata getDublinCoreMetadata()
    {
        if (dc == null)
        {
            dc = new JiBXDublinCoreMetadata();
        }
        List<String> label = new ArrayList<String>(1);
        label.add(getLabel());
        dc.setTitle(label);
        dc.setDirty(this.isDirty());

        return dc;
    }

    public AbstractItemMetadataImpl getDatasetItemMetadata()
    {
        return getDatasetItemContainerMetadata();
    }

    public ItemContainerMetadataImpl getDatasetItemContainerMetadata()
    {
        if (itemContainerMetadata == null)
        {
            itemContainerMetadata = new ItemContainerMetadataImpl(getStoreId());
        }
        itemContainerMetadata.setSid(getStoreId());
        return itemContainerMetadata;
    }

    /**
     * DO NOT USE. Needed for deserialization in Store.
     * 
     * @param itemContainerMetadata
     *        ItemContainerMetadataImpl
     */
    public void setItemContainerMetadata(ItemContainerMetadataImpl itemContainerMetadata)
    {
        this.itemContainerMetadata = itemContainerMetadata;
    }

    public List<MetadataUnit> getMetadataUnits()
    {
        List<MetadataUnit> metadataUnits = super.getMetadataUnits();

        metadataUnits.add(getDublinCoreMetadata());
        metadataUnits.add(getDatasetItemContainerMetadata());

        return metadataUnits;
    }

    public boolean isDeletable()
    {
        return getTotalFileCount() == 0 && getTotalFolderCount() == 0;
    }

    public void onDescendantStateChange(CreatorRole oldCreatorRole, CreatorRole newCreatorRole)
    {
        getDatasetItemContainerMetadata().onChildStateChange(oldCreatorRole, newCreatorRole);
        DatasetItemContainer parent = (DatasetItemContainer) getParent();
        if (parent != null)
        {
            parent.onDescendantStateChange(oldCreatorRole, newCreatorRole);
        }
    }

    public void onDescendantStateChange(VisibleTo oldVisibleTo, VisibleTo newVisibleTo)
    {
        getDatasetItemContainerMetadata().onChildStateChange(oldVisibleTo, newVisibleTo);
        DatasetItemContainer parent = (DatasetItemContainer) getParent();
        if (parent != null)
        {
            parent.onDescendantStateChange(oldVisibleTo, newVisibleTo);
        }
    }

    public void onDescendantStateChange(AccessibleTo oldAccessibleTo, AccessibleTo newAccessibleTo)
    {
        getDatasetItemContainerMetadata().onChildStateChange(oldAccessibleTo, newAccessibleTo);
        DatasetItemContainer parent = (DatasetItemContainer) getParent();
        if (parent != null)
        {
            parent.onDescendantStateChange(oldAccessibleTo, newAccessibleTo);
        }
    }

    public void addChild(DmoContainerItem item) throws RepositoryException
    {
        container.addChild(item);
    }

    public void onChildAdded(DatasetItem item)
    {
        getDatasetItemContainerMetadata().onChildAdded(item);
        callParentOnDescendantAdded(item);
    }

    private void callParentOnDescendantAdded(DatasetItem item)
    {
        if (!isRegisteredDeleted())
        {
            DmoContainer parent = getParent();
            if (parent != null && parent instanceof DatasetItemContainer)
            {
                ((DatasetItemContainer) parent).onDescendantAdded(item);
            }
        }
    }

    public void onDescendantAdded(DatasetItem item)
    {
        getDatasetItemContainerMetadata().addDescendant(item);
        callParentOnDescendantAdded(item);
    }

    public void removeChild(DmoContainerItem item) throws RepositoryException
    {
        container.removeChild(item);
        onChildRemoved((DatasetItem) item);
    }

    public void onChildRemoved(DatasetItem item)
    {
        getDatasetItemContainerMetadata().onChildRemoved(item);
        callParentOnDescendantRemoved(item);
    }

    private void callParentOnDescendantRemoved(DatasetItem item)
    {
        if (!isRegisteredDeleted())
        {
            DmoContainer parent = getParent();
            if (parent != null && parent instanceof DatasetItemContainer)
            {
                ((DatasetItemContainer) parent).onDescendantRemoved(item);
            }
        }
    }

    public void onDescendantRemoved(DatasetItem item)
    {
        getDatasetItemContainerMetadata().onDescendantRemoved(item);
        callParentOnDescendantRemoved(item);
    }

    public List<? extends DmoContainerItem> getChildren() throws RepositoryException
    {
        return container.getChildren();
    }

    public void setChildren(List<? extends DmoContainerItem> children) throws RepositoryException
    {
        container.setChildren(children);
    }

    public Set<String> getChildSids() throws RepositoryException
    {
        return container.getChildSids();
    }

    public void removeAndDeleteChild(DmoContainerItem item) throws RepositoryException
    {
        container.removeAndDeleteChild(item);
    }

    public void addFileOrFolder(DatasetItem item) throws DomainException
    {
        try
        {
            container.addChild((DmoContainerItem) item);
            item.setParent(this);
        }
        catch (RepositoryException e)
        {
            throw new DomainException(e);
        }
    }

    @Override
    public String getAutzStrategyName()
    {
        return "nl.knaw.dans.easy.security.authz.EasyItemContainerAuthzStrategy";
    }

    /**
     * This object would not exist if multiple inheritance would have existed
     * 
     * @author lobo
     */
    class ContainerPlaceHolder extends AbstractDmoContainer
    {
        private static final long serialVersionUID = 134823904823L;
        private FolderItemImpl    container;

        public ContainerPlaceHolder(FolderItemImpl folderItemImpl)
        {
            super(folderItemImpl.getStoreId());
            container = folderItemImpl;
        }

        public String getObjectNamespace()
        {
            return container.getObjectNamespace();
        }

        public boolean isDeletable()
        {
            return container.isDeletable();
        }

        @Override
        protected DmoContainer getThisDmo()
        {
            return container;
        }

        public Set<DmoCollection> getCollections()
        {
            return container.getCollections();
        }

        @Override
        public UnitOfWork getUnitOfWork() throws NoUnitOfWorkAttachedException
        {
            return container.getUnitOfWork();
        }
    }

}
