package nl.knaw.dans.common.lang.search.simple;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.FieldSet;

public class SimpleFieldSet<T> extends AbstractSet<Field<T>> implements FieldSet<T>
{
    private static final long serialVersionUID = 2297645031181185382L;

    private Map<String, Field<T>> map = new HashMap<String, Field<T>>();

    @Override
    public Iterator<Field<T>> iterator()
    {
        return map.values().iterator();
    }

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return map.containsValue(o);
    }

    @Override
    public boolean remove(Object o)
    {
        if (!(o instanceof Field))
            return false;
        return map.remove(((Field<?>) o).getName()) != null;
    }

    @Override
    public void clear()
    {
        map.clear();
    }

    @Override
    public boolean add(Field<T> o)
    {
        return map.put(o.getName(), o) == null;
    }

    public Field<T> getByFieldName(String name)
    {
        return map.get(name);
    }
}
