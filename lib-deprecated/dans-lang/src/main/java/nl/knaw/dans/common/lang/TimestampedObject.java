package nl.knaw.dans.common.lang;

import java.io.Serializable;

import org.joda.time.DateTime;

/**
 * Object with a timestamp.
 * 
 * @author ecco Sep 24, 2009
 */
public interface TimestampedObject extends Serializable
{

    /**
     * Get the timestamp of this TimestampedObject - can be <code>null</code>.
     * 
     * @return timestamp
     */
    DateTime getTimestamp();

    /**
     * Set timestamp on this TimestampedObject.
     * 
     * @see DateTime#DateTime(Object)
     * @param timestamp
     *        one of the objects recognized in <a
     *        href="http://joda-time.sourceforge.net/api-release/org/joda/time/convert/ConverterManager.html"
     *        >ConverterManager</a>
     */
    void setTimestamp(Object timestamp);

    /**
     * Compare the timestamp of this TimestampedObject to the given compareDate. The contract for <code>null</code>
     * values of this method is:
     * <ul>
     * <li>this.timestamp == <code>null</code> and compareDate == <code>null</code>: returns <code>false</code></li>
     * <li>this.timestamp == <code>null</code> and compareDate != <code>null</code>: returns <code>false</code></li>
     * <li>this.timestamp != <code>null</code> and compareDate == <code>null</code>: returns <code>false</code></li>
     * </ul>
     * 
     * @see DateTime#DateTime(Object)
     * @param compareDate
     *        one of the objects recognized in <a
     *        href="http://joda-time.sourceforge.net/api-release/org/joda/time/convert/ConverterManager.html"
     *        >ConverterManager</a>
     * @return <code>true</code> if this timestamp is older than compareDate, <code>false</code> otherwise
     */
    boolean isOlderThan(Object compareDate);

    /**
     * Tells whether this TimestampedObject has changed in such a way that it's persisted state needs to be updated.
     * 
     * @return <code>true</code> if dirty, <code>false</code> otherwise
     */
    boolean isDirty();

    /**
     * Mark this TimestampedObject as changed in such a way that it is, or is not consistent with it's persisted state.
     * 
     * @param dirty
     *        <code>true</code> if it is not consistent with it's persisted state, <code>false</code> otherwise
     */
    void setDirty(boolean dirty);

}
