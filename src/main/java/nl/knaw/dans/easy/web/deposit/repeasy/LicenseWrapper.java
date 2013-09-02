package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractDefaultListWrapper;
import nl.knaw.dans.pf.language.emd.EmdRights;
import nl.knaw.dans.pf.language.emd.types.BasicString;

public class LicenseWrapper extends AbstractDefaultListWrapper<KeyValuePair, BasicString>
{

    private static final long serialVersionUID = 1357696704603653686L;

    private Map<String, List<BasicString>> listMap = new HashMap<String, List<BasicString>>();

    public LicenseWrapper(EmdRights emdRights, List<BasicString> wrappedList)
    {
        super(wrappedList, null, null);
        listMap = emdRights.getRights();
    }

    public List<KeyValuePair> getInitialItems()
    {
        List<KeyValuePair> listItems = new ArrayList<KeyValuePair>();
        KeyValuePair kvp = new KeyValuePair();
        if (!getWrappedList().isEmpty())
        {
            kvp = new KeyValuePair("true", EmdRights.LICENSE_ACCEPT);
        }
        listItems.add(kvp);
        return listItems;
    }

    public int synchronize(List<KeyValuePair> listItems)
    {

        getWrappedList().clear();

        for (KeyValuePair keyValuePair : listItems)
        {
            if (keyValuePair.getKey().equals("true"))
            {
                getWrappedList().add(new BasicString(EmdRights.LICENSE_ACCEPT));
            }
        }

        return 0;
    }

    @Override
    public KeyValuePair getEmptyValue()
    {
        return new KeyValuePair(null, null);
    }
}
