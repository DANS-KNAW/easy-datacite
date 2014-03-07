package nl.knaw.dans.common.lang.search;

import java.util.List;

/**
 * A snippet field is a field with a string value. In the snippet value a part might be highlighted by an
 * 'em' tag.
 * 
 * @author lobo
 */
public interface SnippetField extends Field<List<String>>
{

}
