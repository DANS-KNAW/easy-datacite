package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractDefaultListWrapper;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

/**
 * Wraps a list of {@link BasicString} to obtain {@link KeyValuePair}s.
 * 
 * @author ecco Mar 31, 2009
 */
public class SchemedBasicStringListWrapper extends AbstractDefaultListWrapper<KeyValuePair, BasicString> {

    private static final long serialVersionUID = 7976759517463796566L;

    public SchemedBasicStringListWrapper(List<BasicString> wrappedList) {
        super(wrappedList);
    }

    public SchemedBasicStringListWrapper(List<BasicString> wrappedList, String schemeName, String schemeId) {
        super(wrappedList, schemeName, schemeId);

    }

    public List<KeyValuePair> getInitialItems() {
        List<KeyValuePair> listItems = new ArrayList<KeyValuePair>();
        for (BasicString bs : getWrappedList()) {
            if (isSame(getSchemeName(), bs.getScheme())) {
                listItems.add(new KeyValuePair(bs.getValue(), null));
            }
        }
        return listItems;
    }

    public int synchronize(List<KeyValuePair> listItems) {
        List<BasicString> filteredList = new ArrayList<BasicString>();
        for (BasicString bs : getWrappedList()) {
            if (isSame(getSchemeName(), bs.getScheme())) {
                filteredList.add(bs);
            }
        }
        getWrappedList().removeAll(filteredList);

        for (KeyValuePair keyValuePair : listItems) {
            if (keyValuePair != null && keyValuePair.getKey() != null) {
                final BasicString bs = new BasicString(keyValuePair.getKey());
                bs.setScheme(getSchemeName());
                bs.setSchemeId(getSchemeId());
                getWrappedList().add(bs);
            }
        }
        return 0;
    }

    @Override
    public ChoiceRenderer getChoiceRenderer() {
        return new KvpChoiceRenderer();
    }

    @Override
    public KeyValuePair getEmptyValue() {
        return new KeyValuePair(null, null);
    }

}
