package nl.knaw.dans.easy.security.authz;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.domain.dataset.EasyFile;
import nl.knaw.dans.easy.domain.model.FileItem;

public class EasyFileItemAuthzStrategy extends AbstractDatasetAutzStrategy
{
    
    private static final long serialVersionUID = 6619203194721259107L;
    private FileItem fileItem;        
    private int viewProfile = NOT_EVALUATED;
    private int readProfile = NOT_EVALUATED;
    
    protected EasyFileItemAuthzStrategy()
    {

    }
    
    protected EasyFileItemAuthzStrategy(User user, Object target, Object...contextObjects)
    {
        super(user, contextObjects);
        if (target instanceof FileItem)
        {
            fileItem = (FileItem) target;
        }
        checkAttributes();
        
    }
    
    protected EasyFileItemAuthzStrategy(Object target)
    {
        if (target instanceof FileItem)
        {
            fileItem = (FileItem) target;
        }
    }
    
    protected void checkAttributes()
    {
        super.checkAttributes();
        if (fileItem == null) throw new IllegalArgumentException("Insufficient parameters: no fileItem");
        if (!getDataset().getDmoStoreId().equals(fileItem.getDatasetId()))
            throw new IllegalArgumentException("FileItem is not part of given dataset");
        
//        if (!AUTHZ_STRATEGY_NAME.equals(fileItem.getAutzStrategyName()))
//            throw new IllegalArgumentException("AuthzStrategyName of fileItem is not " + AUTHZ_STRATEGY_NAME);
    }
    
    @Override
    protected int getResourceDiscoveryProfile()
    {
        if (viewProfile == NOT_EVALUATED)
        {
            viewProfile = AccessCategory.UTIL.getBitMask(fileItem.getViewAccessCategory());
        }
        return viewProfile;
    }
    
    @Override
    protected int getResourceReadProfile()
    {
        if (readProfile == NOT_EVALUATED)
        {
            readProfile = AccessCategory.UTIL.getBitMask(fileItem.getReadAccessCategory());
        }
        return readProfile;
    }
    
    @Override
    public boolean canUnitBeDiscovered(String unitId)
    {
        if (EasyFile.UNIT_ID.equals(unitId))
        {
            return canBeDiscovered() && profileMatches(getUserProfile(), getResourceDiscoveryProfile());
        }
        else
        {
            throw new UnsupportedOperationException("Method not implemented for unitId " + unitId);
        }
    }
    
    @Override
    public boolean canUnitBeRead(String unitId)
    {
        if (unitId == null || EasyFile.UNIT_ID.equals(unitId))
        {
            return canBeRead() && profileMatches(getUserProfile(), getResourceReadProfile());
        }
        else
        {
            throw new UnsupportedOperationException("Method not implemented for unitId " + unitId);
        }
    }
    
    @Override
    protected boolean canAllBeRead()
    {
        return canUnitBeRead(null);
    }
    
    @Override
    public TriState canChildrenBeDiscovered()
    {
        return TriState.NONE;
    }
    
    @Override
    public TriState canChildrenBeRead()
    {
        return TriState.NONE;
    }

    @Override
    public EasyFileItemAuthzStrategy newStrategy(User user, Object target, Object...contextObjects)
    {
        return new EasyFileItemAuthzStrategy(user, target, contextObjects);
    }
    
    @Override
    public EasyFileItemAuthzStrategy sameStrategy(Object target)
    {
        EasyFileItemAuthzStrategy sameStrategy = new EasyFileItemAuthzStrategy(target);
        super.clone(sameStrategy);
        
        sameStrategy.checkAttributes();
        return sameStrategy;
    }

}
