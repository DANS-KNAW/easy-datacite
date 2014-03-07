package nl.knaw.dans.easy.web.wicket;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.PropertiesMessage;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSelector extends AutoCompleteTextField
{
    private static final long serialVersionUID = 3797498659017039856L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SelectUserPanel.class);

    public UserSelector(String wicketId)
    {
        this(wicketId, new IdModel());
    }

    public UserSelector(String wicketId, IModel model)
    {
        super(wicketId, model, new AbstractAutoCompleteTextRenderer()
        {

            private static final long serialVersionUID = -3654758614039672040L;

            @SuppressWarnings("unchecked")
            @Override
            protected String getTextValue(Object obj)
            {
                Map.Entry<String, String> entry = (Entry<String, String>) obj;
                String value = entry.getValue().replaceAll(":", "");
                String id = entry.getKey();
                return value + " : " + id;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Iterator getChoices(String text)
    {
        Iterator iterator = null;
        try
        {
            Map<String, String> idNameMap = Services.getUserService().getByCommonNameStub(text, 10L);
            iterator = idNameMap.entrySet().iterator();
        }
        catch (ServiceException e)
        {
            final String message = new PropertiesMessage("UserSelector").errorMessage(EasyResources.INTERNAL_ERROR);
            LOGGER.error(message, e);
            throw new InternalWebError();
        }
        return iterator;
    }

}
