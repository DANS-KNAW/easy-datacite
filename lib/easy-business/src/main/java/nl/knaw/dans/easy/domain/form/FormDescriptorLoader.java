package nl.knaw.dans.easy.domain.form;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormDescriptorLoader {
    public static final String FORM_DESCRIPTIONS_FULL_PATH = "easy-business/discipline/emd/form-description/";
    public static final String FORM_DESCRIPTIONS = "form-descriptions/";

    public static final String[] LOCATIONS = {FORM_DESCRIPTIONS + "archaeology.xml", FORM_DESCRIPTIONS + "history.xml", FORM_DESCRIPTIONS + "sociology.xml",
            FORM_DESCRIPTIONS + "lifescience.xml", FORM_DESCRIPTIONS + "language_literature.xml", FORM_DESCRIPTIONS + "unspecified.xml",};

    private static final Logger logger = LoggerFactory.getLogger(FormDescriptorLoader.class);

    public static void loadFormDescriptors(Map<String, FormDescriptor> formDescriptorMap) throws ResourceNotFoundException {
        List<FormDescriptor> formDescriptorList;
        try {
            formDescriptorList = loadDescriptors();
        }
        catch (IOException e) {
            throw new ResourceNotFoundException(e);
        }

        formDescriptorMap.clear();
        for (FormDescriptor descriptor : formDescriptorList) {
            if (formDescriptorMap.get(descriptor.getId()) != null) {
                logger.warn("The id FormDescription:" + descriptor.getId() + " is not unique!");
            } else {
                formDescriptorMap.put(descriptor.getId(), descriptor);
            }
        }

        logger.info("Loaded " + formDescriptorMap.size() + " FormDescriptors");
    }

    private static List<FormDescriptor> loadDescriptors() throws IOException, ResourceNotFoundException {
        List<FormDescriptor> formDescriptorList = new ArrayList<FormDescriptor>();
        for (String location : LOCATIONS) {
            URL url = FormDescriptorLoader.class.getResource(location);
            if (url == null) {
                final String msg = "Could not load FormDescription at " + location;
                logger.error(msg);
            } else {
                FormDescriptor formDescriptor = loadDescription(url);
                if (formDescriptor != null) {
                    formDescriptorList.add(formDescriptor);
                }
            }
        }
        return formDescriptorList;
    }

    private static FormDescriptor loadDescription(URL url) throws IOException {
        FormDescriptor descriptor = null;
        try {
            descriptor = (FormDescriptor) JiBXObjectFactory.unmarshal(FormDescriptor.class, url.openStream());
            logger.debug("Loaded FormDescription:" + descriptor.getId() + " from " + url.toString());
        }
        catch (XMLDeserializationException e) {
            final String msg = "Could not load FormDescription at " + url.toString();
            logger.error(msg, e);
        }
        return descriptor;
    }

}
