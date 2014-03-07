package nl.knaw.dans.common.lang.repo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import nl.knaw.dans.common.lang.FastByteArrayOutputStream;
import nl.knaw.dans.common.lang.TimestampedObject;

import org.joda.time.DateTime;
import org.joda.time.base.AbstractInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of a TimestampedObject.
 * 
 * @author ecco Sep 27, 2009
 */
public abstract class AbstractTimestampedObject implements TimestampedObject
{

    private static final long serialVersionUID = 6037999236969220251L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTimestampedObject.class);

    private DateTime timestamp;
    private boolean dirty;
    private byte[] originalMd5;

    /**
     * {@inheritDoc}
     */
    public DateTime getTimestamp()
    {
        return timestamp;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isOlderThan(final Object compareDate) throws IllegalArgumentException
    {
        return compare(getTimestamp(), compareDate) < 0;
    }

    /**
     * {@inheritDoc}
     */
    public void setTimestamp(final Object timestamp) throws IllegalArgumentException
    {
        if (timestamp == null)
        {
            this.timestamp = null;
        }
        else
        {
            this.timestamp = new DateTime(timestamp);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        if (dirty)
            return dirty;
        dirty = !Arrays.equals(originalMd5, calcMd5());
        return dirty;
    }

    /**
     * {@inheritDoc}
     */
    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
        if (!dirty)
        {
            originalMd5 = calcMd5();
        }
    }

    public byte[] calcMd5()
    {
        FastByteArrayOutputStream out = new FastByteArrayOutputStream(8 * 1024);
        ObjectOutputStream objstream = null;

        // save original values of stuph we don't want to include in
        // the calculation of the md5 hash.
        byte[] saveOriginalMd5 = null;
        if (originalMd5 != null)
            saveOriginalMd5 = Arrays.copyOf(originalMd5, originalMd5.length);
        originalMd5 = null;
        boolean saveDirty = dirty;
        dirty = false;
        DateTime saveTimestamp = timestamp;
        timestamp = null;

        try
        {
            objstream = new ObjectOutputStream(out);
            objstream.writeObject(this);
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            md5.update(out.getByteArray());
            return md5.digest();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
        finally
        {
            try
            {
                // put back original mvalues 
                if (saveOriginalMd5 != null)
                {
                    originalMd5 = Arrays.copyOf(saveOriginalMd5, saveOriginalMd5.length);
                }
                dirty = saveDirty;
                timestamp = saveTimestamp;

                if (objstream != null)
                {
                    objstream.close();
                }
                out.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Evaluate if this TimestampedObject needs to be marked as changed in such a way that it is, or is not consistent
     * with it's persisted state anymore, given an old attribute value and the new attribute value. This method can be
     * used on setter-methods of classes that extend {@link AbstractTimestampedObject}. This method automatically sets
     * the dirty flag.
     * 
     * @param value1
     *        value of attribute
     * @param value2
     *        value of attribute
     * @return <code>true</code> if value1 is different from value2, <code>false</code> otherwise
     */
    protected boolean evaluateDirty(final Object value1, final Object value2)
    {
        boolean dirty = false;
        if (value2 == null)
        {
            dirty = value1 != null;
        }
        else
        {
            dirty = !value2.equals(value1);
        }
        if (dirty)
        {
            setDirty(true);
        }
        return dirty;
    }

    /**
     * Compare date with compareDate.
     * 
     * @param date
     *        one of the objects recognized in <a
     *        href="http://joda-time.sourceforge.net/api-release/org/joda/time/convert/ConverterManager.html"
     *        >ConverterManager</a>
     * @param compareDate
     *        one of the objects recognized in <a
     *        href="http://joda-time.sourceforge.net/api-release/org/joda/time/convert/ConverterManager.html"
     *        >ConverterManager</a>
     * @return negative value if date is less, 0 if equal, or positive value if greater. 0 if one of the parameters is
     *         null.
     * @throws IllegalArgumentException
     *         if date or compareDate could not be converted.
     */
    public static int compare(final Object date, final Object compareDate) throws IllegalArgumentException
    {
        AbstractInstant dateTime;
        AbstractInstant compareDateTime;
        if (date == null)
        {
            LOGGER.warn("Could not determine ancienity: date is null.");
            return 0;
        }
        else if (compareDate == null)
        {
            LOGGER.warn("Could not determine ancienity: compareDate is null.");
            return 0;
        }

        if (date instanceof AbstractInstant)
        {
            dateTime = (AbstractInstant) date;
        }
        else
        {
            dateTime = new DateTime(date);
        }

        if (compareDate instanceof AbstractInstant)
        {
            compareDateTime = (AbstractInstant) compareDate;
        }
        else
        {
            compareDateTime = new DateTime(compareDate);
        }
        return dateTime.compareTo(compareDateTime);
    }

}
