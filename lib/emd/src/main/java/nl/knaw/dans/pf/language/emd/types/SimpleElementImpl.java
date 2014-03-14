package nl.knaw.dans.pf.language.emd.types;

/**
 * Basic implementation of the {@link SimpleElement}.
 * 
 * @author ecco
 * @param <T>
 *        the wrapped type
 */
public abstract class SimpleElementImpl<T> implements SimpleElement<T>, MetadataItem
{

    /**
     *
     */
    private static final long serialVersionUID = -2927383818237650985L;

    // ecco: CHECKSTYLE: OFF
    /**
     * The wrapped type.
     */
    protected T value;

    /**
     * The id of a list of choices used in a value or attribute.
     */
    protected String schemeId;

    // ecco: CHECKSTYLE: ON

    /**
     * {@inheritDoc}
     */
    public T getValue()
    {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(final T value)
    {
        this.value = value;
    }

    /**
     * @return the schemeId
     */
    public String getSchemeId()
    {
        return schemeId;
    }

    /**
     * @param schemeId
     *        the schemeId to set
     */
    public void setSchemeId(String schemeId)
    {
        this.schemeId = schemeId;
    }

    /**
     * Returns a string-representation of the value of this SimpleElement.
     * 
     * @return string-representation of the value
     */
    @Override
    public String toString()
    {
        return value == null ? "" : value.toString();
    }

}
