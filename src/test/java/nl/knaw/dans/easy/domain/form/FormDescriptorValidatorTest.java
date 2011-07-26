package nl.knaw.dans.easy.domain.form;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;

public class FormDescriptorValidatorTest
{
    
    @Test
    public void getURL() throws Exception
    {
        URL url = FormDescriptionValidator.instance().getSchemaURL(FormDescriptionValidator.VERSION_0_1);
        assertNotNull(url);
    }

}
