package nl.knaw.dans.common.wicket.components.search;

import java.io.Serializable;
import java.util.Locale;

import org.apache.wicket.model.IModel;

/**
 * Simple interface to translate an original value according to a locale. A translator model might also
 * do the trick and may be more elegant, but I've already done it like this..
 * 
 * @author lobo
 * @param <T>
 *        the type of the original value
 */
public interface Translator<T> extends Serializable
{
    IModel<String> getTranslation(T originalValue, Locale locale, boolean fullName);
}
