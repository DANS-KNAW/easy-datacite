package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.emd.EmdDate;
import nl.knaw.dans.easy.domain.model.emd.types.BasicDate;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractEasyModel;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractListWrapper;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class BasicDateListWrapper extends AbstractListWrapper<BasicDateListWrapper.BasicDateModel>
{

    private static final long serialVersionUID = 2640551572293247405L;

    private Map<String, List<BasicDate>> listMap = new HashMap<String, List<BasicDate>>();

    public BasicDateListWrapper(EmdDate emdDate)
    {
        listMap = emdDate.getBasicDateMap();
    }

    public ChoiceRenderer getChoiceRenderer()
    {
        return new KvpChoiceRenderer();
    }

    public BasicDateModel getEmptyValue()
    {
        BasicDateModel model = new BasicDateModel();
        return model;
    }

    public List<BasicDateModel> getInitialItems()
    {
        List<BasicDateModel> listItems = new ArrayList<BasicDateModel>();
        for (String dateSchemaType : listMap.keySet())
        {
            List<BasicDate> basicDates = listMap.get(dateSchemaType);
            for (BasicDate basicDate : basicDates)
            {
                listItems.add(new BasicDateModel(basicDate, dateSchemaType));
            }
        }
        return listItems;
    }

    @Override
    public int size()
    {
        return getInitialItems().size();
    }

    public int synchronize(List<BasicDateModel> listItems)
    {
        // clear previous entries
        for (String dateSchemeType : listMap.keySet())
        {
            listMap.get(dateSchemeType).clear();
        }

        // add new entries
        int errors = 0;
        for (int i = 0; i < listItems.size(); i++)
        {
            BasicDateModel model = listItems.get(i);
            BasicDate basicDate = model.getBasicDate();

            if (basicDate != null)
            {
                String dateSchemaType = model.dateSchemeType == null ? "" : model.dateSchemeType;
                listMap.get(dateSchemaType).add(basicDate);
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

    public static class BasicDateModel extends AbstractEasyModel implements QualifiedModel
    {

        private static final long serialVersionUID = 5937997441499000891L;

        private String dateSchemeType;
        private String value;

        protected BasicDateModel()
        {

        }

        public BasicDateModel(BasicDate basicDate, String dateSchemeType)
        {
            if (basicDate == null)
            {
                throw new IllegalArgumentException("Model for BasicDate cannot be created.");
            }
            if ("".equals(dateSchemeType))
            {
                this.dateSchemeType = null;
            }
            else
            {
                this.dateSchemeType = dateSchemeType;
            }
            this.value = basicDate.toString();
        }

        public BasicDate getBasicDate()
        {
            return value == null ? null : new BasicDate(value);
        }

        public String getValue()
        {
            return value;
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
