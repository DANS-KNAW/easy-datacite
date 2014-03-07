package nl.knaw.dans.common.lang.util;

import java.io.Serializable;

/**
 * Represents a range of values, by specifying the start and end value Note that there is a
 * org.apache.commons.lang math.Range but it's not generic and only numeric versions are available TODO
 * add unit test
 */
public class Range<T extends Comparable> implements Serializable
{
    private static final long serialVersionUID = 5148605778347331989L;
    private T start = null;
    private T end = null;

    public Range(T start, T end)
    {
        this.start = start;
        this.end = end;
    }

    public T getStart()
    {
        return start;
    }

    public T getEnd()
    {
        return end;
    }

    /**
     * Inclusive range check Note: missing support for open ended (and empty) ranges also the end can be
     * smaller then the start we could add a "normalize" member to fix that
     */
    public boolean contains(final T value)
    {
        return (start.compareTo(value) <= 0 && end.compareTo(value) >= 0);
    }

    @Override
    public String toString()
    {
        return (start == null ? "" : start.toString()) + " - " + (end == null ? "" : end.toString());
    }
}
