package nl.knaw.dans.common.solr.exceptions;


public class TypeFieldEmptyException extends SolrSearchEngineException
{
	private static final long serialVersionUID = 1467424051244992771L;

	public TypeFieldEmptyException()
	{
	}

	public TypeFieldEmptyException(String message)
	{
		super(message);
	}

	public TypeFieldEmptyException(Throwable cause)
	{
		super(cause);
	}

	public TypeFieldEmptyException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
