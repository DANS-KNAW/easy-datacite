package nl.knaw.dans.easy.security.authz;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.security.authz.AuthzMessage;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItemContainerMetadata;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.easymock.EasyMock;
import org.junit.Test;

public class EasyItemContainerAuthzStrategyTest
{
    
    @Test
    public void singleMessageTestYes()
    {
        Dataset dataset = EasyMock.createMock(Dataset.class);
        EasyUser user = EasyMock.createMock(EasyUser.class);
        DatasetItemContainerMetadata icmd = EasyMock.createMock(DatasetItemContainerMetadata.class);
        
        List<AccessCategory> accessCategories = new ArrayList<AccessCategory>();
        accessCategories.add(AccessCategory.ANONYMOUS_ACCESS);
        
        //constructor
        EasyMock.expect(dataset.getDmoStoreId()).andReturn(new DmoStoreId("dataset:1"));
        EasyMock.expect(dataset.getDatasetItemContainerMetadata()).andReturn(icmd).times(2);
        EasyMock.expect(icmd.getDatasetDmoStoreId()).andReturn(new DmoStoreId("dataset:1"));
        
        //isEnableAllowed?
        EasyMock.expect(dataset.getAdministrativeState()).andReturn(DatasetState.PUBLISHED);
        EasyMock.expect(dataset.isUnderEmbargo()).andReturn(false);
        
        //getUserProfile
        EasyMock.expect(user.isAnonymous()).andReturn(true);
        EasyMock.expect(user.isActive()).andReturn(true);
        
        //getResourceReadProfile
        EasyMock.expect(icmd.getChildAccessibility()).andReturn(accessCategories);
        
        EasyMock.replay(dataset, user, icmd);
        
        EasyItemContainerAuthzStrategy strategy = new EasyItemContainerAuthzStrategy(user, dataset, dataset);
        AuthzMessage message = strategy.getSingleReadMessage();
        assertEquals("dataset.authzstrategy.sm.yes", message.getMessageCode());
        
        EasyMock.verify(dataset, user, icmd);
    }
    
    @Test
    public void singleMessageTestLogin()
    {
        Dataset dataset = EasyMock.createMock(Dataset.class);
        EasyUser user = EasyMock.createMock(EasyUser.class);
        DatasetItemContainerMetadata icmd = EasyMock.createMock(DatasetItemContainerMetadata.class);
        
        List<AccessCategory> accessCategories = new ArrayList<AccessCategory>();
        accessCategories.add(AccessCategory.ANONYMOUS_ACCESS);
        accessCategories.add(AccessCategory.OPEN_ACCESS);
        
        //constructor
        EasyMock.expect(dataset.getDmoStoreId()).andReturn(new DmoStoreId("dataset:1"));
        EasyMock.expect(dataset.getDatasetItemContainerMetadata()).andReturn(icmd).times(2);
        EasyMock.expect(icmd.getDatasetDmoStoreId()).andReturn(new DmoStoreId("dataset:1"));
        
        //isEnableAllowed?
        EasyMock.expect(dataset.getAdministrativeState()).andReturn(DatasetState.PUBLISHED);
        EasyMock.expect(dataset.isUnderEmbargo()).andReturn(false);
        
        //getUserProfile
        EasyMock.expect(user.isAnonymous()).andReturn(true).times(2);
        EasyMock.expect(user.isActive()).andReturn(true);
        
        //getResourceReadProfile
        EasyMock.expect(icmd.getChildAccessibility()).andReturn(accessCategories);
        
        EasyMock.replay(dataset, user, icmd);
        
        EasyItemContainerAuthzStrategy strategy = new EasyItemContainerAuthzStrategy(user, dataset, dataset);
        AuthzMessage message = strategy.getSingleReadMessage();
        assertEquals("dataset.authzstrategy.sm.login", message.getMessageCode());
        
        EasyMock.verify(dataset, user, icmd);
    }

}
