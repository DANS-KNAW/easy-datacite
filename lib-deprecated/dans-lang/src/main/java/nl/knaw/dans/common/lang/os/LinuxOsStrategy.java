package nl.knaw.dans.common.lang.os;

import java.io.File;
import java.io.IOException;

public class LinuxOsStrategy implements OsStrategy
{

    @Override
    public int setAllRWX(File file, Appendable out, Appendable err) throws IOException
    {
        String cmd = "chmod a=rwx " + file.getPath();
        return OS.execAndWait(cmd, out, err);
    }

    @Override
    public int setAllRWX(String filename, Appendable out, Appendable err) throws IOException
    {
        String cmd = "chmod a=rwx " + filename;
        return OS.execAndWait(cmd, out, err);
    }

}
