package nl.knaw.dans.common.fedora.store;

import java.util.List;

import nl.knaw.dans.common.fedora.Fedora;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class FedoraDmoStoreOnlineTest
{

    private static FedoraDmoStore STORE;

    @BeforeClass
    public static void beforeClass()
    {
        Fedora fedora = new Fedora(Tester.getString("fedora.base.url"), Tester.getString("fedora.admin.username"), Tester.getString("fedora.admin.userpass"));
        STORE = new FedoraDmoStore("test", fedora);//, null);
    }

    @Ignore
    @Test
    public void getUnitMetadata() throws Exception
    {
        List<UnitMetadata> umdList = STORE.getUnitMetadata(new DmoStoreId("easy-jumpoff:41"));
        for (UnitMetadata umd : umdList)
        {
            System.err.println(umd.getLabel() + " " + umd.getLocation());
        }
    }

}
