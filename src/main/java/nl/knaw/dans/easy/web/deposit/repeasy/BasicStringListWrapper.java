package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.web.deposit.repeater.AbstractDefaultListWrapper;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.apache.commons.lang.StringUtils;

/**
 * Wraps a list of {@link BasicString} to obtain {@link String}s.
 * 
 * @author ecco Mar 31, 2009
 */
public class BasicStringListWrapper extends AbstractDefaultListWrapper<String, BasicString>
{

    private static final long serialVersionUID = -8198236609846333286L;

    public BasicStringListWrapper(List<BasicString> sourceList)
    {
        super(sourceList);
    }

    public BasicStringListWrapper(List<BasicString> sourceList, String schemeName, String schemeId)
    {
        super(sourceList, schemeName, schemeId);
    }

    public List<String> getInitialItems()
    {
        List<String> listItems = new ArrayList<String>();
        for (BasicString bs : getWrappedList())
        {
            if (isSame(getSchemeName(), bs.getScheme()))
            {
                listItems.add(bs.getValue());
            }
        }
        return listItems;
    }

    public int synchronize(List<String> listItems)
    {
        List<BasicString> filteredList = new ArrayList<BasicString>();
        for (BasicString bs : getWrappedList())
        {
            if (isSame(getSchemeName(), bs.getScheme()))
            {
                filteredList.add(bs);
            }
        }
        getWrappedList().removeAll(filteredList);

        for (String str : (listItems))
        {
            if (!StringUtils.isBlank(str))
            {
                BasicString basicString = new BasicString(str);
                basicString.setScheme(getSchemeName());
                basicString.setSchemeId(getSchemeId());
                getWrappedList().add(basicString);
            }
        }
        return 0;
    }

}
