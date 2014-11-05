package nl.knaw.dans.easy.security.authz;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.VisibleTo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyItemContainerAuthzStrategy extends AbstractItemContainerAuthzStrategy {

    private static final long serialVersionUID = -3903342896601185096L;
    protected static Logger logger = LoggerFactory.getLogger(EasyItemContainerAuthzStrategy.class);

    private DatasetItemContainer itemContainer;
    private Set<AccessibleTo> accessibilities;
    private Set<VisibleTo> visibilities;

    protected EasyItemContainerAuthzStrategy() {}

    protected EasyItemContainerAuthzStrategy(User user, Object target, Object... contextObjects) {
        super(user, contextObjects);
        if (target instanceof DatasetItemContainer) {
            itemContainer = (DatasetItemContainer) target;
        }
        checkAttributes();
    }

    protected EasyItemContainerAuthzStrategy(Object target) {
        if (target instanceof DatasetItemContainer) {
            itemContainer = (DatasetItemContainer) target;
        }
    }

    @Override
    protected void checkAttributes() {
        super.checkAttributes();
        if (itemContainer == null)
            throw new IllegalArgumentException("Insufficient parameters: no itemContainer");
        DmoStoreId datasetId = getDataset().getDmoStoreId();
        if (!(datasetId.equals(itemContainer.getDatasetItemContainerMetadata().getDatasetDmoStoreId()) || datasetId.equals(itemContainer.getDmoStoreId())))
            throw new IllegalArgumentException("ItemContainer is not given dataset, nor part of given dataset");
    }

    @Override
    public boolean canUnitBeDiscovered(String unitId) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public boolean canUnitBeRead(String unitId) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    protected boolean canAllBeRead() {
        return TriState.ALL.equals(canChildrenBeRead());
    }

    @Override
    public EasyItemContainerAuthzStrategy newStrategy(User user, Object target, Object... contextObjects) {
        return new EasyItemContainerAuthzStrategy(user, target, contextObjects);
    }

    @Override
    public EasyItemContainerAuthzStrategy sameStrategy(Object target) {
        EasyItemContainerAuthzStrategy sameStrategy = new EasyItemContainerAuthzStrategy(target);
        super.clone(sameStrategy);

        sameStrategy.checkAttributes();
        return sameStrategy;
    }

    @Override
    Set<AccessibleTo> getAccessibilities() {
        if (accessibilities == null)
            try {
                // getFileItemVO would get both at once but only for folders
                // moreover under the hood both queries are executed as sub-selects anyway
                // and we don't need the main query and a third under-the hood query
                accessibilities = Data.getFileStoreAccess().getValuesFor(itemContainer.getDmoStoreId(), AccessibleTo.class);
            }
            catch (StoreAccessException e) {
                logger.error(e.getMessage(), e);
                accessibilities = new HashSet<AccessibleTo>();
            }
        return accessibilities;
    }

    @Override
    Set<VisibleTo> getVisibilities() {
        if (visibilities == null)
            try {
                visibilities = Data.getFileStoreAccess().getValuesFor(itemContainer.getDmoStoreId(), VisibleTo.class);
            }
            catch (StoreAccessException e) {
                logger.error(e.getMessage(), e);
                visibilities = new HashSet<VisibleTo>();
            }
        return visibilities;
    }
}
