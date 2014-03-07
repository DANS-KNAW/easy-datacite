package nl.knaw.dans.common.fedora.store;

import nl.knaw.dans.common.fedora.rdf.FedoraURIReference;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.Test;

public class FedoraDmoStoreTest
{

    @Test
    public void printQuery()
    {
        if (Tester.isVerbose())
        {
            System.out.println(FedoraDmoStore.createSubordinateQuery(FedoraURIReference.create("easy-dataset:4000")));
            System.out.println(FedoraDmoStore.createJumpoffQuery("bla"));
        }
    }

}
