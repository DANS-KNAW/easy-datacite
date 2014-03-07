package nl.knaw.dans.common.lang.log;

public class EventNameOverviewReport extends OverviewReport
{

    public static final String DEFAULT_EVENT_NAME = RL.GLOBAL;

    private final String eventName;

    public EventNameOverviewReport()
    {
        this(DEFAULT_EVENT_NAME);
    }

    public EventNameOverviewReport(String eventName)
    {
        this(eventName, null);
    }

    public EventNameOverviewReport(String eventName, EventPrinter eventPrinter)
    {
        super(eventPrinter);
        this.eventName = eventName;
    }

    @Override
    public String getFileName()
    {
        return eventName + "-" + super.getFileName();
    }

    @Override
    public void info(Event event)
    {
        if (eventName.equals(event.getEventName()))
            super.info(event);
    }

    @Override
    public void warn(Event event)
    {
        if (eventName.equals(event.getEventName()))
            super.warn(event);
    }

    @Override
    public void error(Event event)
    {
        if (eventName.equals(event.getEventName()))
            super.error(event);
    }

}
