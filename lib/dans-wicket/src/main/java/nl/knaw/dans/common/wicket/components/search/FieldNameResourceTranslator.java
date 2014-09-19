package nl.knaw.dans.common.wicket.components.search;

/**
 * Translates a search field name according to a property that starts with fieldname. and ends with the original value.
 */
public class FieldNameResourceTranslator extends ResourceTranslator {
    private static final long serialVersionUID = -5288594668340132408L;

    public FieldNameResourceTranslator() {
        super("fieldname.");
    }
}
