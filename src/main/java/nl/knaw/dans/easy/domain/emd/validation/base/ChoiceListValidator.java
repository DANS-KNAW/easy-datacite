package nl.knaw.dans.easy.domain.emd.validation.base;

import static nl.knaw.dans.easy.domain.emd.validation.base.EmdXPath.*;

import java.util.List;

import nl.knaw.dans.common.lang.ApplicationException;
import nl.knaw.dans.common.lang.CacheException;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListGetter;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;

public abstract class ChoiceListValidator implements Validator
{

    public static final class RightsValidator extends ChoiceListValidator
    {
        public RightsValidator(final String listId)
        {
            super(listId, RIGHTS.getXPath());
        }

        @Override
        public List<String> getValidatedValue(final EasyMetadata emd)
        {
            return emd.getEmdRights().getValues();
        }
    }

    public static final class RelationsValidator extends ChoiceListValidator
    {
        public RelationsValidator(final String listId)
        {
            super(listId, RELATION.getXPath());
        }

        @Override
        public List<String> getValidatedValue(final EasyMetadata emd)
        {
            return emd.getEmdRelation().getValues();
        }
    }

    private final String listId;
    private final String xPathStub;
    private ChoiceList choiceList;

    public ChoiceListValidator(final String listId, final String xPathStub)
    {
        this.listId = listId;
        this.xPathStub = xPathStub;

    }

    abstract List<String> getValidatedValue(EasyMetadata emd);

    @Override
    public synchronized void validate(final EasyMetadata emd, final ValidationReporter reporter)
    {
        // TODO why is there a second value 'accept'?
        // controlled vocabularies are not mandatory, so null values are OK
        final List<String> values = getValidatedValue(emd);
        if (values == null || values.size() == 0)
            return;
        final String value = values.get(0);
        if (value == null)
            return;

        if (!choiceListContains(value))
        {
            reporter.setMetadataValid(false);
            final String msg = "The value '" + value + "' of " + xPathStub + " is not a valid key in the list '" + listId + "'";

            reporter.addError(new ValidationReport(msg, xPathStub, this));
        }
    }

    private boolean choiceListContains(final String key)
    {
        final KeyValuePair keyValuePair = new KeyValuePair(key, null);
        return getChoiceList().getChoices().contains(keyValuePair);
    }

    private ChoiceList getChoiceList()
    {
        if (choiceList == null)
        {
            try
            {
                choiceList = ChoiceListGetter.getInstance().getChoiceList(listId, null);
            }
            catch (final ObjectNotFoundException e)
            {
                throw new ApplicationException(e);
            }
            catch (final CacheException e)
            {
                throw new ApplicationException(e);
            }
            catch (final ResourceNotFoundException e)
            {
                throw new ApplicationException(e);
            }
            catch (final DomainException e)
            {
                throw new ApplicationException(e);
            }
        }
        return choiceList;
    }
}
