package nl.knaw.dans.easy.domain.model.emd;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;

import org.junit.Test;

public class EmdRelationChoiceListTest
{
    @Test
    public void testRelationChoiceList() throws ServiceException
    {
        final ChoiceList choiceList = new EasyDepositService().getChoices("common.dcterms.relation", null);
        final List<KeyValuePair> choices = choiceList.getChoices();
        final List<String> emdListKeys = Arrays.asList(EmdRelation.LIST_KEYS);
        final Set<String> choiceListKeys = new HashSet<String>();

        // check the choicelist size.
        assertTrue("The size of choice list must not be longer than " + emdListKeys.size(), choices.size() <= emdListKeys.size());

        for (final KeyValuePair choice : choices)
        {
            final String choiceKey = choice.getKey();
            choiceListKeys.add(choiceKey);
            assertTrue("The choice list key [" + choiceKey + "] is not found in LIST_KEYS of " + EmdRelation.class.getName(), emdListKeys.contains(choiceKey));
        }
        for (final String emdKey : emdListKeys)
        {
            // so far this happens to pass, but should all relation instances allow all types of relations??
            assertTrue("This keys is not found:" + emdKey, emdKey.trim().length() == 0 || choiceListKeys.contains(emdKey));
        }
    }
}
