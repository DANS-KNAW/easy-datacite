package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractEasyModel;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractListWrapper;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;
import nl.knaw.dans.pf.language.emd.EmdDate;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class IsoDateListWrapper extends AbstractListWrapper<IsoDateListWrapper.IsoDateModel>
{

    private static final long serialVersionUID = -7329761811695091371L;

    private Map<String, List<IsoDate>> listMap = new HashMap<String, List<IsoDate>>();

    public IsoDateListWrapper(EmdDate emdDate)
    {
        listMap = emdDate.getIsoDateMap();
    }

    public ChoiceRenderer getChoiceRenderer()
    {
        return new KvpChoiceRenderer();
    }

    public IsoDateModel getEmptyValue()
    {
        IsoDateModel model = new IsoDateModel();
        return model;
    }

    public List<IsoDateModel> getInitialItems()
    {
        List<IsoDateModel> listItems = new ArrayList<IsoDateModel>();
        for (String dateSchemaType : listMap.keySet())
        {
            List<IsoDate> isoDates = listMap.get(dateSchemaType);
            for (IsoDate isoDate : isoDates)
            {
                listItems.add(new IsoDateModel(isoDate, dateSchemaType));
            }
        }
        return listItems;
    }

    @Override
    public int size()
    {
        return getInitialItems().size();
    }

    public int synchronize(List<IsoDateModel> listItems)
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
            IsoDateModel model = listItems.get(i);
            IsoDate isoDate = model.getIsoDate();

            if (isoDate != null)
            {
                String dateSchemaType = model.dateSchemeType == null ? "" : model.dateSchemeType;
                listMap.get(dateSchemaType).add(isoDate);
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

    public static class IsoDateModel extends AbstractEasyModel implements QualifiedModel
    {

        private static final long serialVersionUID = 3841830259279016843L;

        private String dateSchemeType;
        private IsoDate isoDate;

        public IsoDateModel(IsoDate isoDate, String dateSchemeType)
        {
            if (isoDate == null)
            {
                throw new IllegalArgumentException("Model for IsoDate cannot be created.");
            }
            if ("".equals(dateSchemeType))
            {
                this.dateSchemeType = null;
            }
            else
            {
                this.dateSchemeType = dateSchemeType;
            }
            this.isoDate = isoDate;
        }

        protected IsoDateModel()
        {
        }

        public IsoDate getIsoDate()
        {
            return isoDate;
        }

        public String getValue()
        {
            if (isoDate == null)
            {
                return null;
            }
            else
            {
                return isoDate.toString();
            }
        }

        public void setValue(String value)
        {
            // wicket 1.4 DatePicker bug workaround
            if (value != null && value.length() == 8)
            {
                if (value.charAt(2) == '-' && value.charAt(5) == '-')
                {
                    value = "20" + value;
                }
            }
            // end workaround
            if (StringUtils.isBlank(value))
            {
                isoDate = null;
            }
            else
            {
                isoDate = convertToDateTime(value);
            }
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
