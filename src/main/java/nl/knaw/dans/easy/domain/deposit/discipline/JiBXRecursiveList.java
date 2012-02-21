package nl.knaw.dans.easy.domain.deposit.discipline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;

public class JiBXRecursiveList extends AbstractJiBXObject<JiBXRecursiveList> implements RecursiveNode, Serializable
{
    
    private static final long serialVersionUID = 3490879024839204765L;
    
    private String listId;
    private List<JiBXRecursiveEntry> recursiveEntries = new ArrayList<JiBXRecursiveEntry>();
    
    
    @SuppressWarnings("unused")
    private JiBXRecursiveList()
    {
        // used by JiBX
    }
    
    protected JiBXRecursiveList(String listId)
    {
        this.listId = listId;
    }

    public String getListId()
    {
        return listId;
    }

    public void setListId(String listId)
    {
        this.listId = listId;
    }

    public List<JiBXRecursiveEntry> getChildren()
    {
        return recursiveEntries;
    }

    public void setChildren(List<JiBXRecursiveEntry> recursiveEntries)
    {
        this.recursiveEntries = recursiveEntries;
    }
    
    public void add(JiBXRecursiveEntry re)
    {
        if (re == null)
        {
            throw new NullPointerException("A Child of a RecursiveList cannot be null");
        }
        recursiveEntries.add(re);
    }
    
    public JiBXRecursiveEntry getEntry(String key)
    {
        JiBXRecursiveEntry entry = null;
        Iterator<JiBXRecursiveEntry> iter = recursiveEntries.iterator();
        while (iter.hasNext() && entry == null)
        {
            JiBXRecursiveEntry kid = iter.next();
            entry = kid.get(key);
        }
        return entry;
    }

}
