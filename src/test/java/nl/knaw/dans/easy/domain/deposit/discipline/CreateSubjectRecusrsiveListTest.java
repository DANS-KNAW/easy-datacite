package nl.knaw.dans.easy.domain.deposit.discipline;

import java.util.List;

import nl.knaw.dans.common.lang.test.ClassPathHacker;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class CreateSubjectRecusrsiveListTest
{
    
    private static final String A_SUBJECT = "archaeology.dc.subject";
    
    private static final String[][] R_SUBJECT = { {"DEPO", "D"}, {"EX", "E"}, {"GX", "G"}, {"IX", "I"}, {"NX", "N"}, {"RX", "R"}, {"VX", "V"}, {"XXX", "X"}};
    
    
    @BeforeClass
    public static void beforeClass()
    {
        ClassPathHacker.addFile("../easy-webui/src/main/resources");
    }
    
    @Ignore
    @Test
    public void create() throws Exception
    {
        create(A_SUBJECT, R_SUBJECT);
    }
    
    public void create(String listId, String[][] roots) throws Exception
    {
        RecursiveList rl = new RecursiveList(listId);
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
                rl.add(new RecursiveEntry(key, shortname, name, rootOrdinal));
            }
            else
            {
                String rootKey = getRootKey(kvp.getKey(), roots);
                RecursiveEntry root = rl.getEntry(rootKey);
                int ordinal = root.getOrdinal() + 10;
                for (RecursiveEntry entry : root.getChildren())
                {
                    if (entry.getOrdinal() >= ordinal)
                    {
                        ordinal = entry.getOrdinal() + 10;
                    }
                }
                root.add(new RecursiveEntry(key, shortname, name, ordinal));
            }
        }
        System.out.println(rl.asXMLString(4));
    }

    private String getRootKey(String key, String[][] roots)
    {
        String k = key.substring(0, 1);
        for (String[] root : roots)
        {
            if (root[1].equals(k))
            {
                return root[0];
            }
        }
        System.err.println(key);
        return null;
    }

    private String getShortname(KeyValuePair kvp, String[][] roots)
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
        int index = value.indexOf(",");
        if (index > -1)
        {
            return value.substring(0, index);
        }
        else
        {
            return value;
        }
    }

    private String getEntryShortname(String value)
    {
        int index = value.indexOf(" - ");
        return value.substring(index + 3, value.length());
    }

    private boolean isRoot(KeyValuePair kvp, String[][] roots)
    {
        String key = kvp.getKey();
        for (String[] root : roots)
        {
            if (key.equals(root[0]))
            {
                return true;
            }
        }
        return false;
    }

}
