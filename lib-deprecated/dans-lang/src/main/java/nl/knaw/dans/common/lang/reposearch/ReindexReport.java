package nl.knaw.dans.common.lang.reposearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import nl.knaw.dans.common.lang.file.SidListFile;
import nl.knaw.dans.common.lang.repo.DmoStoreId;

public class ReindexReport
{
    private final List<DmoStoreId> reindexed;

    private final List<ReindexError> errors;

    public ReindexReport(List<DmoStoreId> reindexed, List<ReindexError> errors)
    {
        this.reindexed = reindexed;
        this.errors = errors;
    }

    public List<DmoStoreId> getReindexed()
    {
        return reindexed;
    }

    public List<ReindexError> getErrors()
    {
        return errors;
    }

    public void writeErrors(File errorFile) throws IOException
    {
        FileOutputStream out = null;
        PrintStream ps = null;
        try
        {
            out = new FileOutputStream(errorFile);
            ps = new PrintStream(out);
            for (ReindexError error : errors)
            {
                printLn(out, "-----------------------------------------------");
                printLn(out, "Error during " + error.getSid() + " on " + error.getOperation() + "\r\n");
                printLn(out, "Exception = " + error.getException().getMessage());
                error.getException().printStackTrace(ps);
                printLn(out, "-----------------------------------------------");
            }
        }
        finally
        {
            if (out != null)
                out.close();
            if (ps != null)
                ps.close();
        }
    }

    private void printLn(OutputStream out, String line) throws IOException
    {
        out.write(new String(line + "\r\n").getBytes());
    }

    public void writeReindexSidList(File outputSidListFile) throws IOException
    {
        SidListFile.writeSidList(outputSidListFile, getReindexed());
    }

}
