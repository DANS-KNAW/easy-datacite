package nl.knaw.dans.easy.web.deposit.repeater;

import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

/**
 * A default implementation of a {@link ListWrapper}, providing methods to obtain and handle instances of type T, with a
 * wrapped list containing type S.
 * 
 * @author ecco Mar 31, 2009
 * @param <T>
 *        the type handled by this ListWrapper
 * @param <S>
 *        the type contained in the wrapped list
 */
public abstract class AbstractDefaultListWrapper<T extends Object, S extends Object> extends AbstractListWrapper<T>
{

    private static final long serialVersionUID = 8139060385867927367L;

    private final List<S>     wrappedList;
    
    private String schemeName;
    private String schemeId;

    /**
     * Constructor.
     * 
     * @param wrappedList
     *        the wrapped list
     */
    public AbstractDefaultListWrapper(List<S> wrappedList)
    {
        this.wrappedList = wrappedList;
    }
    
    public AbstractDefaultListWrapper(List<S> wrappedList, String schemeName, String schemeId)
    {
        this.wrappedList = wrappedList;
        this.schemeName = schemeName;
        this.schemeId = schemeId;
    }
    
    /**
     * Returns <code>null</code>. Default implementation in case we don't need a ChoiceRenderer.
     */
    public ChoiceRenderer getChoiceRenderer()
    {
        return null;
    }

    /**
     * Returns <code>null</code>. Default implementation in case we don't need an empty value for type T.
     */
    public T getEmptyValue()
    {
        return null;
    }

    /**
     * Get the wrapped list.
     * 
     * @return the wrapped list
     */
    protected List<S> getWrappedList()
    {
        return wrappedList;
    }
    
    public String getSchemeName()
    {
        return schemeName;
    }

    public void setSchemeName(String schemeName)
    {
        this.schemeName = schemeName;
    }

    public String getSchemeId()
    {
        return schemeId;
    }

    public void setSchemeId(String schemeId)
    {
        this.schemeId = schemeId;
    }

    public static boolean isSame(String s1, String s2)
    {
        boolean same = false;
        if (s1 == null && s2 == null)
        {
            same = true;
        }
        if (s1 != null && s1.equals(s2))
        {
            same = true;
        }
        return same;
    }

}
