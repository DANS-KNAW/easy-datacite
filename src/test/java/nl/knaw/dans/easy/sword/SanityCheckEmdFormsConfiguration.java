package nl.knaw.dans.easy.sword;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import nl.knaw.dans.easy.business.services.EasyDepositService;

import org.junit.Test;

public class SanityCheckEmdFormsConfiguration
{
    @Test
    public void generatePackagingDoc() throws Exception
    {
        String helpDir = System.getProperties().get("EASY_WEBUI_HOME") + "/res/example/editable/help/";
        if (helpDir == null || !new File(helpDir).exists())
            throw new IllegalArgumentException("please set/correct ${EASY_WEBUI_HOME} " + helpDir);

        // relies on the started server for the required fedora context
        IntegrationTester.start();
        final StringBuffer html = new PackagingDoc().generate(new EasyDepositService(), helpDir);
        IntegrationTester.stop();

        // just check one of the audiences retrieved from fedora
        assertTrue(html.toString().contains("Glasblazerij"));

        // for a visual check
        OutputStream outputStream = new FileOutputStream("target/packagingDoc.html");
        outputStream.write(html.toString().getBytes());
        outputStream.close();

        // are all help files available?
        assertFalse(html.toString().contains("file does not exist"));
    }
}
