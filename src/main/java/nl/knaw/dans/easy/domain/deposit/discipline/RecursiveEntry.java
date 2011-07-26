package nl.knaw.dans.easy.domain.deposit.discipline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;

public class RecursiveEntry extends AbstractJiBXObject<RecursiveEntry> implements RecursiveNode, Serializable, Comparable<RecursiveEntry>
{

    private static final long serialVersionUID = -1709403472957310519L;
    
    private String key;
    private String shortname;
    private String name;
    private int ordinal;
    
    private List<RecursiveEntry> children = new ArrayList<RecursiveEntry>();
    
    public RecursiveEntry()
    {
        
    }
    
    public RecursiveEntry(String key, String shortname, String name, int ordinal)
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

    public List<RecursiveEntry> getChildren()
    {
        return children;
    }

    public void setChildren(List<RecursiveEntry> children)
    {
        this.children = children;
    }
    
    public void add(RecursiveEntry re)
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
    
    public RecursiveEntry get(String key)
    {
        RecursiveEntry entry = null;
        if (getKey().equals(key))
        {
            entry = this;
        }
        else
        {
            Iterator<RecursiveEntry> iter = children.iterator();
            while (iter.hasNext() && entry == null)
            {
                RecursiveEntry kid = iter.next();
                entry = kid.get(key);
            }
        }
        return entry;
    }

    @Override
    public int compareTo(RecursiveEntry re)
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
