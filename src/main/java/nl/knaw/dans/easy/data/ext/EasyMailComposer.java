package nl.knaw.dans.easy.data.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.mail.MailComposer;
import nl.knaw.dans.common.lang.mail.MailComposerException;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;

public class EasyMailComposer extends MailComposer
{

    public static final String LOCATION_TEXT_HEADER = "/mail/templates/default/header.txt";
    public static final String LOCATION_TEXT_FOOTER = "/mail/templates/default/footer.txt";
    public static final String LOCATION_HTML_HEADER = "/mail/templates/default/header.html";
    public static final String LOCATION_HTML_FOOTER = "/mail/templates/default/footer.html";

    private static String TEXT_HEADER;
    private static String TEXT_FOOTER;

    private static String HTML_HEADER;
    private static String HTML_FOOTER;

    public static String getTextHeader()
    {
        if (TEXT_HEADER == null)
        {
            TEXT_HEADER = read(LOCATION_TEXT_HEADER);
        }
        return TEXT_HEADER;
    }

    public static String getTextFooter()
    {
        if (TEXT_FOOTER == null)
        {
            TEXT_FOOTER = read(LOCATION_TEXT_FOOTER);
        }
        return TEXT_FOOTER;
    }

    public static String getHtmlHeader()
    {
        if (HTML_HEADER == null)
        {
            HTML_HEADER = read(LOCATION_HTML_HEADER);
        }
        return HTML_HEADER;
    }

    public static String getHtmlFooter()
    {
        if (HTML_FOOTER == null)
        {
            HTML_FOOTER = read(LOCATION_HTML_FOOTER);
        }
        return HTML_FOOTER;
    }

    private static String read(String location)
    {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(ResourceLocator.getInputStream(location)));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append("\r\n");
            }
        }
        catch (IOException e)
        {
            throw new ApplicationException(e);
        }
        catch (ResourceNotFoundException e)
        {
            throw new ApplicationException(e);
        }
        finally
        {
            closeReader(reader);
        }
        return sb.toString();
    }

    private static void closeReader(BufferedReader reader)
    {
        if (reader != null)
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                throw new ApplicationException(e);
            }
        }
    }

    /**
     * @param placeholderSuppliers
     *        only one instance per class supported
     */
    public EasyMailComposer(Object... placeholderSuppliers)
    {
        super(placeholderSuppliers);
    }

    public String composeText(String templateLocation) throws MailComposerException
    {
        return getTextHeader() + getMessageBody(templateLocation, false) + getTextFooter();
    }

    public String composeHtml(String templateLocation) throws MailComposerException
    {
        return getHtmlHeader() + getMessageBody(templateLocation, true) + getHtmlFooter();
    }

    public String getMessageBody(String templateLocation, boolean htmlForLineBreak) throws MailComposerException
    {
        String mailText = null;
        InputStream inStream = null;
        try
        {
            inStream = ResourceLocator.getInputStream(templateLocation);
            mailText = compose(inStream, htmlForLineBreak);
        }
        catch (IOException e)
        {
            throw new MailComposerException(e);
        }
        catch (ResourceNotFoundException e)
        {
            throw new MailComposerException(e);
        }
        catch (MailComposerException e)
        {
            throw new MailComposerException(e);
        }
        finally
        {
            closeInStream(inStream);
        }
        return mailText;
    }

    private void closeInStream(InputStream inStream) throws MailComposerException
    {
        if (inStream != null)
        {
            try
            {
                inStream.close();
            }
            catch (IOException e)
            {
                throw new MailComposerException(e);
            }
        }
    }

}
