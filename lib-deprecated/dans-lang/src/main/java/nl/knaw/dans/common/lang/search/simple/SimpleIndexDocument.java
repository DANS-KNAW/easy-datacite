package nl.knaw.dans.common.lang.search.simple;

import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.Index;
import nl.knaw.dans.common.lang.search.IndexDocument;

public class SimpleIndexDocument extends SimpleDocument implements IndexDocument
{
    private static final long serialVersionUID = -3562470874746650080L;

    private Index index;

    public SimpleIndexDocument(Index index)
    {
        setIndex(index);
    }

    public SimpleIndexDocument(IndexDocument doc)
    {
        super(doc);
        setIndex(doc.getIndex());
    }

    public Index getIndex()
    {
        return index;
    }

    public void setIndex(Index index)
    {
        this.index = index;
    }

    public Field getPrimaryKey()
    {
        return getFieldByName(index.getPrimaryKey());
    }

}
