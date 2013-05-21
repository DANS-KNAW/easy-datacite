package nl.knaw.dans.common.wicket.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.wicket.model.Model;

/**
 * Model that persists its content in a text file.
 */
public class TextFileModel extends Model<String>
{
    private static final long serialVersionUID = -736312827071904958L;

    private File file;
    private String content;

    /**
     * Initializes a new <code>TextFileModel</code> object.
     * 
     * @param file
     *        the text file from/to which to read/write
     */
    public TextFileModel(final File file)
    {
        this.file = file;
    }

    protected TextFileModel()
    {
    }

    protected void setFile(File file)
    {
        this.file = file;
    }

    @Override
    public String getObject()
    {
        if (content == null)
        {
            content = readFile();
        }

        return content;
    }

    private String readFile()
    {
        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new FileReader(file));
            final StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
                appendSystemDependentNewline(buffer);
            }

            return buffer.toString();
        }
        catch (final IOException e)
        {
            return throwRuntimeException(e, "read text file");
        }
        finally
        {
            closeReader(reader);
        }
    }

    private static void appendSystemDependentNewline(StringBuilder buffer)
    {
        buffer.append(String.format("%n"));
    }

    private String throwRuntimeException(Throwable t, String action)
    {
        throw new RuntimeException(String.format("Could not %s for %s.  File '%s'; Message: '%s'", action, getClass().getName(), file, t.getMessage()), t);
    }

    private void closeReader(Reader reader)
    {
        if (reader != null)
        {
            try
            {
                reader.close();
            }
            catch (final IOException e)
            {
                throwRuntimeException(e, "close text file");
            }
        }
    }

    @Override
    public void setObject(final String object)
    {
        content = object;

        FileWriter writer = null;
        try
        {
            writer = new FileWriter(file);
            writer.write(content==null?"":content);
        }
        catch (final IOException e)
        {
            throw new RuntimeException(String.format("Could not write text to file for TextFileModel. File: '%s', Message: '%s'", file, e.getMessage()));
        }
        finally
        {
            closeWriter(writer);
        }
    }

    private void closeWriter(Writer writer)
    {
        if (writer != null)
        {
            try
            {
                writer.close();
            }
            catch (final IOException e)
            {
                throwRuntimeException(e, "write text file");
            }
        }
    }
}
