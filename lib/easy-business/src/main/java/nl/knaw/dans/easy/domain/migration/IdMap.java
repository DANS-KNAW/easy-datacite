package nl.knaw.dans.easy.domain.migration;

import nl.knaw.dans.common.lang.annotations.ldap.LdapAttribute;
import nl.knaw.dans.common.lang.annotations.ldap.LdapObject;
import nl.knaw.dans.common.lang.ldap.DateTimeTranslator;
import nl.knaw.dans.common.lang.user.RepoEntry;

import org.joda.time.DateTime;

@LdapObject(objectClasses = {"dansIdMap"})
public class IdMap implements RepoEntry {

    private static final long serialVersionUID = 1804205773574794833L;

    @LdapAttribute(id = "dansStoreId")
    private String storeId;

    @LdapAttribute(id = "dansPreviousId")
    private String aipId;

    @LdapAttribute(id = "dansPid")
    private String persistentIdentifier;

    @LdapAttribute(id = "dansMigrationDate", valueTranslator = DateTimeTranslator.class)
    private DateTime migrationDate;

    public IdMap() {

    }

    public IdMap(String storeId, String aipId, String persistentIdentifier) {
        this(storeId, aipId, persistentIdentifier, new DateTime());
    }

    public IdMap(String storeId, String aipId, String persistentIdentifier, DateTime migrationDate) {
        this.storeId = storeId;
        this.aipId = aipId;
        this.persistentIdentifier = persistentIdentifier;
        this.migrationDate = migrationDate;
    }

    @Override
    public String getId() {
        return storeId;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getAipId() {
        return aipId;
    }

    public String getPersistentIdentifier() {
        return persistentIdentifier;
    }

    public DateTime getMigrationDate() {
        return migrationDate;
    }

}
