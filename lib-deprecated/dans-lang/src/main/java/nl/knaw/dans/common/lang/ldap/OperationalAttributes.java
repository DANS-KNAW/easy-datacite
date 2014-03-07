package nl.knaw.dans.common.lang.ldap;

import org.joda.time.DateTime;

public interface OperationalAttributes
{

    DateTime getCreateTimestamp();

    DateTime getModifyTimestamp();

}
