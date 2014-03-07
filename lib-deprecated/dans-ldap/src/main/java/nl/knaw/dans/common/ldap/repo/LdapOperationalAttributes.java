package nl.knaw.dans.common.ldap.repo;

import nl.knaw.dans.common.lang.ldap.DateTimeTranslator;
import nl.knaw.dans.common.lang.ldap.OperationalAttributes;

import org.joda.time.DateTime;

public class LdapOperationalAttributes implements OperationalAttributes
{

    private static DateTimeTranslator TRANSLATOR = new DateTimeTranslator();

    private DateTime createTimestamp;

    private DateTime modifyTimestamp;

    protected LdapOperationalAttributes()
    {

    }

    public DateTime getCreateTimestamp()
    {
        return createTimestamp;
    }

    public DateTime getModifyTimestamp()
    {
        return modifyTimestamp;
    }

    protected void setCreateTime(String creationTime)
    {
        createTimestamp = parseDateTime(creationTime);
    }

    protected void setModifyTime(String modifyTime)
    {
        modifyTimestamp = parseDateTime(modifyTime);
    }

    protected static DateTime parseDateTime(String s)
    {
        return TRANSLATOR.fromLdap(s);
    }

}
