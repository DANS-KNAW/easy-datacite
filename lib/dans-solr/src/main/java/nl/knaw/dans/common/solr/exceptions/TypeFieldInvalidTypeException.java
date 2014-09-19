package nl.knaw.dans.common.solr.exceptions;

public class TypeFieldInvalidTypeException extends SolrSearchEngineException {
    private static final long serialVersionUID = -2855545993789208336L;

    public TypeFieldInvalidTypeException() {}

    public TypeFieldInvalidTypeException(String message) {
        super(message);
    }

    public TypeFieldInvalidTypeException(Throwable cause) {
        super(cause);
    }

    public TypeFieldInvalidTypeException(String message, Throwable cause) {
        super(message, cause);
    }

}
