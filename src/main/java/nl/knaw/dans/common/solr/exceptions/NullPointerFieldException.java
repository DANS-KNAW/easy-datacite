package nl.knaw.dans.common.solr.exceptions;

public class NullPointerFieldException extends SolrSearchEngineException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3786970232723442636L;

	public NullPointerFieldException()
	{
	}

	public NullPointerFieldException(String message)
	{
		super(message);
	}

	public NullPointerFieldException(Throwable cause)
	{
		super(cause);
	}

	public NullPointerFieldException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
