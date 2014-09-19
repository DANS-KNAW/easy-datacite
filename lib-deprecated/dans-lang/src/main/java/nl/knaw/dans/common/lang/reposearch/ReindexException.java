package nl.knaw.dans.common.lang.reposearch;

public class ReindexException extends Exception {
    private static final long serialVersionUID = -1013701628733758889L;

    private ReindexReport report;

    public ReindexException(Throwable cause, ReindexReport report) {
        super(cause);
        this.report = report;
    }

    public ReindexReport getReindexReport() {
        return report;
    }

}
