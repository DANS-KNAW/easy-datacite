package nl.knaw.dans.common.wicket.components.upload;


/**
 * @author lobo
 * This class contains the information concerning the upload process. It is being held
 * by an UploadProcess class.
 */
public class UploadStatus
{
	private Integer percentComplete = 0;

	private boolean error = false;

	private boolean finished = false;

	private String message;

	public UploadStatus(String message) {
		this.message = message;
		percentComplete = 0;
	}

	public boolean isError()
	{
		return error;
	}

	public void setError(boolean error)
	{
		this.error = error;
	}

	public void setError(String errorMessage)
	{
		this.error = true;
		this.message = errorMessage;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Integer getPercentComplete()
	{
		return percentComplete;
	}

	public void setPercentComplete(Integer percentComplete)
	{
		this.percentComplete = percentComplete;
	}

	public boolean isFinished()
	{
		return finished;
	}

	public void setFinished(boolean finished)
	{
		this.finished = finished;
	}
}
