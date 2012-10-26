package nl.knaw.dans.common.wicket.components.upload.postprocess;

public class UploadPostProcessException extends Exception
{
    private static final long serialVersionUID = 1769153762647134647L;

    public UploadPostProcessException()
    {
        super();
    }

    public UploadPostProcessException(Exception e)
    {
        super(e);
    }

    public UploadPostProcessException(String msg)
    {
        super(msg);
    }
}
