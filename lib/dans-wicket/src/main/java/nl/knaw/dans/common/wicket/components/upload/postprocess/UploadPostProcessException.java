package nl.knaw.dans.common.wicket.components.upload.postprocess;

@SuppressWarnings("serial")
public class UploadPostProcessException extends Exception {
    public UploadPostProcessException() {
        super();
    }

    public UploadPostProcessException(Exception e) {
        super(e);
    }

    public UploadPostProcessException(String msg) {
        super(msg);
    }

    public UploadPostProcessException(String msg, Throwable e) {
        super(msg, e);
    }
}
