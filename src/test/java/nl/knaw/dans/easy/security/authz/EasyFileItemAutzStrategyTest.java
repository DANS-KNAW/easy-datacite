package nl.knaw.dans.easy.security.authz;

import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;

import org.junit.Test;

public class EasyFileItemAutzStrategyTest
{
    
    @Test (expected = IllegalArgumentException.class)
    public void constructor1()
    {
        EasyFileItemAuthzStrategy strategy = new EasyFileItemAuthzStrategy();
        strategy.checkAttributes();
    }
    
    @Test
    public void timeTest()
    {
        EasyUser user = getAnonymousUser();
        Dataset dataset = new DatasetImpl("foo:dataset");
        FileItem fileItem = new FileItemImpl("foo:fileItem");
        fileItem.setDatasetId(dataset.getDmoStoreId());
        
        EasyFileItemAuthzStrategy strategy = new EasyFileItemAuthzStrategy(user, fileItem, dataset);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++)
        {
            EasyFileItemAuthzStrategy strategy2 = strategy.sameStrategy(fileItem);
            strategy2.canChildrenBeDiscovered();
            strategy2.canChildrenBeRead();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    private EasyUser getAnonymousUser()
    {
        return EasyUserAnonymous.getInstance();
    }

}
