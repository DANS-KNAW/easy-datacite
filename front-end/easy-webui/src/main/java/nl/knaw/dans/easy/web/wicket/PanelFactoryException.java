package nl.knaw.dans.easy.web.wicket;

public class PanelFactoryException extends RuntimeException
{

    private static final long serialVersionUID = 7423468243187276645L;

    public PanelFactoryException()
    {
    }

    public PanelFactoryException(String message)
    {
        super(message);
    }

    public PanelFactoryException(Throwable cause)
    {
        super(cause);
    }

    public PanelFactoryException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
