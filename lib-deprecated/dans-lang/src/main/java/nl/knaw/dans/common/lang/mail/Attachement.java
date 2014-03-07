package nl.knaw.dans.common.lang.mail;

import java.io.File;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.util.ByteArrayDataSource;

/** Immutable object */
public class Attachement
{
    public final String fileName;
    final DataSource dataSource;

    public Attachement(final String fileName, final File file)
    {
        // TODO don't we need a content type?
        dataSource = new FileDataSource(file);
        this.fileName = fileName;
    }

    public Attachement(final String fileName, final String mimeType, final byte[] content)
    {
        dataSource = new ByteArrayDataSource(content, mimeType);
        this.fileName = fileName;
    }
}
