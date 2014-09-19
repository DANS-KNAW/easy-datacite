package nl.knaw.dans.common.lang.service.exceptions;

public class ServiceRuntimeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -6644568278387009383L;

    public ServiceRuntimeException() {}

    public ServiceRuntimeException(String msg) {
        super(msg);
    }

    public ServiceRuntimeException(Throwable e) {
        super(e);
    }

    public ServiceRuntimeException(String msg, Throwable e) {
        super(msg, e);
    }

}
