package nl.knaw.dans.easy.security.authz;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemAccessibleTo;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVisibleTo;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
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
        try {
            return Data.getFileStoreAccess().getItemVoAccessibilities(Data.getFileStoreAccess().getFolderItemVO(itemContainer.getDmoStoreId()));
        }
        catch (StoreAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return new HashSet<AccessibleTo>();

    }

    @Override
    Set<VisibleTo> getVisibilities() {
        try {
            return Data.getFileStoreAccess().getItemVoVisibilities(Data.getFileStoreAccess().getFolderItemVO(itemContainer.getDmoStoreId()));
        }
        catch (StoreAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return new HashSet<VisibleTo>();
    }

}
