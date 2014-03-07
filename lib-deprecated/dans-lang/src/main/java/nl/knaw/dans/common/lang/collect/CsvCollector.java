package nl.knaw.dans.common.lang.collect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.progress.ProgressSubject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvCollector extends ProgressSubject implements Collector<List<List<String>>>
{

    public static final String DEFAULT_SEPARATOR = ";";
    private static final Logger logger = LoggerFactory.getLogger(CsvCollector.class);

    private final File file;
    private String separator;

    public CsvCollector(File file)
    {
        this.file = file;
    }

    public CsvCollector(String filename)
    {
        this(new File(filename));
    }

    public String getSeparator()
    {
        if (separator == null)
        {
            separator = DEFAULT_SEPARATOR;
        }
        return separator;
    }

    public void setSeparator(String separator)
    {
        this.separator = separator;
    }

    @Override
    public List<List<String>> collect() throws CollectorException
    {
        onStartProcess();
        List<List<String>> entries = new ArrayList<List<String>>();
        try
        {
            read(entries, file);
        }
        catch (IOException e)
        {
            throw new CollectorException(e);
        }
        onEndProcess();
        return entries;
    }

    private void read(List<List<String>> entries, File file2read) throws IOException
    {
        RandomAccessFile raf = null;
        try
        {
            raf = new RandomAccessFile(file2read, "r");
            String entry;
            while ((entry = raf.readLine()) != null)
            {
                String[] items = entry.split(getSeparator());
                List<String> line = Arrays.asList(items);
                entries.add(line);
            }
        }
        catch (FileNotFoundException e)
        {
            logger.error("Could not open " + file2read.getPath());
            throw e;
        }
        finally
        {
            if (raf != null)
            {
                raf.close();
            }
        }

    }

}
