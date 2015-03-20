package nl.knaw.dans.easy.web.deposit.repeasy;

import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_AIP_ID;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_DMO_ID;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_DOI;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_PID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractDefaultListWrapper;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class IdentifierListWrapper extends AbstractDefaultListWrapper<IdentifierListWrapper.IdentifierModel, BasicIdentifier> {

    private static final long serialVersionUID = -8745696945204069167L;

    private static List<String> NON_EDITABLE = Arrays.asList(new String[] {SCHEME_PID, SCHEME_DOI, SCHEME_DMO_ID, SCHEME_AIP_ID, SCHEME_ARCHIS_ONDERZOEK_M_NR});
    private static List<String> IMMUTABLE = Arrays.asList(new String[] {SCHEME_DMO_ID, SCHEME_ARCHIS_ONDERZOEK_M_NR});
    private static List<String> PERSISTENT = Arrays.asList(new String[] {SCHEME_PID, SCHEME_DOI});

    private boolean showOnlyPeristent = false;
    private boolean showNoPeristent = false;

    public IdentifierListWrapper(List<BasicIdentifier> wrappedList) {
        super(wrappedList);
    }

    @Override
    public List<IdentifierModel> getInitialEditableItems() {
        List<IdentifierModel> listItems = new ArrayList<IdentifierModel>();
        for (BasicIdentifier basicIdentifier : getWrappedList()) {
            if (basicIdentifier.getValue() != null && isEditable(basicIdentifier.getScheme())) {
                listItems.add(new IdentifierModel(basicIdentifier));
            }
        }
        return listItems;
    }

    private void removeAllEditableItems() {
        List<BasicIdentifier> editableList = new ArrayList<BasicIdentifier>();
        for (BasicIdentifier basicIdentifier : getWrappedList()) {
            if (isEditable(basicIdentifier.getScheme())) {
                editableList.add(basicIdentifier);
            }
        }
        getWrappedList().removeAll(editableList);
    }

    public List<IdentifierModel> getInitialItems() {
        List<IdentifierModel> listItems = new ArrayList<IdentifierModel>();
        for (BasicIdentifier basicIdentifier : getWrappedList()) {
            if (basicIdentifier.getValue() != null && isWanted(basicIdentifier.getScheme())) {
                listItems.add(new IdentifierModel(basicIdentifier));
            }
        }
        return listItems;
    }

    public int synchronize(List<IdentifierModel> listItems) {
        removeAllEditableItems();
        for (IdentifierModel model : listItems) {
            BasicIdentifier basicIdentifier = model.getBasicIdentifier();
            if (basicIdentifier != null && basicIdentifier.getValue() != null) {
                getWrappedList().add(basicIdentifier);
            }
        }
        return 0;
    }

    @Override
    public IdentifierModel getEmptyValue() {
        IdentifierModel model = new IdentifierModel();
        return model;
    }

    @Override
    public ChoiceRenderer<KeyValuePair> getChoiceRenderer() {
        return new KvpChoiceRenderer();
    }

    private boolean isEditable(String scheme) {
        return !NON_EDITABLE.contains(scheme);
    }

    private boolean isWanted(String scheme) {
        if (showOnlyPeristent)
            return PERSISTENT.contains(scheme);
        if (showNoPeristent)
            return !PERSISTENT.contains(scheme);
        return true;
    }

    public void showNoPeristent(boolean showNoPeristent) {
        this.showNoPeristent = showNoPeristent;
    }

    public void showOnlyPeristent(boolean showOnlyPeristent) {
        this.showOnlyPeristent = showOnlyPeristent;
    }

    public static class IdentifierModel implements Serializable {

        private static final long serialVersionUID = 3841830253279006843L;

        private final BasicIdentifier basicIdentifier;

        public IdentifierModel(BasicIdentifier basicIdentifier) {
            this.basicIdentifier = basicIdentifier;
        }

        protected IdentifierModel() {
            basicIdentifier = new BasicIdentifier();
        }

        public BasicIdentifier getBasicIdentifier() {
            return basicIdentifier;
        }

        public void setScheme(KeyValuePair schemeKVP) {
            String scheme = schemeKVP == null ? null : schemeKVP.getKey();
            if (!IMMUTABLE.contains(basicIdentifier.getScheme())) {
                basicIdentifier.setScheme(scheme);
            }
        }

        public KeyValuePair getScheme() {
            return new KeyValuePair(basicIdentifier.getScheme(), null);
        }

        public void setValue(String value) {
            basicIdentifier.setValue(value);
        }

        public String getValue() {
            return basicIdentifier.getValue();
        }

    }

}
