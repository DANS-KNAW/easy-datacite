package nl.knaw.dans.common.lang.log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reporter of {@link Event}s.
 * 
 * @see RL
 */
public class Reporter
{

    private static final Logger logger = LoggerFactory.getLogger(Reporter.class);

    private File reportLocation;

    private boolean allRW;

    private List<Report> reports = new ArrayList<Report>();

    public Reporter()
    {

    }

    public Reporter(final String reportDirectoryName)
    {
        this(reportDirectoryName, false);
    }

    public Reporter(final String reportDirectoryName, boolean allRW)
    {
        this(new File(reportDirectoryName), allRW);
    }

    public Reporter(File reportDirectory, boolean allRW)
    {
        this.allRW = allRW;
        this.reportLocation = reportDirectory;
    }

    public List<Report> getReports()
    {
        if (reports.isEmpty())
        {
            reports.add(new LoggerReport());
        }
        return reports;
    }

    public void addReport(Report report)
    {
        report.setReportLocation(getReportLocation(), allRW);
        reports.add(report);
    }

    public boolean removeReport(Report report)
    {
        return reports.remove(report);
    }

    public void setReports(List<Report> reports)
    {
        this.reports = reports;
        setLocationOnReports();
    }

    private void setLocationOnReports()
    {
        for (Report report : this.reports)
        {
            report.setReportLocation(getReportLocation(), allRW);
        }
    }

    public File getReportLocation()
    {
        if (reportLocation == null)
        {
            reportLocation = new File(RL.DEFAULT_REPORT_LOCATION);
            prepareReportLocation();
        }
        return reportLocation;
    }

    public void setReportLocation(File location, boolean allReadWrite)
    {
        this.reportLocation = location;
        this.allRW = allReadWrite;
        prepareReportLocation();
        setLocationOnReports();
    }

    private void prepareReportLocation()
    {
        try
        {
            RL.prepareReportLocation(reportLocation, allRW);
        }
        catch (IOException e)
        {
            throw new RLRuntimeException(e);
        }
    }

    public void info(Event event)
    {
        event.setCaller(getCaller());
        event.setLevel(Event.INFO);
        for (Report report : getReports())
        {
            report.info(event);
        }
    }

    public void warn(Event event)
    {
        event.setCaller(getCaller());
        event.setLevel(Event.WARNING);
        for (Report report : getReports())
        {
            report.warn(event);
        }
    }

    public void error(Event event)
    {
        event.setCaller(getCaller());
        event.setLevel(Event.ERROR);
        for (Report report : getReports())
        {
            report.error(event);
        }
    }

    public void close()
    {
        for (Report report : getReports())
        {
            report.close();
        }
        logger.info("Closed reports");
    }

    private Caller getCaller()
    {
        String className = "";
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
        {
            String cn = ste.getClassName();
            if (!cn.equals(Thread.class.getName()) && !cn.equals(Reporter.class.getName()) && !cn.equals(this.getClass().getName())
                    && !cn.equals(RL.class.getName()))
            {
                className = ste.getClassName();
                sb.append(className).append(".").append(ste.getMethodName()).append(" (").append(ste.getFileName()).append(":").append(ste.getLineNumber())
                        .append(")");
                break;
            }
        }
        return new Caller(className, sb.toString());
    }

    public static class Caller
    {
        private final String className;
        private final String sourceLink;

        public Caller(String className, String sourceLink)
        {
            this.className = className;
            this.sourceLink = sourceLink;
        }

        public String getClassName()
        {
            return className;
        }

        public String getSourceLink()
        {
            return sourceLink;
        }
    }

}
