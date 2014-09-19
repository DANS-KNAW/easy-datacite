package nl.knaw.dans.common.lang.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.log.Reporter.Caller;

import org.joda.time.DateTime;

public class Event {
    public static final String INFO = "INFO";
    public static final String WARNING = "WARN";
    public static final String ERROR = "ERROR";

    private final DateTime date;
    private final String eventName;
    private final List<String> messages = new ArrayList<String>();
    private final Throwable cause;

    private String level;
    private Caller caller;

    private Details details;

    private String resourceId;

    public Event(String eventName, String... messages) {
        this(eventName, null, messages);
    }

    public Event(Throwable cause, String... messages) {
        this("", cause, messages);
    }

    public Event(String eventName, Throwable cause, String... messages) {
        this.date = new DateTime();
        this.eventName = eventName;
        this.messages.addAll(Arrays.asList(messages));
        this.cause = cause;
    }

    public String getEventName() {
        return eventName;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void addMessage(int index, String message) {
        messages.add(index, message);
    }

    public String getLevel() {
        return level;
    }

    void setLevel(String level) {
        this.level = level;
    }

    public Caller getCaller() {
        return caller;
    }

    void setCaller(Caller caller) {
        this.caller = caller;
    }

    public String getClassName() {
        return caller.getClassName();
    }

    public String getSourceLink() {
        return caller.getSourceLink();
    }

    public Throwable getCause() {
        return cause;
    }

    public boolean hasCause() {
        return cause != null;
    }

    public DateTime getDate() {
        return date;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public boolean hasDetails() {
        return details != null;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public void setDetails(String detailReportLocation, String reportName) {
        setDetails(new Details(detailReportLocation, reportName));
    }

    public void setDetails(String detailReportLocation, String reportName, String linkPrefix) {
        setDetails(new Details(detailReportLocation, reportName, linkPrefix));
    }

    @Override
    public String toString() {
        return new StringBuilder() //
                .append(this.getClass().getName() + " [").append(level + " ") //
                .append(eventName + "]").toString();
    }

    public static class Details {

        private final String reportName;

        private final String detailReportLocation;

        private String linkPrefix;

        public Details(String detailReportLocation, String reportName) {
            this.detailReportLocation = detailReportLocation;
            this.reportName = reportName;
        }

        public Details(String detailReportLocation, String reportName, String linkPrefix) {
            this.detailReportLocation = detailReportLocation;
            this.reportName = reportName;
            this.linkPrefix = linkPrefix;
        }

        public String getReportName() {
            return reportName == null ? "error" : reportName;
        }

        public String getDetailReportLocation() {
            return detailReportLocation;
        }

        public String getLinkPrefix() {
            return linkPrefix == null ? "" : linkPrefix;
        }

        public String getDetailLink() {
            return getLinkPrefix() + "/" + detailReportLocation + "/" + reportName;
        }

    }

}
