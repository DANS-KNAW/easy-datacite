package nl.knaw.dans.common.lang.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;
import org.w3c.tidy.TidyMessageListener;

/**
 * Validates and pretty prints Html. HtmlValidator is a simple wrapper around org.w3c.tidy.Tidy.
 * 
 * @author ecco
 */
public class HtmlValidator implements TidyMessageListener
{

    private static final String HEAD_1 = "<!DOCTYPE html PUBLIC " + "\"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
            + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + "<head><title>";
    private static final String HEAD_2 = "</title></head><body>\n";
    private static final String TAIL = "</body></html>";

    private final Tidy tidy;

    private List<TidyMessage> summeryMessages;
    private List<TidyMessage> infoMessages;
    private List<TidyMessage> warningMessages;
    private List<TidyMessage> errorMessages;
    private List<TidyMessage> messages;

    private String title = "";
    private boolean printBodyOnly;

    public HtmlValidator()
    {
        tidy = new Tidy();
        tidy.setMessageListener(this);
        tidy.setBreakBeforeBR(true);
        // tidy.setSmartIndent(true);
        tidy.setForceOutput(true);
        tidy.setQuiet(true);
        resetMessages();
    }

    public void resetMessages()
    {
        summeryMessages = new ArrayList<TidyMessage>();
        infoMessages = new ArrayList<TidyMessage>();
        warningMessages = new ArrayList<TidyMessage>();
        errorMessages = new ArrayList<TidyMessage>();
        messages = new ArrayList<TidyMessage>();
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String tidyPartialHtml(String markupPart)
    {
        return tidyPartialHtml(markupPart, true);
    }

    public String tidyPartialHtml(String markupPart, boolean printBodyOnly)
    {
        String markupFeed = HEAD_1 + title + HEAD_2 + markupPart + TAIL;
        return tidyHtml(markupFeed, printBodyOnly);
    }

    public String tidyHtml(String markup, boolean printBodyOnly)
    {
        resetMessages();
        this.printBodyOnly = printBodyOnly;
        tidy.setPrintBodyOnly(printBodyOnly);
        ByteArrayInputStream in = new ByteArrayInputStream(markup.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        tidy.parse(in, out);
        return out.toString();
    }

    @Override
    public void messageReceived(TidyMessage msg)
    {
        messages.add(msg);
        switch (msg.getLevel().getCode())
        {
        case 0:
            summeryMessages.add(msg);
            break;
        case 1:
            infoMessages.add(msg);
            break;
        case 2:
            warningMessages.add(msg);
            break;
        case 3:
            errorMessages.add(msg);
        default:
            break;
        }

    }

    public boolean hasErrors()
    {
        return errorMessages.size() > 0;
    }

    public boolean hasWarnings()
    {
        return warningMessages.size() > 0;
    }

    public int errorCount()
    {
        return errorMessages.size();
    }

    public int warningCount()
    {
        return warningMessages.size();
    }

    public List<String> getSummeryMessages()
    {
        return convert(summeryMessages);
    }

    public List<String> getInfoMessages()
    {
        return convert(infoMessages);
    }

    public List<String> getWarningMessages()
    {
        return convert(warningMessages);
    }

    public List<String> getErrorMessages()
    {
        return convert(errorMessages);
    }

    public List<String> getMessages()
    {
        return convert(messages);
    }

    public String getLastMessage()
    {
        if (messages.size() == 0)
        {
            return "";
        }
        else
        {
            return messages.get(messages.size() - 1).getMessage();
        }
    }

    private List<String> convert(List<TidyMessage> tidyMsgs)
    {
        List<String> msgs = new ArrayList<String>();
        for (TidyMessage tidyMsg : tidyMsgs)
        {
            String msg = tidyMsg.getLevel() + ": " + tidyMsg.getMessage() + " line=" + (tidyMsg.getLine() - (printBodyOnly ? 1 : 0)) + " column="
                    + tidyMsg.getColumn();
            msgs.add(msg);
        }
        return msgs;
    }

    public void setHideVarious()
    {
        tidy.setHideComments(true);
        tidy.setDropFontTags(true);
        tidy.setDropEmptyParas(true);
        tidy.setDropProprietaryAttributes(true);
        tidy.setTrimEmptyElements(true);
    }
}
