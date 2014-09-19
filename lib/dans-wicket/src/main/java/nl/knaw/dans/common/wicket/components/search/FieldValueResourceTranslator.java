package nl.knaw.dans.common.wicket.components.search;

/**
 * Translates a search field value according to a property that starts with fieldvalue. and ends with the original value.
 */
public class FieldValueResourceTranslator extends ResourceTranslator {
    private static final long serialVersionUID = -5288594668340132408L;

    public FieldValueResourceTranslator() {
        super("fieldvalue.");
    }
}
