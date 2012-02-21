package nl.knaw.dans.easy.domain.deposit.discipline;

import java.util.List;

import nl.knaw.dans.common.lang.test.ClassPathHacker;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class CreateTemporalRecursiveListTest
{
    
    private static final String A_TEMPORAL = "archaeology.dcterms.temporal";
    
    private static final String[]R_TEMPORAL = { "PALEO", "MESO", "NEO", "BRONS", "IJZ", "ROM", "XME", "NT", "XXX"};
    
    private static final String[] ME_KIDS = { "VME", "VMEA", "VMEB", "VMEC", "VMED", "LME", "LMEA", "LMEB" };
    
    @BeforeClass
    public static void beforeClass()
    {
        ClassPathHacker.addFile("../easy-webui/src/main/resources");
    }
    
    @Ignore
    @Test
    public void create() throws Exception
    {
        create(A_TEMPORAL, R_TEMPORAL);
    }
    
    public void create(String listId, String[] roots) throws Exception
    {
        JiBXRecursiveList rl = new JiBXRecursiveList(listId);
        ChoiceList subject = ChoiceListCache.getInstance().getList(listId);
        List<KeyValuePair> choices = subject.getChoices();
        int rootOrdinal = 0;
        for (KeyValuePair kvp : choices)
        {
            String key = kvp.getKey();
            String name = kvp.getValue();
            String shortname = getShortname(kvp, roots);
            if (isRoot(kvp, roots))
            {
                rootOrdinal += 1000;
                rl.add(new JiBXRecursiveEntry(key, shortname, name, rootOrdinal));
            }
            else
            {
                String rootKey = getRootKey(kvp.getKey(), roots);
                JiBXRecursiveEntry root = rl.getEntry(rootKey);
                int ordinal = root.getOrdinal() + 10;
                for (JiBXRecursiveEntry entry : root.getChildren())
                {
                    if (entry.getOrdinal() >= ordinal)
                    {
                        ordinal = entry.getOrdinal() + 10;
                    }
                }
                root.add(new JiBXRecursiveEntry(key, shortname, name, ordinal));
            }
        }
        System.out.println(rl.asXMLString(4));
    }

    private String getRootKey(String key, String[] roots)
    {
        for (String root : roots)
        {
            if (key.startsWith(root))
            {
                return root;
            }
        }
        
        for (String me : ME_KIDS)
        {
            if (key.equals(me))
            {
                return "XME";
            }
        }
        
        System.err.println(key);
        return null;
    }

    private String getShortname(KeyValuePair kvp, String[] roots)
    {
        String shortname;
        if (isRoot(kvp, roots))
        {
            shortname = getRootShortname(kvp.getValue());
        }
        else
        {
            shortname = getEntryShortname(kvp.getValue());
        }
        return shortname;
    }

    private String getRootShortname(String value)
    {
        return value;
    }

    private String getEntryShortname(String value)
    {
        return value;
    }

    private boolean isRoot(KeyValuePair kvp, String[] roots)
    {
        String key = kvp.getKey();
        for (String root : roots)
        {
            if (key.equals(root))
            {
                return true;
            }
        }
        return false;
    }

}
