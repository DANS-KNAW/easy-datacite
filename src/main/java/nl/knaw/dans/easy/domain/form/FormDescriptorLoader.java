package nl.knaw.dans.easy.domain.form;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class FormDescriptorLoader
{
    
    private static final Logger logger = LoggerFactory.getLogger(FormDescriptorLoader.class);
    
    public static void loadFormDescriptors(String location, Map<String, FormDescriptor> formDescriptorMap) throws ResourceNotFoundException
    {
        File defFolder = ResourceLocator.getFile(location);
        loadFormDescriptors(defFolder, formDescriptorMap);
    }
    
    public static void loadFormDescriptors(File defFolder, Map<String, FormDescriptor> formDescriptorMap) throws ResourceNotFoundException
    {
        logger.debug("Loading FormDescriptors from " + defFolder.getAbsolutePath());
        if (!defFolder.exists())
        {
            final String msg = "The folder " + defFolder.getAbsolutePath() + " does not exist.";
            logger.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (!defFolder.canRead())
        {
            final String msg = "Cannot access the folder " + defFolder.getAbsolutePath();
            logger.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        List<FormDescriptor> formDescriptorList = loadFromFile(defFolder);

            formDescriptorMap.clear();
            for (FormDescriptor descriptor : formDescriptorList)
            {
                if (formDescriptorMap.get(descriptor.getId()) != null)
                {
                    logger.warn("The id FormDescription:" + descriptor.getId() + " is not unique!");
                }
                else
                {
                    formDescriptorMap.put(descriptor.getId(), descriptor);
                }
            }

        logger.info("Loaded " + formDescriptorMap.size() + " FormDescriptors from " + defFolder.getAbsolutePath());
    }

    private static List<FormDescriptor> loadFromFile(File defFolder)
    {
        List<FormDescriptor> formDescriptorList = new ArrayList<FormDescriptor>();

        for (File defFile : defFolder.listFiles())
        {
            if (isValidDescription(defFile))
            {
                FormDescriptor descriptor = loadDescription(defFile);
                if (descriptor != null)
                {
                    formDescriptorList.add(descriptor);
                }
            }
        }

        Collections.sort(formDescriptorList, new Comparator<FormDescriptor>()
        {

            public int compare(FormDescriptor o1, FormDescriptor o2)
            {
                if (o1 == null && o2 == null)
                {
                    return 0;
                }
                if (o1 == null)
                {
                    return -1;
                }
                if (o2 == null)
                {
                    return 1;
                }
                String ordinal1 = o1.getOrdinal() == null ? "" : o1.getOrdinal();
                String ordinal2 = o2.getOrdinal() == null ? "" : o2.getOrdinal();
                return ordinal1.compareTo(ordinal2);
            }

        });
        return formDescriptorList;
    }

    private static boolean isValidDescription(File defFile)
    {
        if (!defFile.getName().endsWith(".xml"))
        {
            logger.debug("Loading FormDescriptors: skipping file " + defFile.getName());
            return false;
        }
        try
        {
            XMLErrorHandler handler = FormDescriptionValidator.instance().validate(defFile,
                    FormDescriptor.CURRENT_VERSION);
            if (!handler.passed())
            {
                final String msg = "Could not load FormDescription at " + defFile.getAbsolutePath() + "\n"
                        + handler.getMessages();
                logger.error(msg);
                return false;
            }
        }
        catch (ValidatorException e)
        {
            final String msg = "Could not load FormDescription at " + defFile.getAbsolutePath();
            logger.error(msg, e);
            return false;
        }
        catch (SAXException e)
        {
            final String msg = "Could not load FormDescription at " + defFile.getAbsolutePath();
            logger.error(msg, e);
            return false;
        }
        catch (SchemaCreationException e)
        {
            final String msg = "Could not get schema for validation. Schema version=" + FormDescriptor.CURRENT_VERSION;
            logger.error(msg, e);
            return false;
        }
        return true;
    }

    private static FormDescriptor loadDescription(File defFile)
    {
        FormDescriptor descriptor = null;
        try
        {
            descriptor = (FormDescriptor) JiBXObjectFactory.unmarshal(FormDescriptor.class, defFile);
            logger.debug("Loaded FormDescription:" + descriptor.getId() + " from " + defFile.getAbsolutePath());
        }
        catch (XMLDeserializationException e)
        {
            final String msg = "Could not load FormDescription at " + defFile.getAbsolutePath();
            logger.error(msg, e);
        }
        return descriptor;
    }



}
