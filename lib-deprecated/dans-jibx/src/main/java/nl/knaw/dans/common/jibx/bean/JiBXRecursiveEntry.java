package nl.knaw.dans.common.jibx.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;
import nl.knaw.dans.common.lang.repo.bean.RecursiveEntry;

public class JiBXRecursiveEntry extends AbstractJiBXObject<JiBXRecursiveEntry> implements RecursiveEntry {

    private static final long serialVersionUID = -1709403472957310519L;

    private String key;
    private String shortname;
    private String name;
    private int ordinal;

    private List<RecursiveEntry> children = new ArrayList<RecursiveEntry>();

    public JiBXRecursiveEntry() {

    }

    public JiBXRecursiveEntry(String key, String shortname, String name, int ordinal) {
        this.key = key;
        this.shortname = shortname;
        this.name = name;
        this.ordinal = ordinal;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#getKey()
     */
    @Override
    public String getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#setKey(java.lang.String)
     */
    @Override
    public void setKey(String key) {
        this.key = key;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#getShortname()
     */
    @Override
    public String getShortname() {
        return shortname;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#setShortname(java.lang.String)
     */
    @Override
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#getOrdinal()
     */
    @Override
    public int getOrdinal() {
        return ordinal;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#setOrdinal(int)
     */
    @Override
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#getChildren()
     */
    @Override
    public List<RecursiveEntry> getChildren() {
        return children;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#setChildren(java.util.List)
     */
    @Override
    public void setChildren(List<RecursiveEntry> children) {
        this.children = children;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#add(nl.knaw.dans.common.jibx.bean.JiBXRecursiveEntry)
     */
    @Override
    public void add(RecursiveEntry re) {
        if (re == null) {
            throw new NullPointerException("A Child of a RecursiveEntry cannot be null");
        }
        if (this == re) {
            throw new IllegalArgumentException("An RecursiveEntry cannot be added to itself!");
        }
        children.add(re);
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#get(java.lang.String)
     */
    @Override
    public RecursiveEntry get(String key) {
        RecursiveEntry entry = null;
        if (getKey().equals(key)) {
            entry = this;
        } else {
            Iterator<RecursiveEntry> iter = children.iterator();
            while (iter.hasNext() && entry == null) {
                RecursiveEntry kid = iter.next();
                entry = kid.get(key);
            }
        }
        return entry;
    }

    /*
     * (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.RecursiveEntry#compareTo(nl.knaw.dans.common.jibx.bean. JiBXRecursiveEntry)
     */
    @Override
    public int compareTo(RecursiveEntry re) {
        if (re == null) {
            return 1;
        } else {
            return ordinal - re.getOrdinal();
        }
    }

}
