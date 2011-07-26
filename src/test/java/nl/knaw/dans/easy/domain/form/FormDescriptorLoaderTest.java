package nl.knaw.dans.easy.domain.form;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.XMLException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class FormDescriptorLoaderTest
{
    
    private static final Logger logger = LoggerFactory.getLogger(FormDescriptorLoaderTest.class);
    
    
    @BeforeClass
    public static void beforeClass() throws ServiceException
    {
        ClassPathHacker.addFile("../easy-webui/src/main/resources/");
    }
    
    @Test
    public void testLoadFormDescriptors() throws ServiceException, ResourceNotFoundException, XMLException, SAXException, SchemaCreationException
    {
        String location = "easy-business/discipline/emd/form-description/";
        Map<String, FormDescriptor> formDescriptorMap = new HashMap<String, FormDescriptor>();
        FormDescriptorLoader.loadFormDescriptors(location, formDescriptorMap);
        assertEquals(4, formDescriptorMap.size());
        
        for (String name : formDescriptorMap.keySet())
        {
            FormDescriptor formDescriptor = formDescriptorMap.get(name);
            FormDescriptionValidator.instance().validate(formDescriptor);
            checkIds(formDescriptor);
        }
    }

    private void checkIds(FormDescriptor formDescriptor)
    {
        for (TermPanelDefinition tpd : formDescriptor.getTermPanelDefinitions())
        {
            if (!tpd.getId().equals(tpd.getNamespacePrefix() + "." + tpd.getTermName()))
            {
                logger.warn("Id not correct " + formDescriptor.getId() + " " + tpd.getId() + ": "
                        + tpd.getNamespacePrefix() + "." + tpd.getTermName());
            }
            
        }
        
    }

}
