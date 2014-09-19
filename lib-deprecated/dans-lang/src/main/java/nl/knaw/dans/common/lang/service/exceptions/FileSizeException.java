package nl.knaw.dans.common.lang.service.exceptions;

public class FileSizeException extends ServiceException {
    private static final long serialVersionUID = 8483469299362768976L;

    protected int amount;
    protected int limit;

    public FileSizeException(String message) {
        super(message);
    }

    public FileSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileSizeException(Throwable cause) {
        super(cause);
    }

    /*
     * Note that sizes in MegaBytes
     */
    public FileSizeException(int amount, int limit) {
        super("The file(s) exceeds the max size limit of " + limit + "megabytes");
        this.amount = amount;
        this.limit = limit;
    }

    public int getAmount() {
        return amount;
    }

    public int getLimit() {
        return limit;
    }

}
