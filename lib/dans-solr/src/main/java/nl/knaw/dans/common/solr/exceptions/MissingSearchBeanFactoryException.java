package nl.knaw.dans.common.solr.exceptions;

public class MissingSearchBeanFactoryException extends SolrSearchEngineException {
    private static final long serialVersionUID = 736708590554967787L;

    public MissingSearchBeanFactoryException() {}

    public MissingSearchBeanFactoryException(String message) {
        super(message);
    }

    public MissingSearchBeanFactoryException(Throwable cause) {
        super(cause);
    }

    public MissingSearchBeanFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
