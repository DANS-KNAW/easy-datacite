package nl.knaw.dans.easy.web.statistics;

import java.util.HashMap;

import nl.knaw.dans.easy.web.template.AbstractEasyPage;


public class PageClassStatistics extends StatisticsModel<AbstractEasyPage>
{

    public PageClassStatistics(AbstractEasyPage aep)
    {
        super(aep);
    }

    @Override
    public HashMap<String, String> getLogValues()
    {
        HashMap<String, String> res = new HashMap<String, String>();
        res.put("CLASS_NAME", this.getObject().getClass().getName());
        
        return res;
    }

    @Override
    public String getName()
    {
        return "PageClass";
    }

}