package nl.knaw.dans.common.wicket;

import java.util.MissingResourceException;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * Renders one choice of an enum object. The 'id' values used for internal representation is the
 * name of the enum instance. This value is also (part of) the key to look up the 'display value' in
 * the resource. The 'display value' is the first non null value of the following list:
 * <ul>
 * <li>resourceProvider.getString(&lt;resourceId>.&lt;enum.value></li>
 * <li>resourceProvider.getString(&lt;enum.value>)</li>
 * <li>&lt;enum.value></li>
 * </ul>
 * example: <br>
 * <br>
 * Container.java
 * 
 * <pre>
 *   public class Container extends ...
 *   {
 *     class enum Foo {bar};
 *     ...() {
 *         final IChoiceRenderer renderer = new EnumChoiceRenderer(this,"qualifier");
 *         final List<String> choiceList = Arrays.asList(Foo.values());
 *         add(new DropDownChoice("selectFoo",model,choiceList,renderer));
 *     }
 *   }
 * </pre>
 * 
 * Container.properties
 * 
 * <pre>
 *     qualifier.null=-Choose one-
 *     qualifier.bar=Bar
 * </pre>
 * 
 * Container.html
 * 
 * <pre>
 *     &lt;select wicket:id="selectFoo">&lt;/select>
 * </pre>
 * 
 * generated html:
 * 
 * <pre>
 *     &lt;select name="...:selectFoo" wicket:id="selectFoo">&lt;/select>
 *     &lt;option value="" selected="selected">-Choose one-&lt;/option>
 *     &lt;option value="bar">Bar&lt;/option>
 *     &lt;/select>
 * </pre>
 * 
 * adapted from http://techblog.molindo.at/2008/01/wicket-choicerenderer-for-enums.html by Michael
 * Sparer
 */
public class EnumChoiceRenderer<T extends Enum<T>> implements IChoiceRenderer<T>
{
    private static final long serialVersionUID = 1L;

    private final Component resourceProvider;

    private final String qualifier;

    /**
     * @param resourceProvider resource provider for the display value of an enum instance.
     * @param qualifier optional qualification to look up the display value in the resourceProvider.
     *            This allows to use the same list (or overlapping lists) of enum values for
     *            different components, such as a filter and an update value.
     * 
     */
    public EnumChoiceRenderer(final Component resourceProvider, final String qualifier)
    {
        this.resourceProvider = resourceProvider;
        this.qualifier = qualifier;
    }

    public String getDisplayValue(final T enumConst)
    {
        final String key = enumConst == null ? "null" : enumConst.name();
        if (qualifier != null)
        {
            try
            {
                return resourceProvider.getString(qualifier + '.' + key);
            }

            catch (MissingResourceException exception)
            {
            }
        }
        try
        {
            return resourceProvider.getString(key);
        }
        catch (MissingResourceException exception)
        {
        }
        return key;

    }

    public String getIdValue(final T enumConst, final int index)
    {
        return enumConst == null ? null : enumConst.name();
    }

}
