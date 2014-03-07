package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.IsoDate;

import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;

public class LimitedDateWrapper extends SingleISODateWrapper
{
    private static final long serialVersionUID = 1L;

    final DateTime min;
    final DateTime max;

    public LimitedDateWrapper(final List<IsoDate> isoDateList, final DateTime min, final DateTime max)
    {
        super(isoDateList);
        this.min = min;
        this.max = max;
    }

    @Override
    public DateModel getEmptyValue()
    {
        return new DateAvailableModel();
    }

    @Override
    public List<DateModel> getInitialItems()
    {
        final List<DateModel> listItems = new ArrayList<DateModel>();

        for (final IsoDate isoDate : isoDateList)
        {
            listItems.add(new DateAvailableModel(isoDate));
        }
        return listItems;
    }

    public class DateAvailableModel extends DateModel
    {
        private static final long serialVersionUID = 1L;

        public DateAvailableModel(final IsoDate isoDate)
        {
            super(isoDate);
        }

        public DateAvailableModel()
        {
            super();
        }

        @Override
        public IsoDate getIsoDate()
        {
            final IsoDate isoDate = super.getIsoDate();
            if (isoDate != null)
            {
                try
                {
                    final DateTime actual = new DateTime(getValue());
                    if (actual.isBefore(min) || actual.isAfter(max))
                    {
                        addErrorMessage("minimun value: " + min.toString(DATE_FORMAT) + " maximum value: " + max.toString(DATE_FORMAT));
                    }
                }
                catch (final IllegalFieldValueException exception)
                {
                    addErrorMessage(exception.getMessage());
                }
                catch (final IllegalArgumentException exception)
                {
                    addErrorMessage(exception.getMessage());
                }
            }
            return isoDate;
        }
    }
}
