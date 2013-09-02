package nl.knaw.dans.common.wicket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;

/**
 * Example usage in the wicket application init() With the effect that every string resource not for
 * Locale.ENGLISH will be surrounded by {} This us useful for testing if strings are loaded on a Locale
 * settings change for the application while having the 'normal' strings for a 'standard' locale
 * 
 * <pre>
 * List&lt;IStringResourceLoader&gt; rloaders = getResourceSettings().getStringResourceLoaders();
 * getResourceSettings().addStringResourceLoader(new DebugStringResourceLoader(rloaders, &quot;{&quot;, &quot;}&quot;, Locale.ENGLISH));
 * </pre>
 * 
 * @author paulboon
 */
public class DebugStringResourceLoader implements IStringResourceLoader
{
    private List<IStringResourceLoader> rloaders;
    private String debugPostfix;
    private String debugPrefix;
    private Locale debugExcludeLocale;

    public DebugStringResourceLoader(List<IStringResourceLoader> replacedLoaders, String debugPrefix, String debugPostfix, Locale debugLocale)
    {
        // Be sure to copy the list to prevent endless recursion:
        // this loader is added and calls all loaders in the list
        // which would then include itself!
        //
        // Another solution would be to skip 'ourselfes' when iterating over the loaders
        this.rloaders = new ArrayList<IStringResourceLoader>(replacedLoaders);
        this.debugPrefix = debugPrefix;
        this.debugPostfix = debugPostfix;
        this.debugExcludeLocale = debugLocale;
    }

    public DebugStringResourceLoader(List<IStringResourceLoader> replacedLoaders, String debugPrefix, String debugPostfix)
    {
        this.rloaders = new ArrayList<IStringResourceLoader>(replacedLoaders);
        this.debugPrefix = debugPrefix;
        this.debugPostfix = debugPostfix;
    }

    public String loadStringResource(Component component, String key)
    {
        Iterator<IStringResourceLoader> iter = rloaders.iterator();
        while (iter.hasNext())
        {
            IStringResourceLoader loader = (IStringResourceLoader) iter.next();
            String string = loader.loadStringResource(component, key);
            if (string != null)
            {
                return filterStringResource(component.getLocale(), string);
            }
        }
        return null;
    }

    public String loadStringResource(Class<?> clazz, String key, Locale locale, String style)
    {
        Iterator<IStringResourceLoader> iter = rloaders.iterator();
        while (iter.hasNext())
        {
            IStringResourceLoader loader = (IStringResourceLoader) iter.next();
            String string = loader.loadStringResource(clazz, key, locale, style);
            if (string != null)
            {
                return filterStringResource(locale, string);
            }
        }
        return null;
    }

    private String filterStringResource(Locale locale, String string)
    {
        String result = null;

        if (string != null)
        {
            if (debugExcludeLocale != null && debugExcludeLocale.equals(locale))
                result = string;
            else
                result = debugPrefix + string + debugPostfix;
        }

        return result;
    }
}
