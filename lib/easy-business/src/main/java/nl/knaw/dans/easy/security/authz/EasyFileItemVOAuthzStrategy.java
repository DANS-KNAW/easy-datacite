package nl.knaw.dans.easy.security.authz;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.domain.dataset.EasyFile;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;

public class EasyFileItemVOAuthzStrategy extends AbstractDatasetAutzStrategy {
    private static final long serialVersionUID = 1927285836498130123L;
    private FileItemVO fileItemVO;
    private int viewProfile = NOT_EVALUATED;
    private int readProfile = NOT_EVALUATED;

    protected EasyFileItemVOAuthzStrategy() {

    }

    protected EasyFileItemVOAuthzStrategy(User user, Object target, Object... contextObjects) {
        super(user, contextObjects);
        if (target instanceof FileItemVO) {
            fileItemVO = (FileItemVO) target;
        }
        checkAttributes();
    }

    protected EasyFileItemVOAuthzStrategy(Object target) {
        if (target instanceof FileItemVO) {
            fileItemVO = (FileItemVO) target;
        }
    }

    protected void checkAttributes() {
        super.checkAttributes();
        if (fileItemVO == null)
            throw new IllegalArgumentException("Insufficient parameters: no fileItemVO");
        if (!getDataset().getStoreId().equals(fileItemVO.getDatasetSid()))
            throw new IllegalArgumentException("FileItem is not part of given dataset");
    }

    @Override
    protected int getResourceDiscoveryProfile() {
        if (viewProfile == NOT_EVALUATED) {
            viewProfile = AccessCategory.UTIL.getBitMask(fileItemVO.getViewAccessCategory());
        }
        return viewProfile;
    }

    @Override
    protected int getResourceReadProfile() {
        if (readProfile == NOT_EVALUATED) {
            readProfile = AccessCategory.UTIL.getBitMask(fileItemVO.getReadAccessCategory());
        }
        return readProfile;
    }

    @Override
    public boolean canUnitBeDiscovered(String unitId) {
        if (EasyFile.UNIT_ID.equals(unitId)) {
            return canBeDiscovered() && profileMatches(getUserProfile(), getResourceDiscoveryProfile());
        } else {
            throw new UnsupportedOperationException("Method not implemented for unitId " + unitId);
        }
    }

    @Override
    public boolean canUnitBeRead(String unitId) {
        if (unitId == null || EasyFile.UNIT_ID.equals(unitId)) {
            return canBeRead() && profileMatches(getUserProfile(), getResourceReadProfile());
        } else {
            throw new UnsupportedOperationException("Method not implemented for unitId " + unitId);
        }
    }

    @Override
    protected boolean canAllBeRead() {
        return canUnitBeRead(null);
    }

    @Override
    public TriState canChildrenBeDiscovered() {
        return TriState.NONE;
    }

    @Override
    public TriState canChildrenBeRead() {
        return TriState.NONE;
    }

    @Override
    public EasyFileItemVOAuthzStrategy newStrategy(User user, Object target, Object... contextObjects) {
        return new EasyFileItemVOAuthzStrategy(user, target, contextObjects);
    }

    @Override
    public EasyFileItemVOAuthzStrategy sameStrategy(Object target) {
        EasyFileItemVOAuthzStrategy sameStrategy = new EasyFileItemVOAuthzStrategy(target);
        super.clone(sameStrategy);

        sameStrategy.checkAttributes();
        return sameStrategy;
    }

}
