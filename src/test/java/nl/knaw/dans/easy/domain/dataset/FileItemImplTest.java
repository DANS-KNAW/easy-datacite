package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FileItem;

import org.junit.Test;

public class FileItemImplTest
{

    private boolean verbose = Tester.isVerbose();

    @Test
    public void dirtyChecking()
    {
        if (verbose)
            Tester.printClassAndFieldHierarchy(FileItemImpl.class);
        // fields affected by dirtyChecking:
        // label:java.lang.String
        // ownerId:java.lang.String
        // state:java.lang.String

        FileItemImpl fi = new FileItemImpl("dummy-file:1");
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
    
    @Test
    public void setFile() throws ResourceNotFoundException, IOException
    {
        FileItem fi = new FileItemImpl("dummy-file:1");
        File file = Tester.getFile("test-files/FileItemImpl/kubler.doc");
        fi.setFile(file);
        assertTrue(fi.isDirty());
        assertEquals("kubler.doc", fi.getLabel());
        assertEquals("application/vnd.ms-word", fi.getMimeType());
        assertEquals(111104, fi.getSize());
    }
    
    @Test
    public void isAccessibleFor()
    {
        FileItem fi = new FileItemImpl("dummy-file:1");
        fi.setAccessibleTo(AccessibleTo.ANONYMOUS);
        
        List<AccessCategory> categories = new ArrayList<AccessCategory>();
        
        int profile = AccessCategory.UTIL.getBitMask(categories);
        if (verbose)
            System.err.println(profile);
        assertFalse(fi.isAccessibleFor(profile));
        
        categories.add(AccessCategory.ANONYMOUS_ACCESS);
        profile = AccessCategory.UTIL.getBitMask(categories);
        if (verbose)
            System.err.println(profile);
        assertTrue(fi.isAccessibleFor(profile));
        
        
        fi.setAccessibleTo(null);
        assertFalse(fi.isAccessibleFor(profile));
    }
    
    @Test
    public void accessCategory()
    {
        List<AccessCategory> categories = new ArrayList<AccessCategory>();
        System.err.println(AccessCategory.UTIL.getBitMask(categories) + " " + categories);
        
        categories.add(AccessCategory.ANONYMOUS_ACCESS);
        System.err.println(AccessCategory.UTIL.getBitMask(categories) + " " + categories);
        
        categories.add(AccessCategory.OPEN_ACCESS);
        System.err.println(AccessCategory.UTIL.getBitMask(categories) + " " + categories);
    }
    
    @Test
    public void getAccessProfile()
    {
        FileItemImpl fi = new FileItemImpl("dummy-file:1");
        fi.setAccessibleTo(AccessibleTo.KNOWN);
        for (int i = 0; i < 128; i++)
        {
            System.err.println(i + " > " + fi.getAccessProfile(i));
        }
    }

}
