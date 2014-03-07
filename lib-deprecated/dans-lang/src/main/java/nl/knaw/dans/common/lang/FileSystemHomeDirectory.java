package nl.knaw.dans.common.lang;

import java.io.File;

public class FileSystemHomeDirectory implements HomeDirectory
{
    private File homedir;

    public FileSystemHomeDirectory(File homedir)
    {
        this.homedir = homedir;
    }

    @Override
    public String getHome()
    {
        return homedir.getAbsolutePath();
    }

    @Override
    public File getHomeDirectory()
    {
        return homedir;
    }

}
