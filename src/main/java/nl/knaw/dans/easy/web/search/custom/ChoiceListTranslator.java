package nl.knaw.dans.easy.web.search.custom;

import java.util.Locale;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.search.Translator;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.servicelayer.services.Services;

public class ChoiceListTranslator implements Translator<String>
{

    private static final long serialVersionUID = -5317555410541552791L;
    private static final Logger logger = LoggerFactory.getLogger(ChoiceListTranslator.class);

    private final String listId;

    public ChoiceListTranslator(String listId)
    {
        this.listId = listId;
    }

    @Override
    public IModel<String> getTranslation(String originalValue, Locale locale, boolean fullName)
    {
        String translation = null;
        try
        {
            ChoiceList choiceList = Services.getDepositService().getChoices(listId, locale);
            translation = choiceList.getValue(originalValue);
            if (translation == null)
            {
                logger.error("No value found for key '" + originalValue + "' in list " + listId);
                translation = originalValue;
            }
        }
        catch (ServiceException e)
        {
            logger.error("Could not get choicelist: ", e);
        }

        return new Model<String>(translation);
    }

}
