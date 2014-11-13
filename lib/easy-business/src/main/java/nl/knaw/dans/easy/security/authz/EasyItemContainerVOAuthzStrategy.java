package nl.knaw.dans.easy.security.authz;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemAccessibleTo;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVisibleTo;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.VisibleTo;

public class EasyItemContainerVOAuthzStrategy extends AbstractItemContainerAuthzStrategy {

    private static final long serialVersionUID = -6233534115714650363L;
    private FolderItemVO folderItemVO;

    protected EasyItemContainerVOAuthzStrategy() {}

    protected EasyItemContainerVOAuthzStrategy(User user, Object target, Object... contextObjects) {
        super(user, contextObjects);
        if (target instanceof FolderItemVO) {
            folderItemVO = (FolderItemVO) target;
        }
        checkAttributes();
    }

    protected EasyItemContainerVOAuthzStrategy(Object target) {
        if (target instanceof FolderItemVO) {
            folderItemVO = (FolderItemVO) target;
        }
    }

    @Override
    protected void checkAttributes() {
        super.checkAttributes();
        if (folderItemVO == null)
            throw new IllegalArgumentException("Insufficient parameters: no folderItemVO");
        String datasetId = getDataset().getStoreId();
        if (!(datasetId.equals(folderItemVO.getDatasetSid()) || datasetId.equals(folderItemVO.getSid())))
            throw new IllegalArgumentException("FolderItemVO is not given dataset, nor part of given dataset");
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
    public EasyItemContainerVOAuthzStrategy newStrategy(User user, Object target, Object... contextObjects) {
        return new EasyItemContainerVOAuthzStrategy(user, target, contextObjects);
    }

    @Override
    public EasyItemContainerVOAuthzStrategy sameStrategy(Object target) {
        EasyItemContainerVOAuthzStrategy sameStrategy = new EasyItemContainerVOAuthzStrategy(target);
        super.clone(sameStrategy);

        sameStrategy.checkAttributes();
        return sameStrategy;
    }

    @Override
    Set<AccessibleTo> getAccessibilities() {
        try {
            return Data.getFileStoreAccess().getItemVoAccessibilities(folderItemVO);
        }
        catch (StoreAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    Set<VisibleTo> getVisibilities() {
        try {
            return Data.getFileStoreAccess().getItemVoVisibilities(folderItemVO);
        }
        catch (StoreAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
