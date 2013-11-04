package nl.knaw.dans.easy.business.bean;

import java.io.Serializable;

public class SystemStatus implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final SystemStatus instance = new SystemStatus();
    private boolean readOnly = false;
    private long lastCheck = System.currentTimeMillis();
    private long checkFrequency = 1000 * 60 * 2;

    /**
     * Singleton
     */
    private SystemStatus()
    {
    }

    public static SystemStatus instance()
    {
        return instance;
    }

    public boolean getReadOnly()
    {
        refreshValues();
        return readOnly;
    }

    public void setReadOnly(boolean isReadOnly)
    {
        this.readOnly = isReadOnly;
        changeValues();
    }

    public long getCheckFrequency()
    {
        return checkFrequency;
    }

    public void setCheckFrequency(long checkFrequency)
    {
        this.checkFrequency = checkFrequency;
    }

    private void changeValues()
    {
        // TODO create / drop / change file
    }

    private void refreshValues()
    {
        // each WebApp has its own instance of the singleton
        // so we synchronize SWORD, web-ui and the rest interfaces via the file system
        if (System.currentTimeMillis() < lastCheck + getCheckFrequency())
            return;
        // TODO check file
    }
}
