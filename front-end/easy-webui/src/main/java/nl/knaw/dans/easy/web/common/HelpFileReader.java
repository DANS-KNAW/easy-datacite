package nl.knaw.dans.easy.web.common;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.util.TextFileReader;

/**
 * Helper class to read a help item from the corresponding file in the easy.home directory.
 */
public class HelpFileReader extends TextFileReader {
    public HelpFileReader(String helpFileName) {
        super(getHelpFile(helpFileName));
    }

    private static File getHelpFile(String helpFileName) {
        try {
            return ResourceLocator.getFile("/help/" + helpFileName + ".template");
        }
        catch (ResourceNotFoundException e) {
            /*
             * These semantics are not pretty. The problem is that not all form fields have associated help items. If we return a non-existent file (as below)
             * everything works out correctly.
             */
            return new File("");
        }
    }

    public String read() {
        try {
            return super.read();
        }
        catch (IOException e) {}

        return "Could not find help on this item";
    }
}
