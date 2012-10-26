package nl.knaw.dans.easy.fedora.store;

import java.net.MalformedURLException;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollectionImpl;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;

import org.junit.BeforeClass;
import org.junit.Test;

public class DisciplineContainerOnlineTest extends AbstractOnlineTest
{

    @BeforeClass
    public static void beforeClass() throws RepositoryException, MalformedURLException
    {
        //ClassPathHacker.addFile("../easy-webui/src/main/resources");
        setUpData();
        //store = Data.getEasyStore();

    }

    @Test
    public void rootTest() throws Exception
    {
        DmoStoreId discRootId = new DmoStoreId(DisciplineCollectionImpl.EASY_DISCIPLINE_ROOT);
        EasyStore store = Data.getEasyStore();

        DisciplineContainer root = (DisciplineContainer) store.retrieve(discRootId);
        StringBuilder sb = new StringBuilder();
        print(root, 0, sb);

        System.out.println(sb.toString());
    }

    private void print(DisciplineContainer dc, int indent, StringBuilder sb) throws DomainException
    {
        sb.append("\n");
        for (int i = 0; i < indent; i++)
        {
            sb.append(" ");
        }
        sb.append(dc.getStoreId() + " " + dc.getDisciplineMetadata().getOICode() + " " + dc.getName());
        for (DisciplineContainer dcKid : dc.getSubDisciplines())
        {
            print(dcKid, indent + 4, sb);
        }
    }

}
