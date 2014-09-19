package nl.knaw.dans.easy.web.statistics;

import java.util.HashMap;

import nl.knaw.dans.easy.domain.download.DownloadInfo;
import nl.knaw.dans.easy.domain.download.FileContentWrapper;

public class DownloadStatistics extends StatisticsModel<DownloadInfo> {
    public DownloadStatistics(DownloadInfo info) {
        super(info);
    }

    @Override
    public HashMap<String, String> getLogValues() {
        HashMap<String, String> res = new HashMap<String, String>();
        int i = 0;
        for (String file : getObject().getFileNames()) {
            res.put(String.format("FILE_NAME(%d)", i++), file);
        }
        return res;
    }

    @Override
    public String getName() {
        return "file";
    }
}
