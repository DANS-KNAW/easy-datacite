package nl.knaw.dans.easy.security;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;
import nl.knaw.dans.easy.domain.model.FileItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FreelyAvailableContentCheck extends AbstractCheck
{

    private static final String LIST_LOCATION = "specs/free-content.txt";

    private static List<String> FREE_CONTENT_LIST;

    private static final Logger logger = LoggerFactory.getLogger(FreelyAvailableContentCheck.class);

    @Override
    public String getProposition()
    {
        return "[Content stream of FileItem is marked as freely available content]";
    }

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;
        FileItem fileItem = ctxParameters.getFileItem();
        if (fileItem != null)
        {
            conditionMet = isFreelyAvailableContent(fileItem.getStoreId());
        }
        return conditionMet;
    }

    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        StringBuilder sb = super.startExplain(ctxParameters);

        FileItem fileItem = ctxParameters.getFileItem();
        if (fileItem == null)
        {
            sb.append("\n\tfileItem = null");
        }

        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }

    private boolean isFreelyAvailableContent(String fileItemId)
    {
        boolean available = false;
        try
        {
            available = getFreeContentList().contains(fileItemId);
        }
        catch (IOException e)
        {
            String msg = "Could not read list from " + LIST_LOCATION;
            logger.error(msg, e);
            throw new ApplicationException(msg, e);
        }
        return available;
    }

    private static synchronized List<String> getFreeContentList() throws IOException
    {
        if (FREE_CONTENT_LIST == null)
        {
            FREE_CONTENT_LIST = loadFreeContentList();
        }
        return FREE_CONTENT_LIST;
    }

    private static List<String> loadFreeContentList() throws IOException
    {
        List<String> list = new ArrayList<String>();
        RandomAccessFile raf = null;
        try
        {

            File file;
            try
            {
                file = ResourceLocator.getFile(LIST_LOCATION);
            }
            catch (ResourceNotFoundException e)
            {
                throw new RuntimeException("Could not load free content list");
            }
            raf = new RandomAccessFile(file, "r");
            String line;
            while ((line = raf.readLine()) != null)
            {
                list.add(line);
            }

        }
        finally
        {
            if (raf != null)
            {
                raf.close();
            }
        }
        return list;
    }

}
