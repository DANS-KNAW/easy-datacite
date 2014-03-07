package nl.knaw.dans.common.lang;

import java.util.HashMap;
import java.util.Map;

public class ALiasDelegate<T>
{
    private final Map<String, AliasInterface<T>> nameToAlias;
    private final Map<Class<? extends T>, AliasInterface<T>> classToAlias;

    public static class InitializationException extends RuntimeException
    {
        private static final long serialVersionUID = 2651739582149783191L;

        /** An exception that anyone can catch, but only the owner can throw */
        private InitializationException(final String message)
        {
            super(message);
        }
    }

    public static interface AliasInterface<T>
    {
        /**
         * @return a string such that {@link ALiasDelegate#valueOf(Class)} returns this.
         */
        Class<? extends T> getAliasClass();

        /**
         * @return a string such that {@link ALiasDelegate#valueOfAlias(String)} returns this.
         */
        String getAlias();
    }

    public ALiasDelegate(final AliasInterface<T>[] values)
    {
        classToAlias = new HashMap<Class<? extends T>, AliasInterface<T>>();
        nameToAlias = new HashMap<String, AliasInterface<T>>();

        for (final AliasInterface<T> value : values)
        {
            if (classToAlias.put(value.getAliasClass(), value) != null)
                throw new InitializationException("duplicate class: " + value.getAliasClass().getName());
            if (nameToAlias.put(value.getAlias(), value) != null)
                throw new InitializationException("duplicate name: " + value.getAlias());
        }
    }

    /**
     * @return an {@link AliasInterface} such that {@link AliasInterface#getAliasClass()}.equals(aliasClass)
     */
    public AliasInterface<T> valueOf(final Class<? extends T> aliasClass)
    {
        if (aliasClass == null)
            throw new NullPointerException("null argument");
        if (!classToAlias.containsKey(aliasClass))
            throw new IllegalArgumentException("unknown class " + aliasClass.getName());
        return classToAlias.get(aliasClass);
    }

    /**
     * @return an {@link AliasInterface} such that {@link AliasInterface#getAlias()}.equals(alias)
     */
    public AliasInterface<T> valueOfAlias(final String alias)
    {
        if (alias == null)
            throw new NullPointerException("null argument");
        if (!nameToAlias.containsKey(alias))
            throw new IllegalArgumentException("unknown alias: " + alias);
        return nameToAlias.get(alias);
    }
}
