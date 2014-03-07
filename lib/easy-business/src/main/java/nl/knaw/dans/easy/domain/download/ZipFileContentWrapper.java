package nl.knaw.dans.easy.domain.download;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nl.knaw.dans.easy.domain.dataset.item.ItemVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipFileContentWrapper implements Serializable, DownloadInfo
{

    private static final long serialVersionUID = -3671147015443361517L;

    private static final Logger logger = LoggerFactory.getLogger(ZipFileContentWrapper.class);

    private File zipFile;

    private List<? extends ItemVO> downloadedItemVOs = new ArrayList<ItemVO>();

    private String filename;

    public File getZipFile()
    {
        return zipFile;
    }

    public void setZipFile(File zipFile)
    {
        this.zipFile = zipFile;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public List<? extends ItemVO> getDownloadedItemVOs()
    {
        return downloadedItemVOs;
    }

    public void setDownloadedItemVOs(List<? extends ItemVO> downloadedItemVOs)
    {
        this.downloadedItemVOs = downloadedItemVOs;
    }

    public boolean deleteZipFile() throws IOException
    {
        boolean deleted = false;
        if (zipFile != null)
        {
            deleted = zipFile.delete();
            if (!deleted)
            {
                String msg = "Could not delete zip file " + zipFile.getAbsolutePath();
                logger.error(msg);
                throw new IOException(msg);
            }
        }
        return deleted;
    }

    @Override
    public List<String> getFileNames()
    {
        List<String> result = new LinkedList<String>();

        for (ItemVO item : downloadedItemVOs)
        {
            result.add(item.getPath());
        }

        return result;
    }

}
