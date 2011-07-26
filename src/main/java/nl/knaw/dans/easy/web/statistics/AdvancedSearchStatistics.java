package nl.knaw.dans.easy.web.statistics;

import java.util.HashMap;
import java.util.List;

import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.easy.web.search.pages.AdvSearchData;

public class AdvancedSearchStatistics extends StatisticsModel<AdvSearchData>
{

    public AdvancedSearchStatistics(AdvSearchData asd)
    {
        super(asd);
    }

    @Override
    public HashMap<String, String> getLogValues()
    {
        HashMap<String, String> res = new HashMap<String, String>();

        AdvSearchData asd = getObject();

        if(asd.query != null && !asd.query.equals(""))
        {
            res.put("ANY_FIELD", asd.query);
        }
        
        List<SimpleField> fields = asd.fields;
        for(SimpleField<String> sf: fields) 
        {
            if(sf.getValue()!=null)
            {
                res.put(sf.getName(), (String)sf.getValue());
            }
        }

        return res;
    }

    @Override
    public String getName()
    {
        return "searchrequest";
    }

}
