package nl.knaw.dans.easy.web.statistics;

import java.util.HashMap;

import nl.knaw.dans.easy.domain.download.ZipFileContentWrapper;

public class DownloadZipStatistics extends StatisticsModel<ZipFileContentWrapper>
{
    public DownloadZipStatistics(ZipFileContentWrapper file)
    {
        super(file);
    }

    @Override
    public HashMap<String, String> getLogValues()
    {
        HashMap<String, String> res = new HashMap<String, String>();
        res.put("NUM_FILES", "" + getObject().getDownloadedItemVOs().size());
        return res;
    }

    @Override
    public String getName()
    {
        return "zip-file";
    }
}
