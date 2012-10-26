package nl.knaw.dans.easy.web.statistics;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class UploadFileStatistics extends StatisticsModel<List<File>>
{
    public UploadFileStatistics(List<File> files)
    {
        super(files);
    }

    @Override
    public HashMap<String, String> getLogValues()
    {
        HashMap<String, String> res = new HashMap<String, String>();
        List<File> files = getObject();
        for (File f : files)
        {
            res.put("FILE_NAME", f.getName());
        }
        return res;
    }

    @Override
    public String getName()
    {
        return "files";
    }
}
