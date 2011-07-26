package nl.knaw.dans.easy.web.fileexplorer2;

// commentaar, 2e poging, hk 110708

import java.io.IOException;
import java.io.InputStream;

import nl.knaw.dans.easy.domain.download.FileContentWrapper;

import org.apache.wicket.AbortException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamDownloadPage extends WebPage
{
    private static final Logger logger = LoggerFactory.getLogger(StreamDownloadPage.class);

    // NOTE: GK: don't try to do this with ZipFileContentWrapper...

    public StreamDownloadPage(FileContentWrapper fcw)
    {
        InputStream is = null;

        try
        {
            is = fcw.getURL().openStream();
            write(fcw.getFileName(), fcw.getFileItemVO().getMimetype(), fcw.getFileItemVO().getSize(), is);
        }
        catch (IOException e)
        {
            logger.error("Error while trying to stream single file download.", e);
        }
        finally
        {
            close(is);
        }
    }

    private void close(InputStream is)
    {
        try
        {
            if (is != null)
            {
                is.close();
            }
        }
        catch (IOException e)
        {

        }
    }

    private void write(String filename, String mimeType, long length, InputStream inStream) throws IOException
    {
        String name = filename == null ? "no-name" : filename.replaceAll(" ", "_");

        WebResponse response = getWebRequestCycle().getWebResponse();
        response.setContentType(mimeType);
        response.setContentLength(length);
        response.setAttachmentHeader(name);
        response.write(inStream);
        throw new AbortException();
    }
}
