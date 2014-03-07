package nl.knaw.dans.commons.pid;

public interface PidTableProperties
{
    static final String TABLE_NAME = "pid_counter";

    /** Long. Last and generated part of the URN. */
    static final String VALUE_COLUMN_NAME = "pid_last_generated";

    /** '%s' is typically replaced by {@link #URN_PREFIX}+{@link #DANS_PREFIX} */
    static final String WHERE_CLAUSE = " where pid_prefix = '%s'";

    /** Start of the URN / primary key. */
    static final String URN_PREFIX = "urn:nbn:nl:ui:";

    /**
     * Tail of the primary key. Center part of the URN. Used by
     * {@link PidGenerator#PidGenerator(java.sql.Connection)} to call
     * {@link PidGenerator#PidGenerator(java.sql.Connection, String)}
     */
    static final String DANS_PREFIX = "13-";
}
