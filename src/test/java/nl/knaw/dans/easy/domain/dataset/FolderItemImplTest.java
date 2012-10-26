package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.Test;

public class FolderItemImplTest
{

    private boolean verbose = Tester.isVerbose();

    @Test
    public void dirtyChecking()
    {
        if (verbose)
            Tester.printClassAndFieldHierarchy(FolderItemImpl.class);

        // fields affected by dirty checking:
        // label:java.lang.String
        // ownerId:java.lang.String
        // state:java.lang.String

        FolderItemImpl fi = new FolderItemImpl("dummy-folder:1");
        assertTrue(fi.isDirty());

        fi.setLabel("foo");
        assertTrue(fi.isDirty());
        fi.setDirty(false);
        assertFalse(fi.isDirty());

        fi.setOwnerId("bar");
        assertTrue(fi.isDirty());
        fi.setDirty(false);

        fi.setState("bla");
        assertTrue(fi.isDirty());
        fi.setDirty(false);
    }
}
