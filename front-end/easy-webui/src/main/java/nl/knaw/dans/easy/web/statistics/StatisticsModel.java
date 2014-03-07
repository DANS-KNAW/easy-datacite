package nl.knaw.dans.easy.web.statistics;

import java.util.HashMap;

public abstract class StatisticsModel<T>
{
    private T obj;

    public StatisticsModel(T obj)
    {
        this.obj = obj;
    }

    public T getObject()
    {
        return obj;
    }

    abstract public String getName();

    abstract public HashMap<String, String> getLogValues();
}
