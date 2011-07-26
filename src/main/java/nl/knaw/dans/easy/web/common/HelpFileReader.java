package nl.knaw.dans.easy.web.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nl.knaw.dans.common.lang.util.TextFileReader;
import nl.knaw.dans.easy.util.EasyHome;

/**
 * Helper class to read a help item from the corresponding file in the easy.home directory.
 */
public class HelpFileReader extends TextFileReader
{
    public HelpFileReader(String helpFileName)
    {
        super(getHelpFile(helpFileName));
    }

    private static File getHelpFile(String helpFileName)
    {
        try
        {
            return new File(EasyHome.getLocation(), "/editable/help/" + helpFileName + ".template");
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("Easy.home not set", e);
        }
    }

    public String read()
    {
        try
        {
            return super.read();
        }
        catch (IOException e)
        {
        }

        return "Could not find help on this item";
    }
}
