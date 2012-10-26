package nl.knaw.dans.easy.web.deposit.repeasy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.emd.types.BasicIdentifier;
import nl.knaw.dans.easy.domain.model.emd.types.EmdConstants;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractDefaultListWrapper;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class IdentifierListWrapper extends AbstractDefaultListWrapper<IdentifierListWrapper.IdentifierModel, BasicIdentifier>
{

    private static final long serialVersionUID = -8745696945204069167L;

    private static List<String> SCHEMENAME_FILTER_LIST;

    public IdentifierListWrapper(List<BasicIdentifier> wrappedList)
    {
        super(wrappedList);
    }

    @Override
    public List<IdentifierModel> getInitialEditableItems()
    {
        List<IdentifierModel> listItems = new ArrayList<IdentifierModel>();
        for (BasicIdentifier basicIdentifier : getWrappedList())
        {
            boolean editable = !getNonEditableShemes().contains(basicIdentifier.getScheme());
            if (basicIdentifier.getValue() != null && editable)
            {
                listItems.add(new IdentifierModel(basicIdentifier));
            }
        }
        return listItems;
    }

    private void removeAllEditableItems()
    {
        List<BasicIdentifier> editableList = new ArrayList<BasicIdentifier>();
        for (BasicIdentifier bi : getWrappedList())
        {
            boolean editable = !getNonEditableShemes().contains(bi.getScheme());
            {
                if (editable)
                {
                    editableList.add(bi);
                }
            }
        }
        getWrappedList().removeAll(editableList);
    }

    private List<String> getNonEditableShemes()
    {
        if (SCHEMENAME_FILTER_LIST == null)
        {
            SCHEMENAME_FILTER_LIST = new ArrayList<String>();
            SCHEMENAME_FILTER_LIST.add(EmdConstants.SCHEME_PID);
            SCHEMENAME_FILTER_LIST.add(EmdConstants.SCHEME_AIP_ID);
            SCHEMENAME_FILTER_LIST.add(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);
            SCHEMENAME_FILTER_LIST.add(EmdConstants.SCHEME_DMO_ID);

        }
        return SCHEMENAME_FILTER_LIST;
    }

    public List<IdentifierModel> getInitialItems()
    {
        List<IdentifierModel> listItems = new ArrayList<IdentifierModel>();
        for (BasicIdentifier basicIdentifier : getWrappedList())
        {
            if (basicIdentifier.getValue() != null)
            {
                listItems.add(new IdentifierModel(basicIdentifier));
            }
        }
        return listItems;
    }

    public int synchronize(List<IdentifierModel> listItems)
    {
        removeAllEditableItems();
        for (IdentifierModel model : listItems)
        {
            BasicIdentifier basicIdentifier = model.getBasicIdentifier();
            if (basicIdentifier != null && basicIdentifier.getValue() != null)
            {
                getWrappedList().add(basicIdentifier);
            }
        }
        return 0;
    }

    @Override
    public IdentifierModel getEmptyValue()
    {
        IdentifierModel model = new IdentifierModel();
        return model;
    }

    @Override
    public ChoiceRenderer getChoiceRenderer()
    {
        return new KvpChoiceRenderer();
    }

    public static class IdentifierModel implements Serializable
    {

        private static final long serialVersionUID = 3841830253279006843L;

        private final BasicIdentifier basicIdentifier;

        public IdentifierModel(BasicIdentifier basicIdentifier)
        {
            this.basicIdentifier = basicIdentifier;
        }

        protected IdentifierModel()
        {
            basicIdentifier = new BasicIdentifier();
        }

        public BasicIdentifier getBasicIdentifier()
        {
            return basicIdentifier;
        }

        public void setScheme(KeyValuePair schemeKVP)
        {
            String scheme = schemeKVP == null ? null : schemeKVP.getKey();
            boolean immutable = EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR.equals(basicIdentifier.getScheme());
            immutable |= EmdConstants.SCHEME_DMO_ID.equals(basicIdentifier.getScheme());

            if (!immutable)
            {
                basicIdentifier.setScheme(scheme);
            }
        }

        public KeyValuePair getScheme()
        {
            return new KeyValuePair(basicIdentifier.getScheme(), null);
        }

        public void setValue(String value)
        {
            basicIdentifier.setValue(value);
        }

        public String getValue()
        {
            return basicIdentifier.getValue();
        }

    }

}
