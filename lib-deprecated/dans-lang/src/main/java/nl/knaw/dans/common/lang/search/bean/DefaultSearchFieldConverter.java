package nl.knaw.dans.common.lang.search.bean;

/**
 * NOTE: this object is not used, since it does nothing. It is kept as a default
 * value for the converter searchfield and copyfield annotation.
 * @author lobo
 */
public class DefaultSearchFieldConverter implements SearchFieldConverter<Object>
{
    public Object fromFieldValue(Object in)
    {
        return in;
    }

    public Object toFieldValue(Object in)
    {
        return in;
    }
}
