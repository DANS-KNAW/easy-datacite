package nl.knaw.dans.easy.domain.deposit.discipline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;

public class RecursiveList extends AbstractJiBXObject<RecursiveList> implements RecursiveNode, Serializable
{
    
    public static final String LID_ARCHAEOLOGY_DC_SUBJECT = "archaeology.dc.subject";
    public static final String LID_ARCHAEOLOGY_DCTERMS_TEMPORAL = "archaeology.dcterms.temporal";

    private static final long serialVersionUID = 3490879024839204765L;
    
    private String listId;
    private List<RecursiveEntry> recursiveEntries = new ArrayList<RecursiveEntry>();
    
    
    @SuppressWarnings("unused")
    private RecursiveList()
    {
        // used by JiBX
    }
    
    protected RecursiveList(String listId)
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

    public List<RecursiveEntry> getChildren()
    {
        return recursiveEntries;
    }

    public void setChildren(List<RecursiveEntry> recursiveEntries)
    {
        this.recursiveEntries = recursiveEntries;
    }
    
    public void add(RecursiveEntry re)
    {
        if (re == null)
        {
            throw new NullPointerException("A Child of a RecursiveList cannot be null");
        }
        recursiveEntries.add(re);
    }
    
    public RecursiveEntry getEntry(String key)
    {
        RecursiveEntry entry = null;
        Iterator<RecursiveEntry> iter = recursiveEntries.iterator();
        while (iter.hasNext() && entry == null)
        {
            RecursiveEntry kid = iter.next();
            entry = kid.get(key);
        }
        return entry;
    }

}
