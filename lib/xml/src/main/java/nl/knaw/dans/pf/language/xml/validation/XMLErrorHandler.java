package nl.knaw.dans.pf.language.xml.validation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Collects validating errors and warnings and prints them to the log-facility. At which log-level messages will be printed is dependent on the
 * {@link XMLErrorHandler.Reporter}.
 * 
 * @author ecco
 */
public class XMLErrorHandler implements ErrorHandler {

    /**
     * Decides at which log-level notification messages are logged.
     * 
     * @author ecco
     */
    public enum Reporter {
        /**
         * No reporting.
         */
        off {

            /**
             * {@inheritDoc}
             */
            @Override
            void evaluate(final SAXParseException parseException, final String severity, final int msgCount) {
                // off means no reporting.
            }
        },
        /**
         * Report at DEBUG level.
         */
        debug {

            /**
             * {@inheritDoc}
             */
            @Override
            void evaluate(final SAXParseException parseException, final String severity, final int msgCount) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(getMessage(parseException, severity, msgCount));
                }
            }
        },
        /**
         * Report at WARN level.
         */
        warn {

            /**
             * {@inheritDoc}
             */
            @Override
            void evaluate(final SAXParseException parseException, final String severity, final int msgCount) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn(getMessage(parseException, severity, msgCount));
                }
            }
        },
        /**
         * Report at ERROR level.
         */
        error {

            /**
             * {@inheritDoc}
             */
            @Override
            void evaluate(final SAXParseException parseException, final String severity, final int msgCount) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error(getMessage(parseException, severity, msgCount));
                }
            }
        };

        /**
         * Log at proper log-level.
         * 
         * @param parseException
         *        the exception to log
         * @param severity
         *        the severity to include in the message
         * @param msgCount
         *        the count to include in the message
         */
        abstract void evaluate(SAXParseException parseException, String severity, int msgCount);
    }

    /**
     * Severity when a warning notification is received.
     */
    private static final String SEVERITY_WARNING = "warning";

    /**
     * Severity when a error notification is received.
     */
    private static final String SEVERITY_ERROR = "error";

    /**
     * Severity when a fatal error notification is received.
     */
    private static final String SEVERITY_FATAL_ERROR = "fatal error";

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLErrorHandler.class);

    private Reporter reporter;

    private int notifications;

    private List<SAXParseException> warnings = new ArrayList<SAXParseException>();
    private List<SAXParseException> errors = new ArrayList<SAXParseException>();
    private List<SAXParseException> fatalErrors = new ArrayList<SAXParseException>();

    /**
     * Constructs a new XMLErrorHandler with a Reporter set to {@link Reporter#debug}.
     */
    public XMLErrorHandler() {
        super();
        reporter = Reporter.debug;
    }

    /**
     * Constructs a new XMLErrorHandler with the given Reporter.
     * 
     * @param reporter
     *        the reporter to use
     */
    public XMLErrorHandler(final Reporter reporter) {
        super();
        this.reporter = reporter;
    }

    /**
     * Get the Reporter used for logging Sax notifications.
     * 
     * @return reporter for logging notifications.
     */
    public Reporter getReporter() {
        return reporter;
    }

    /**
     * Set the Reporter for logging Sax notifications.
     * 
     * @param reporter
     *        reporter for logging notifications.
     */
    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    /**
     * Reset this XMLErrorHandler to it's original state.
     */
    public void reset() {
        warnings.clear();
        errors.clear();
        fatalErrors.clear();
        notifications = 0;
    }

    /**
     * {@inheritDoc}
     */
    public void error(final SAXParseException parseException) throws SAXException {
        errors.add(parseException);
        reporter.evaluate(parseException, SEVERITY_ERROR, ++notifications);
    }

    /**
     * {@inheritDoc}
     */
    public void fatalError(final SAXParseException parseException) throws SAXException {
        fatalErrors.add(parseException);
        reporter.evaluate(parseException, SEVERITY_FATAL_ERROR, ++notifications);
    }

    /**
     * {@inheritDoc}
     */
    public void warning(final SAXParseException parseException) throws SAXException {
        warnings.add(parseException);
        reporter.evaluate(parseException, SEVERITY_WARNING, ++notifications);
    }

    /**
     * Get the list of warnings.
     * 
     * @return the list with warnings
     */
    public List<SAXParseException> getWarnings() {
        return warnings;
    }

    /**
     * Get the list of errors.
     * 
     * @return the list of errors
     */
    public List<SAXParseException> getErrors() {
        return errors;
    }

    /**
     * Get the list of fatal errors. There can only be one fatal error in this list, since parsing stops after a fatal error.
     * 
     * @return the list of fatal errors
     */
    public List<SAXParseException> getFatalErrors() {
        return fatalErrors;
    }

    /**
     * Get all notifications: warnings, errors and fatal errors.
     * 
     * @return all the notifications
     */
    public List<SAXParseException> getNotifications() {
        final List<SAXParseException> allErrors = new ArrayList<SAXParseException>(getWarnings());
        allErrors.addAll(getErrors());
        allErrors.addAll(getFatalErrors());
        return allErrors;
    }

    /**
     * Get the total number of errors, warnings and fatal errors.
     * 
     * @return the number of notifications this XMLErrorHandler received
     */
    public int getNotificationCount() {
        return notifications;
    }

    /**
     * Was this ErrorHandler free of notification of any errors?
     * 
     * @return <code>false</code> if we did receive notification of validating errors, <code>true</code> otherwise
     */
    public boolean passed() {
        return notifications == 0;
    }

    /**
     * Get all the SAXParseException messages this XMLErrorHandler encountered.
     * 
     * @return all the SAXParseException messages as a string
     */
    public String getMessages() {
        int msgCount = 0;
        final StringBuilder builder = new StringBuilder();
        for (SAXParseException parseException : getFatalErrors()) {
            builder.append(getMessage(parseException, SEVERITY_FATAL_ERROR, ++msgCount));
        }
        for (SAXParseException parseException : getErrors()) {
            builder.append(getMessage(parseException, SEVERITY_ERROR, ++msgCount));
        }
        for (SAXParseException parseException : getWarnings()) {
            builder.append(getMessage(parseException, SEVERITY_WARNING, ++msgCount));
        }
        return builder.toString();
    }

    private static String getMessage(final SAXParseException parseException, final String severity, final int count) {
        final StringBuilder builder = new StringBuilder();
        builder.append(parseException.getClass().getName());
        builder.append(" severity=");
        builder.append(severity);
        builder.append(", count=");
        builder.append(count);
        builder.append("\n\t");
        builder.append(parseException.getMessage());
        builder.append("\n\t");
        builder.append("line=");
        builder.append(parseException.getLineNumber());
        builder.append(", column=");
        builder.append(parseException.getColumnNumber());
        builder.append("\n\t");
        builder.append(parseException.getSystemId());
        builder.append("\n");
        return builder.toString();
    }

}
