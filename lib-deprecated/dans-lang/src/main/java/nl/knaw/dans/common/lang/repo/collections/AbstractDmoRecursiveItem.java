package nl.knaw.dans.common.lang.repo.collections;

import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.NoUnitOfWorkAttachedException;
import nl.knaw.dans.common.lang.repo.relations.Relations;

public abstract class AbstractDmoRecursiveItem extends AbstractDmoContainerItem implements DmoRecursiveItem {
    private static final long serialVersionUID = -7851636091622744545L;

    public static String CONTENT_MODEL = "dans-model:recursive-item-v1";

    private RecursiveContainer container;

    public AbstractDmoRecursiveItem(String storeId) {
        super(storeId);
        container = new RecursiveContainer(storeId, this);
    }

    public void addChild(DmoContainerItem item) throws RepositoryException {
        container.addChild(item);
    }

    public void removeChild(DmoContainerItem item) throws RepositoryException {
        container.removeChild(item);
    }

    public Set<DmoStoreId> getChildSids() throws RepositoryException {
        return container.getChildSids();
    }

    public List<? extends DmoContainerItem> getChildren() throws RepositoryException {
        return container.getChildren();
    }

    public void setChildren(List<? extends DmoContainerItem> children) throws RepositoryException {
        container.setChildren(children);
    }

    @Override
    public Set<String> getContentModels() {
        Set<String> contentModels = super.getContentModels();

        // replace container and containerItem content model with recursive item
        contentModels.remove(AbstractDmoContainer.CONTENTMODEL);
        contentModels.remove(AbstractDmoContainerItem.CONTENT_MODEL);
        contentModels.add(CONTENT_MODEL);

        return contentModels;
    }

    public void removeAndDeleteChild(DmoContainerItem item) throws RepositoryException {
        container.removeAndDeleteChild(item);
    }

    class RecursiveContainer extends AbstractDmoContainer {
        private static final long serialVersionUID = -888485698430954711L;
        private AbstractDmoRecursiveItem item;

        public RecursiveContainer(String storeId, AbstractDmoRecursiveItem item) {
            super(storeId);
            this.item = item;
        }

        public DmoNamespace getDmoNamespace() {
            return item.getDmoNamespace();
        }

        public boolean isDeletable() {
            return item.isDeletable();
        }

        @Override
        protected DmoContainer getThisDmo() {
            return item;
        }

        @Override
        public String getStoreId() {
            return item.getStoreId();
        }

        @Override
        public Relations getRelations() {
            return item.getRelations();
        }

        public Set<DmoCollection> getCollections() {
            return item.getCollections();
        }

        public boolean isPartOfCollection(DmoCollection collection) {
            return item.isPartOfCollection(collection);
        }

        @Override
        public UnitOfWork getUnitOfWork() throws NoUnitOfWorkAttachedException {
            return item.getUnitOfWork();
        }

    }
}
