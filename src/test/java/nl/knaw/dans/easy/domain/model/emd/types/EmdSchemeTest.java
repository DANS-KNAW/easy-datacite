package nl.knaw.dans.easy.domain.model.emd.types;

import java.io.File;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListCache;
import nl.knaw.dans.easy.search.RecursiveListCache;

import org.junit.Test;

public class EmdSchemeTest
{
    private static final ChoiceListCache CACHE = ChoiceListCache.getInstance();

    @Test
    public void filesExist() throws Exception
    {
        for (final EmdScheme scheme : EmdScheme.values())
            CACHE.getInputStream(scheme.getId()).close();
    }

    @Test
    public void CheckConstantsExist() throws Exception
    {
        final String folder = ResourceLocator.getURL(ChoiceListCache.BASE_FOLDER).getFile();
        checkFolder(new File(folder));
    }

    @Test
    public void CheckConstantsExist2() throws Exception
    {
        final String folder = ResourceLocator.getURL(RecursiveListCache.BASE_FOLDER).getFile();
        checkFolder(new File(folder));
    }

    private void checkFolder(final File folder)
    {
        for (final File file : folder.listFiles())
        {
            if (file.getName().contains("_"))
            {
                // skip locale specific files
            }
            else if (file.isFile())
            {
                final String path = strip(file.getPath());
                EmdScheme.valueOf(path.toUpperCase().replaceAll("/", "_").replace(".xml", ""));
            }
            else if (file.getName().contains("dmo-collections"))
            {
                // TODO should schemeIDs allow dashes in discipline names?
            }
            else
                checkFolder(file);
        }
    }

    private String strip(String path)
    {
        return path.replaceAll(".*" + RecursiveListCache.BASE_FOLDER, "").replaceAll(".*" + ChoiceListCache.BASE_FOLDER, "").replaceAll("\\..*", "");
    }
}
