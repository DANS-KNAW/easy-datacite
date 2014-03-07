package nl.knaw.dans.common.lang.repo.exception;

public class StorageDeviceException extends RemoteException
{

    private static final long serialVersionUID = -5430696437938317393L;

    public StorageDeviceException()
    {
    }

    public StorageDeviceException(String message)
    {
        super(message);
    }

    public StorageDeviceException(Throwable cause)
    {
        super(cause);
    }

    public StorageDeviceException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
