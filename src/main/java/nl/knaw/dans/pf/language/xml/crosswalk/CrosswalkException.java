package nl.knaw.dans.pf.language.xml.crosswalk;

public class CrosswalkException extends Exception
{
    private static final long serialVersionUID = 1L;

    public CrosswalkException(String string, Throwable e)
    {
        super(string, e);
    }

    public CrosswalkException(String string)
    {
        super(string);
    }
}
