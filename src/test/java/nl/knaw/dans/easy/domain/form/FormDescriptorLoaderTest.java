package nl.knaw.dans.easy.domain.form;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.XMLException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class FormDescriptorLoaderTest
{

    private static final Logger logger = LoggerFactory.getLogger(FormDescriptorLoaderTest.class);

    @Test
    public void testLoadFormDescriptors() throws ServiceException, ResourceNotFoundException, XMLException, SAXException, SchemaCreationException
    {
        Map<String, FormDescriptor> formDescriptorMap = new HashMap<String, FormDescriptor>();
        FormDescriptorLoader.loadFormDescriptors(formDescriptorMap);
        assertEquals(6, formDescriptorMap.size());

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
                logger.warn("Id not correct " + formDescriptor.getId() + " " + tpd.getId() + ": " + tpd.getNamespacePrefix() + "." + tpd.getTermName());
            }

        }

    }

}
