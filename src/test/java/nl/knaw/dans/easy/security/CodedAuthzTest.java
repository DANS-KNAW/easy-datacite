package nl.knaw.dans.easy.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

public class CodedAuthzTest
{
    @Test
    public void testGetSecurityOfficer()
    {
        Authz authz = createCodedAuthz();
        SecurityOfficer na = authz.getSecurityOfficer("foo");
        assertFalse(na.isComponentVisible(null));
        assertFalse(na.isEnableAllowed(null));
        assertEquals("(" + CodedAuthz.NO_SIGNATURE_OFFICER_PROPOSITION + " AND [read only mode is false])", na.getProposition());
    }

    private CodedAuthz createCodedAuthz()
    {
        CodedAuthz codedAuthz = new CodedAuthz();
        SystemReadOnlyStatus systemReadonlyStatus = new SystemReadOnlyStatus();
        systemReadonlyStatus.setFile(new File("target/SystemReadOnlyStatus.properties"));
        codedAuthz.setSystemReadonlyStatus(systemReadonlyStatus);
        return codedAuthz;
    }

    @Test
    public void testGetItem()
    {
        String item = "nl.knaw.dans.easy.web.view.dataset.DatasetViewPage:infosegmentPanel:statusPanel:republish";
        Authz authz = createCodedAuthz();
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

        CodedAuthz authz = createCodedAuthz();
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
