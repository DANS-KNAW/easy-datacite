package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractEasyModel;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractListWrapper;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;
import nl.knaw.dans.pf.language.emd.types.BasicDate;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class SingleBasicDateWrapper extends AbstractListWrapper<SingleBasicDateWrapper.DateModel>
{

    private static final long serialVersionUID = 113141702403446894L;

    private List<BasicDate> basicDateList = new ArrayList<BasicDate>();

    public SingleBasicDateWrapper(List<BasicDate> basicDateList)
    {
        this.basicDateList = basicDateList;
    }

    @Override
    public List<DateModel> getInitialItems()
    {
        List<DateModel> listItems = new ArrayList<DateModel>();

        for (BasicDate basicDate : basicDateList)
        {
            listItems.add(new DateModel(basicDate));
        }

        return listItems;
    }

    @Override
    public int size()
    {
        return basicDateList.size();
    }

    @Override
    public int synchronize(List<DateModel> listItems)
    {
        // clear previous entries
        basicDateList.clear();
        // add new entries
        int errors = 0;
        for (int i = 0; i < listItems.size(); i++)
        {
            DateModel model = listItems.get(i);
            BasicDate basicDate = null;
            basicDate = model.getBasicDate();

            if (basicDate != null)
            {
                basicDateList.add(basicDate);
            }
            if (model.hasErrors())
            {
                handleErrors(model.getErrors(), i);
                errors += model.getErrors().size();
            }
            model.clearErrors();
        }
        return errors;
    }

    @Override
    public DateModel getEmptyValue()
    {
        DateModel model = new DateModel();
        return model;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ChoiceRenderer getChoiceRenderer()
    {
        return new KvpChoiceRenderer();
    }

    public static class DateModel extends AbstractEasyModel implements QualifiedModel
    {

        private static final long serialVersionUID = 3608902558575507784L;

        private String dateSchemeType;
        private String value;

        public DateModel(BasicDate basicDate)
        {
            if (basicDate == null)
            {
                throw new IllegalArgumentException("Model for BasicDate cannot be created.");
            }
            this.value = basicDate.toString();
        }

        protected DateModel()
        {
        }

        public String getValue()
        {
            return value;
        }

        public BasicDate getBasicDate()
        {
            return value == null ? null : new BasicDate(value);
        }

        public void setValue(String value)
        {
            this.value = value;
        }

        public void setScheme(KeyValuePair schemeKVP)
        {
            dateSchemeType = schemeKVP == null ? null : schemeKVP.getKey();
        }

        public KeyValuePair getScheme()
        {
            return new KeyValuePair(dateSchemeType, null);
        }

        // Quick fix (Do not try this at home!)
        @Override
        public String getQualifier()
        {
            return dateSchemeType;
        }
    }

}
