package nl.knaw.dans.common.jibx.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;
import nl.knaw.dans.common.lang.repo.bean.RecursiveEntry;
import nl.knaw.dans.common.lang.repo.bean.RecursiveList;

public class JiBXRecursiveList extends AbstractJiBXObject<JiBXRecursiveList> implements Serializable, RecursiveList
{

    private static final long serialVersionUID = 3490879024839204765L;

    private String listId;
    private List<RecursiveEntry> recursiveEntries = new ArrayList<RecursiveEntry>();

    @SuppressWarnings("unused")
    private JiBXRecursiveList()
    {
        // used by JiBX
    }

    public JiBXRecursiveList(String listId)
    {
        this.listId = listId;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveList#getListId()
     */
    @Override
    public String getListId()
    {
        return listId;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveList#setListId(java.lang.String)
     */
    @Override
    public void setListId(String listId)
    {
        this.listId = listId;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveList#getChildren()
     */
    @Override
    public List<RecursiveEntry> getChildren()
    {
        return recursiveEntries;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveList#setChildren(java.util.List)
     */
    @Override
    public void setChildren(List<RecursiveEntry> recursiveEntries)
    {
        this.recursiveEntries = recursiveEntries;
    }

    /*
     * (non-Javadoc)
     * @see
     * nl.knaw.dans.common.jibx.bean.RecursiveList#add(nl.knaw.dans.common.jibx.bean.JiBXRecursiveEntry)
     */
    @Override
    public void add(RecursiveEntry re)
    {
        if (re == null)
        {
            throw new NullPointerException("A Child of a RecursiveList cannot be null");
        }
        recursiveEntries.add(re);
    }

    @Override
    public RecursiveEntry get(String key)
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
