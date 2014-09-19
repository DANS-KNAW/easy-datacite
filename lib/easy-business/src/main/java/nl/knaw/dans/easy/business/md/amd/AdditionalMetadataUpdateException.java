package nl.knaw.dans.easy.business.md.amd;

public class AdditionalMetadataUpdateException extends Exception {

    private static final long serialVersionUID = 3367117392047114497L;

    public AdditionalMetadataUpdateException() {

    }

    public AdditionalMetadataUpdateException(String msg) {
        super(msg);
    }

    public AdditionalMetadataUpdateException(Throwable cause) {
        super(cause);
    }

    public AdditionalMetadataUpdateException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
