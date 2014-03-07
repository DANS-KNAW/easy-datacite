package nl.knaw.dans.common.lang.search.simple;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.search.Field;

/**
 * Combines optional fieldnames for a single value
 * When searching with fieldnames name1, name2 for value1 we
 * want the query equivalent: (name1=value1 OR name2=value1)
 *
 * @author paulboon
 *
 * @param <T>
 */
public class CombinedOptionalField<T> implements Field<T>
{
    private static final long serialVersionUID = -141468935807946059L;
    private T value;
    private List<String> names = new ArrayList<String>();

    public CombinedOptionalField(List<String> names)
    {
        this.names = names;
    }

    public CombinedOptionalField(List<String> names, T value)
    {
        this.names = names;
        this.value = value;
    }

    public List<String> getNames()
    {
        return names;
    }

    public void setNames(List<String> names)
    {
        this.names = names;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    // behave like a 'normal' Field and use the first element of the list
    public void setName(String name)
    {
        if (names.isEmpty())
            names.add(0, name);
        else
            names.set(0, name);
    }

    // behave like a 'normal' Field and use the first element of the list
    public String getName()
    {
        return names.get(0);
    }

    @Override
    public String toString()
    {
        StringBuilder sbNames = new StringBuilder();
        for (String name : names)
        {
            if (sbNames.length() > 0)
                sbNames.append(", ");
            sbNames.append(name);
        }

        return super.toString() + "[names = '" + sbNames.toString() + "' value = '" + getValue().toString() + "']";
    }

}
