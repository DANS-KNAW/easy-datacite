package nl.knaw.dans.easy.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import nl.knaw.dans.easy.business.bean.SystemStatus;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class CodedAuthzTest
{
    @BeforeClass
    public static void init()
    {
        SystemStatus.INSTANCE.setFile(new File("target/SystemStatus.properties"));
    }

    @Test
    public void testGetSecurityOfficer()
    {
        Authz authz = new CodedAuthz();
        SecurityOfficer na = authz.getSecurityOfficer("foo");
        assertFalse(na.isComponentVisible(null));
        assertFalse(na.isEnableAllowed(null));
        assertEquals("(" + CodedAuthz.NO_SIGNATURE_OFFICER_PROPOSITION + " AND [read only mode is false])", na.getProposition());
    }

    @Test
    public void testGetItem()
    {
        String item = "nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:republish";
        Authz authz = new CodedAuthz();
        assertTrue(authz.hasSecurityOfficer(item));
        assertEquals(
                "(([SessionUser has role ARCHIVIST] AND [Dataset state is MAINTENANCE] AND [Required steps of workflow are completed]) AND [read only mode is false])",
                authz.getSecurityOfficer(item).getProposition());
    }

    @Ignore("Lists the rules. This is not a test.")
    @Test
    public void listRules() throws IOException
    {
        String dirname = "doc/rules/";
        File dir = new File(dirname);
        dir.mkdirs();
        String filename = dirname + "rules.csv";
        File file = new File(filename);
        if (file.exists())
        {
            file.delete();
        }

        CodedAuthz authz = new CodedAuthz();
        RandomAccessFile ram = new RandomAccessFile(filename, "rw");
        ram.writeBytes("Rules;" + new DateTime().toString("yyyy-MM-dd HH:mm") + "\n");
        ram.writeBytes(";\n");
        ram.writeBytes("ITEM;PROPOSITION\n");
        Map<String, SecurityOfficer> rules = authz.getRules();
        for (String item : rules.keySet())
        {
            ram.writeBytes(item + ";" + rules.get(item).getProposition() + "\n");
        }
        ram.close();
    }

}
