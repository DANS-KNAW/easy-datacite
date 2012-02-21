package nl.knaw.dans.easy.web.search.custom;

import java.util.Locale;

import nl.knaw.dans.common.jibx.bean.RecursiveEntry;
import nl.knaw.dans.common.jibx.bean.RecursiveList;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.search.Translator;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecursiveListTranslator implements Translator<String>
{

    private static final long serialVersionUID = 3535819881331764331L;
    private static final Logger logger = LoggerFactory.getLogger(RecursiveListTranslator.class);
    
    private final String listId;
    
    public RecursiveListTranslator(String listId)
    {
        this.listId = listId;
    }

    @Override
    public IModel<String> getTranslation(String originalValue, Locale locale, boolean fullName)
    {
        String translation = null;
        try
        {
            RecursiveList recursiveList = Services.getDepositService().getRecursiveList(listId, locale);
            RecursiveEntry entry = recursiveList.getEntry(originalValue);
            if (entry == null)
            {
                logger.error("No entry found for key '" + originalValue + "' in list " + listId);
                translation = originalValue;
            }
            else
            {
                translation = fullName ? entry.getName() : entry.getShortname();
            }
        }
        catch (ServiceException e)
        {
            logger.error("Could not get recursive list: ", e);
        }
        
        return new Model<String>(translation);
    }

}
