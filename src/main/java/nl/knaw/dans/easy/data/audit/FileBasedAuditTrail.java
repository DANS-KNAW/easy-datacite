package nl.knaw.dans.easy.data.audit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBasedAuditTrail implements AuditTrail
{
    
    public static final long MAX_LENGTH = 5000000;
    
    private static final Logger logger = LoggerFactory.getLogger(FileBasedAuditTrail.class);
    
    private final String auditFilename;
    private final File auditDirectory;
    
    private RandomAccessFile raf;
    private String currentFileName;
    
    public FileBasedAuditTrail(String location) throws IOException
    {
        File auditFile = new File(location);
        auditFilename = auditFile.getName();
        auditDirectory = auditFile.getParentFile();
        if (!auditDirectory.exists() && !auditDirectory.mkdirs())
        {
            throw new IOException("Could not create audit directory: " + auditDirectory.getPath());
        }
    }

    @Override
    public void store(AuditRecord<?> auditRecord)
    {
        try
        {
            getRaf().writeBytes(auditRecord.getRecord() + "\n");
        }
        catch (IOException e)
        {
            logger.error("Could not write audit record: ", e);
        }
        
    }
    
    private RandomAccessFile getRaf() throws IOException
    {
        if (raf == null)
        {
            createRaf();
        }
        else if (raf.length() > MAX_LENGTH)
        {
            close();
            createRaf();
        }
        return raf;
    }

    private void createRaf() throws FileNotFoundException
    {
        currentFileName = auditFilename + "_" + new DateTime().toString("yyyy-MM-dd_HH.mm.ss") + ".csv";
        raf = new RandomAccessFile(new File(auditDirectory, currentFileName), "rw");
        logger.info("Created audit file " + currentFileName);
    }

    @Override
    public void close()
    {
        if (raf != null)
        {
            try
            {
                raf.close();
                raf = null;
                logger.info("Closed audit file " + currentFileName);
            }
            catch (IOException e)
            {
                logger.error("Could not close audit file: ", e);
            }
        }
        else
        {
            logger.info("Nothing to close: no audit file was open.");
        }
        
    }

}
