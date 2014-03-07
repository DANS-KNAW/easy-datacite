package nl.knaw.dans.common.lang.log;

public class ErrorOverviewReport extends OverviewReport
{

    public static final String DEFAULT_ERROR_FILENAME = "errors.csv";

    public ErrorOverviewReport()
    {
        super(DEFAULT_ERROR_FILENAME);
    }

    public ErrorOverviewReport(String fileName)
    {
        super(fileName);
    }

    public ErrorOverviewReport(EventPrinter eventPrinter)
    {
        super(DEFAULT_ERROR_FILENAME, eventPrinter);
    }

    public ErrorOverviewReport(String fileName, EventPrinter eventPrinter)
    {
        super(fileName, eventPrinter);
    }

    @Override
    public void info(Event event)
    {
        // we do not print info events
    }

    @Override
    public void warn(Event event)
    {
        // we do not print warning events
    }

}
