package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.web.deposit.repeater.AbstractDefaultListWrapper;
import nl.knaw.dans.pf.language.emd.types.BasicRemark;

import org.apache.commons.lang.StringUtils;

/**
 * Wraps a list of {@link BasicRemark} to obtain {@link String}s.
 */
public class BasicRemarkListWrapper extends AbstractDefaultListWrapper<String, BasicRemark>
{

    private static final long serialVersionUID = -8198236619846333286L;

    public BasicRemarkListWrapper(List<BasicRemark> sourceList)
    {
        super(sourceList);
    }

    public BasicRemarkListWrapper(List<BasicRemark> sourceList, String schemeName, String schemeId)
    {
        super(sourceList, schemeName, schemeId);
    }

    public List<String> getInitialItems()
    {
        List<String> listItems = new ArrayList<String>();
        for (BasicRemark bs : getWrappedList())
        {
            listItems.add(bs.getValue());
        }
        return listItems;
    }

    public int synchronize(List<String> listItems)
    {
        getWrappedList().clear();
        for (String str : (listItems))
        {
            if (!StringUtils.isBlank(str))
            {
                getWrappedList().add(new BasicRemark(str));
            }
        }
        return 0;
    }

}
