package nl.knaw.dans.common.wicket.components.search;

import java.util.Locale;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * Translates a string using a property.
 * 
 * @author lobo
 */
public class ResourceTranslator implements Translator<String> {
    private static final long serialVersionUID = -5288594668340132408L;

    private String prefix = "";

    private String postfix = "";

    public ResourceTranslator() {}

    public ResourceTranslator(String prefix) {
        this.prefix = prefix;
    }

    public ResourceTranslator(String prefix, String postfix) {
        this.prefix = prefix;
        this.postfix = postfix;
    }

    public IModel<String> getTranslation(String originalValue, Locale locale, boolean fullName) {
        return new ResourceModel(prefix + originalValue + postfix);
    }

}
