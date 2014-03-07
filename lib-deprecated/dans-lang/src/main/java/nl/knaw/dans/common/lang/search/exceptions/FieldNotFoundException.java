package nl.knaw.dans.common.lang.search.exceptions;

public class FieldNotFoundException extends SearchBeanException
{
    private static final long serialVersionUID = 5392194481401516759L;

    public FieldNotFoundException(String fieldName)
    {
        super("Could not find a field named " + fieldName);
    }

}
