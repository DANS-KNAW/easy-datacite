package nl.knaw.dans.easy.business.item;

import static org.junit.Assert.assertTrue;
import nl.knaw.dans.easy.business.item.DownloadWorker;

import org.junit.Test;

public class DownloadLicenseTest
{
    @Test
    public void getLicenseFile()
    {
        assertTrue(null !=DownloadWorker.class.getResource(DownloadWorker.GENERAL_CONDITIONS_FILE_NAME));
    }
}
