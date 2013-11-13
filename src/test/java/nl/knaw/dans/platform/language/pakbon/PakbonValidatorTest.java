package nl.knaw.dans.platform.language.pakbon;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import javax.xml.soap.SOAPException;

import nl.knaw.dans.pf.language.xml.exc.ValidatorException;

import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlResponse;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidationMessage;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

//@Ignore("On-line test")
public class PakbonValidatorTest
{
    private static final String DEFAULT_USERNAME = "bergh";
    private static final String DEFAULT_PASSWORD = "cC!XzlKK";
    private static final String PB_VALID = "src/test/resources/test-files/pakbon_valid.xml";
    private static final String PB_INVALID = "src/test/resources/test-files/pakbon_invalid.xml";

    @Test
    public void testInvalid() throws Exception
    {
        PakbonValidator pbs = new PakbonValidator(new PakbonValidatorCredentials(DEFAULT_USERNAME, DEFAULT_PASSWORD));
        ValidateXmlResponse response = pbs.validateXml(new File(PB_INVALID));

        if (response.getValidation().getMessages() != null)
        {
            for (ValidationMessage msg : response.getValidation().getMessages())
            {
                System.err.println(msg.getMessage());
            }
        }
        assertThat(response.getValidation().getValidXml(), is(false));
    }

    @Test
    public void testValid() throws Exception
    {
        doValidTest();
    }

    @Test
    public void testMultiThread() throws Exception
    {
        for (int i = 0; i < 10; i++)
        {
            Runnable pester = new Runnable()
            {

                @Override
                public void run()
                {
                    try
                    {
                        boolean valid = doValidTest();
                        System.out.println(" isValid=" + valid);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };
            new Thread(pester).start();
        }
        Thread.sleep(10000);
    }

    private boolean doValidTest() throws ValidatorException, SOAPException, IOException
    {
        PakbonValidator pbs = new PakbonValidator(new PakbonValidatorCredentials(DEFAULT_USERNAME, DEFAULT_PASSWORD));
        ValidateXmlResponse response = pbs.validateXml(new File(PB_VALID));

        assertThat(response.getVersion(), is("2.1.0"));
        assertThat(response.getSuccess(), is(true));
        return response.getValidation().getValidXml();
    }

}
