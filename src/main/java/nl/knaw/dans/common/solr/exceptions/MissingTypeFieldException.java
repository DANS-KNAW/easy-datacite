package nl.knaw.dans.common.solr.exceptions;


public class MissingTypeFieldException extends SolrSearchEngineException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1467424051244992771L;

	public MissingTypeFieldException()
	{
	}

	public MissingTypeFieldException(String message)
	{
		super(message);
	}

	public MissingTypeFieldException(Throwable cause)
	{
		super(cause);
	}

	public MissingTypeFieldException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
