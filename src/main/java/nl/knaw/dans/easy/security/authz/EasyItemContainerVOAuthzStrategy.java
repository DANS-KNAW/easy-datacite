package nl.knaw.dans.easy.security.authz;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;

public class EasyItemContainerVOAuthzStrategy extends AbstractDatasetAutzStrategy
{

    private static final long serialVersionUID = -6233534115714650363L;
    private FolderItemVO folderItemVO;
    private int discoveryProfile = NOT_EVALUATED;
    private int readProfile = NOT_EVALUATED;
    
    protected EasyItemContainerVOAuthzStrategy()
    {
        
    }
    
    protected EasyItemContainerVOAuthzStrategy(User user, Object target, Object...contextObjects)
    {
        super(user, contextObjects);
        if (target instanceof FolderItemVO)
        {
            folderItemVO = (FolderItemVO) target;
        }
        checkAttributes();
    }
    
    protected EasyItemContainerVOAuthzStrategy(Object target)
    {
        if (target instanceof FolderItemVO)
        {
            folderItemVO = (FolderItemVO) target;
        }
    }
    
    @Override
    protected void checkAttributes()
    {
        super.checkAttributes();
        if (folderItemVO == null) throw new IllegalArgumentException("Insufficient parameters: no folderItemVO");
        String datasetId = getDataset().getStoreId();
        if (!(datasetId.equals(folderItemVO.getDatasetSid())
              || datasetId.equals(folderItemVO.getSid())))
            throw new IllegalArgumentException("FolderItemVO is not given dataset, nor part of given dataset");
    }


    @Override
    protected int getResourceDiscoveryProfile()
    {
        if (discoveryProfile == NOT_EVALUATED)
        {
            discoveryProfile = AccessCategory.UTIL.getBitMask(folderItemVO.getChildVisibility());
        }
        return discoveryProfile;
    }

    @Override
    protected int getResourceReadProfile()
    {
        if (readProfile == NOT_EVALUATED)
        {
            readProfile = AccessCategory.UTIL.getBitMask(folderItemVO.getChildAccessibility());
        }
        return readProfile;
    }
    
    @Override
    public boolean canUnitBeDiscovered(String unitId)
    {
        throw new UnsupportedOperationException("Method not implemented");
    }
    
    @Override
    public boolean canUnitBeRead(String unitId)
    {
        throw new UnsupportedOperationException("Method not implemented");
    }
    
    @Override
    protected boolean canAllBeRead()
    {
        return TriState.ALL.equals(canChildrenBeRead());
    }
    
    @Override
    public EasyItemContainerVOAuthzStrategy newStrategy(User user, Object target, Object... contextObjects)
    {
        return new EasyItemContainerVOAuthzStrategy(user, target, contextObjects);
    }

    @Override
    public EasyItemContainerVOAuthzStrategy sameStrategy(Object target)
    {
        EasyItemContainerVOAuthzStrategy sameStrategy = new EasyItemContainerVOAuthzStrategy(target);
        super.clone(sameStrategy);
        
        sameStrategy.checkAttributes();
        return sameStrategy;
    }

}
