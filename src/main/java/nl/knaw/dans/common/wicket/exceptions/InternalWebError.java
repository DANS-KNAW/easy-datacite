package nl.knaw.dans.common.wicket.exceptions;

import org.apache.wicket.Application;
import org.apache.wicket.RestartResponseException;

public class InternalWebError extends RestartResponseException
{
	private static final long	serialVersionUID	= 3581913687647975402L;

	public InternalWebError()
	{
		super(Application.get().getApplicationSettings().getInternalErrorPage());
	}

}
