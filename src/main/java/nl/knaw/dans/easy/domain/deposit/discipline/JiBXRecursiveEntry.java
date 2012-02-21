package nl.knaw.dans.easy.domain.deposit.discipline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;

public class JiBXRecursiveEntry extends AbstractJiBXObject<JiBXRecursiveEntry> implements RecursiveNode, Serializable, Comparable<JiBXRecursiveEntry>
{

    private static final long serialVersionUID = -1709403472957310519L;
    
    private String key;
    private String shortname;
    private String name;
    private int ordinal;
    
    private List<JiBXRecursiveEntry> children = new ArrayList<JiBXRecursiveEntry>();
    
    public JiBXRecursiveEntry()
    {
        
    }
    
    public JiBXRecursiveEntry(String key, String shortname, String name, int ordinal)
    {
        this.key = key;
        this.shortname = shortname;
        this.name = name;
        this.ordinal = ordinal;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getShortname()
    {
        return shortname;
    }

    public void setShortname(String shortname)
    {
        this.shortname = shortname;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getOrdinal()
    {
        return ordinal;
    }

    public void setOrdinal(int ordinal)
    {
        this.ordinal = ordinal;
    }

    public List<JiBXRecursiveEntry> getChildren()
    {
        return children;
    }

    public void setChildren(List<JiBXRecursiveEntry> children)
    {
        this.children = children;
    }
    
    public void add(JiBXRecursiveEntry re)
    {
        if (re == null)
        {
            throw new NullPointerException("A Child of a RecursiveEntry cannot be null");
        }
        if (this == re)
        {
            throw new IllegalArgumentException("An RecursiveEntry cannot be added to itself!");
        }
        children.add(re);
    }
    
    public JiBXRecursiveEntry get(String key)
    {
        JiBXRecursiveEntry entry = null;
        if (getKey().equals(key))
        {
            entry = this;
        }
        else
        {
            Iterator<JiBXRecursiveEntry> iter = children.iterator();
            while (iter.hasNext() && entry == null)
            {
                JiBXRecursiveEntry kid = iter.next();
                entry = kid.get(key);
            }
        }
        return entry;
    }

    @Override
    public int compareTo(JiBXRecursiveEntry re)
    {
        if (re == null)
        {
            return 1;
        }
        else
        {
            return ordinal - re.ordinal;
        }
    }


}
