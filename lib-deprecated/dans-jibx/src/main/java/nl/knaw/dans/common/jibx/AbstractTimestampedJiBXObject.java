package nl.knaw.dans.common.jibx;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import nl.knaw.dans.common.lang.FastByteArrayOutputStream;
import nl.knaw.dans.common.lang.repo.AbstractTimestampedObject;
import nl.knaw.dans.common.lang.repo.TimestampedMinimalXMLBean;
import nl.knaw.dans.common.lang.xml.XMLBean;

import org.joda.time.DateTime;

public abstract class AbstractTimestampedJiBXObject<T> extends AbstractJiBXObject<T> implements TimestampedMinimalXMLBean, XMLBean {

    private static final long serialVersionUID = 317432367740811300L;

    private DateTime timestamp;

    private boolean dirty;

    private byte[] originalMd5;

    public DateTime getTimestamp() {
        return timestamp;
    }

    public boolean isOlderThan(Object compareDate) throws IllegalArgumentException {
        return AbstractTimestampedObject.compare(getTimestamp(), compareDate) < 0;
    }

    public void setTimestamp(Object timestamp) throws IllegalArgumentException {
        if (timestamp == null) {
            this.timestamp = null;
        } else {
            this.timestamp = new DateTime(timestamp);
        }
    }

    public boolean isDirty() {
        if (dirty)
            return dirty;
        dirty = !Arrays.equals(originalMd5, calcMd5());
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        if (!dirty) {
            originalMd5 = calcMd5();
        }
    }

    public byte[] calcMd5() {
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

        try {
            objstream = new ObjectOutputStream(out);
            objstream.writeObject(this);
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            md5.update(out.getByteArray());
            return md5.digest();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
        finally {
            try {
                // put back original mvalues
                if (saveOriginalMd5 != null) {
                    originalMd5 = Arrays.copyOf(saveOriginalMd5, saveOriginalMd5.length);
                }
                dirty = saveDirty;
                timestamp = saveTimestamp;

                if (objstream != null) {
                    objstream.close();
                }
                out.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected boolean evaluateDirty(Object obj1, Object obj2) {
        boolean dirty = false;
        if (obj2 == null) {
            dirty = obj1 != null;
        } else {
            dirty = !obj2.equals(obj1);
        }
        if (dirty)
            setDirty(true);
        return dirty;
    }

}
