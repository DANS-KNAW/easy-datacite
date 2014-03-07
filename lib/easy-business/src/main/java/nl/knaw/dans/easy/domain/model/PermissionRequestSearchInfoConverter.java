package nl.knaw.dans.easy.domain.model;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.search.bean.SearchFieldConverter;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanConverterException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;

public class PermissionRequestSearchInfoConverter implements SearchFieldConverter<List<PermissionRequestSearchInfo>>
{

    @SuppressWarnings("unchecked")
    public List<PermissionRequestSearchInfo> fromFieldValue(Object in) throws SearchBeanConverterException
    {
        List<String> inList;
        if (in instanceof String)
        {
            inList = new ArrayList<String>(1);
            inList.add((String) in);
        }
        else
            inList = (List<String>) in;
        List<PermissionRequestSearchInfo> out = new ArrayList<PermissionRequestSearchInfo>();
        for (String inStr : inList)
        {
            PermissionRequestSearchInfo pmInfo = new PermissionRequestSearchInfo();
            try
            {
                pmInfo.fromString(inStr);
            }
            catch (DomainException e)
            {
                throw new SearchBeanConverterException(e);
            }
            out.add(pmInfo);
        }
        return out;
    }

    public Object toFieldValue(List<PermissionRequestSearchInfo> in) throws SearchBeanConverterException
    {
        List<String> out = new ArrayList<String>(in.size());
        for (PermissionRequestSearchInfo pmInfo : in)
        {
            out.add(pmInfo.toString());
        }
        return out;
    }

}
