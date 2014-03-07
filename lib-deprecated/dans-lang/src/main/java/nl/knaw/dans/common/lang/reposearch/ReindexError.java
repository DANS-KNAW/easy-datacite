package nl.knaw.dans.common.lang.reposearch;

import nl.knaw.dans.common.lang.repo.DmoStoreId;

public class ReindexError
{
    private final Throwable exception;

    private final DmoStoreId sid;

    private final String operation;

    public ReindexError(Throwable e, DmoStoreId s, String o)
    {
        this.exception = e;
        this.sid = s;
        this.operation = o;
    }

    public String getOperation()
    {
        return operation;
    }

    public DmoStoreId getSid()
    {
        return sid;
    }

    public Throwable getException()
    {
        return exception;
    }

}
