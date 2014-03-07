package nl.knaw.dans.easy.web.common;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.servicelayer.services.Services;

public class DisciplineUtils
{
    public static ChoiceList getDisciplinesChoiceList()
    {
        try
        {
            return Services.getDepositService().getChoices("custom.disciplines", null);
        }
        catch (ServiceException e)
        {
            throw new InternalWebError();
        }
    }

    public static KeyValuePair getDisciplineItemById(String disciplineId)
    {
        final ChoiceList choices = getDisciplinesChoiceList();

        for (KeyValuePair kvp : choices.getChoices())
        {
            if (kvp.getKey().equals(disciplineId))
            {
                return kvp;
            }
        }

        return null;
    }

}
