package nl.knaw.dans.common.lang.search.simple;

import java.util.List;

import nl.knaw.dans.common.lang.search.SnippetField;

public class SimpleSnippetField extends SimpleField<List<String>> implements SnippetField {
    private static final long serialVersionUID = 119742473860268656L;

    public SimpleSnippetField(String name, List<String> value) {
        super(name, value);
    }
}
