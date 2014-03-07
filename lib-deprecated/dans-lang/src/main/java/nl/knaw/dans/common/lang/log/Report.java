package nl.knaw.dans.common.lang.log;

import java.io.File;

public interface Report
{

    void setReportLocation(File reportLocation, boolean allRW);

    void info(Event event);

    void warn(Event event);

    void error(Event event);

    void close();

}
