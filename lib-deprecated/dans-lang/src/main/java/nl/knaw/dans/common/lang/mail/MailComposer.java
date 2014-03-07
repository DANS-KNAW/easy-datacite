package nl.knaw.dans.common.lang.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailComposer
{

    public static final char PLACEHOLDER_START_OR_END = '~';

    private static Logger logger = LoggerFactory.getLogger(MailComposer.class);

    private Map<String, Object> objectMap = new HashMap<String, Object>();

    public MailComposer(Object... objects)
    {
        for (Object obj : objects)
        {
            mapImplementingClass(obj);
            mapInterfaces(obj);
        }
    }

    private void mapInterfaces(Object obj)
    {
        if (obj != null)
        {
            Class superClass = obj.getClass();
            while (superClass != null)
            {
                Class[] interfaces = superClass.getInterfaces();
                for (Class interf : interfaces)
                {
                    String key = interf.getSimpleName();
                    objectMap.put(key, obj);
                }
                superClass = superClass.getSuperclass();
            }
        }
    }

    private void mapImplementingClass(Object obj)
    {
        if (obj != null)
        {
            String key = obj.getClass().getSimpleName();
            objectMap.put(key, obj);
        }
    }

    public String compose(InputStream inStream) throws MailComposerException
    {
        return compose(inStream, false);
    }

    public String compose(String content) throws MailComposerException
    {
        return compose(content, false);
    }

    public String compose(InputStream inStream, boolean htmlForLineBreak) throws MailComposerException
    {
        if (inStream == null)
        {
            throw new IllegalArgumentException("InputStream cannot be null!");
        }
        String message = null;
        try
        {
            message = composeMessage(inStream, htmlForLineBreak);

        }
        catch (UnsupportedEncodingException e)
        {
            throw new MailComposerException(e);
        }
        catch (IOException e)
        {
            throw new MailComposerException(e);
        }
        return message;
    }

    public String compose(String content, boolean htmlForLineBreak) throws MailComposerException
    {
        if (content == null)
        {
            throw new IllegalArgumentException("InputStream cannot be null!");
        }
        String message = null;
        try
        {
            message = composeMessage(content, htmlForLineBreak);

        }
        catch (UnsupportedEncodingException e)
        {
            throw new MailComposerException(e);
        }
        catch (IOException e)
        {
            throw new MailComposerException(e);
        }
        return message;
    }

    public String composeMessage(InputStream inStream, boolean htmlForLineBreak) throws IOException, MailComposerException
    {
        if (inStream == null)
        {
            throw new MailComposerException("Cannot compose mail from null InputStream.");
        }
        BufferedReader reader = null;
        StringBuilder message = new StringBuilder();
        StringBuilder placeHolder = new StringBuilder();
        boolean readMessage = true;
        try
        {
            reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            int c;
            while ((c = reader.read()) != -1)
            {
                char ch = (char) c;
                if (PLACEHOLDER_START_OR_END == ch)
                {
                    if (!readMessage)
                    {
                        String value = convert(getValue(placeHolder.toString()), htmlForLineBreak);
                        message.append(value);
                        placeHolder.delete(0, placeHolder.length());
                    }
                    readMessage = !readMessage;
                }
                else
                {
                    if (readMessage)
                    {
                        message.append(ch);
                    }
                    else
                    {
                        placeHolder.append(ch);
                    }
                }
            }
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        return message.toString();
    }

    public String composeMessage(String content, boolean htmlForLineBreak) throws IOException, MailComposerException
    {
        if (content == null)
        {
            throw new MailComposerException("Cannot compose mail from null String.");
        }
        StringReader reader = null;
        StringBuilder message = new StringBuilder();
        StringBuilder placeHolder = new StringBuilder();
        boolean readMessage = true;
        try
        {
            reader = new StringReader(content);
            int c;
            while ((c = reader.read()) != -1)
            {
                char ch = (char) c;
                if (PLACEHOLDER_START_OR_END == ch)
                {
                    if (!readMessage)
                    {
                        String value = convert(getValue(placeHolder.toString()), htmlForLineBreak);
                        message.append(value);
                        placeHolder.delete(0, placeHolder.length());
                    }
                    readMessage = !readMessage;
                }
                else
                {
                    if (readMessage)
                    {
                        message.append(ch);
                    }
                    else
                    {
                        placeHolder.append(ch);
                    }
                }
            }
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
        return message.toString();
    }

    private String convert(String value, boolean htmlForLineBreak)
    {
        if (value != null && htmlForLineBreak)
        {
            String retVal = value.replaceAll("\n", "<br/>");
            return retVal;
        }
        else
        {
            return value;
        }
    }

    String getValue(String placeHolder) throws MailComposerException
    {
        String[] splitted = placeHolder.split("\\.");
        String value = "";
        Object obj = objectMap.get(splitted[0]);
        if (obj == null)
        {
            logger.debug("No such object: " + splitted[0]);
            throw new MailComposerException("No such object: " + splitted[0]);
        }
        try
        {
            Method method = obj.getClass().getMethod(splitted[1]);
            Object returned = method.invoke(obj);
            value += returned == null ? "" : returned;
        }
        catch (SecurityException e)
        {
            throw new MailComposerException(e);
        }
        catch (NoSuchMethodException e)
        {
            logger.debug("No such method: " + placeHolder);
            throw new MailComposerException("No such method: " + placeHolder, e);
        }
        catch (IllegalArgumentException e)
        {
            throw new MailComposerException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new MailComposerException(e);
        }
        catch (InvocationTargetException e)
        {
            //throw new MailComposerException(e); // GK: don't throw this!
        }

        return value;
    }

}
