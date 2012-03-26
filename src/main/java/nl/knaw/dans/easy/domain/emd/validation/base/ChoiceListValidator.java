package nl.knaw.dans.easy.domain.emd.validation.base;


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

    private final String listId;
    private final String xPathStub;
    private ChoiceList   choiceList;

    public ChoiceListValidator(final String listId, final String xPathStub)
    {
        this.listId = listId;
        this.xPathStub = xPathStub;

    }

    public abstract String getValidatedValue(EasyMetadata emd);

    @Override
    public synchronized void validate(final EasyMetadata emd, final ValidationReporter reporter)
    {
        // TODO why is there a second value 'accept'?
        final String value = getValidatedValue(emd);

        if (value == null)
        {
            // don't make controlled vocabularies mandatory, that is checked otherwise
            return;
        }
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
