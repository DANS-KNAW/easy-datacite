package nl.knaw.dans.common.ldap.management;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

public class DANSSchema extends AbstractSchema
{

    public static final String SCHEMA_NAME = "dans";

    private final List<Attributes> attributeTypes;
    private final List<Attributes> objectClasses;

    public DANSSchema()
    {
        // attributetypes
        attributeTypes = new ArrayList<Attributes>();

        Attributes attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.1");
        attrs.put("NAME", "dansState");
        attrs.put("DESC", "The state of an entity");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.2");
        attrs.put("NAME", "dansPrefixes");
        attrs.put("DESC", "prefixes in a persons name");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.3");
        attrs.put("NAME", "dansAcceptConditionsOfUse");
        attrs.put("DESC", "accepts the Conditions of Use");
        attrs.put("EQUALITY", "booleanMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.7"); // Boolean syntax
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.4");
        attrs.put("NAME", "dansLastLogin");
        attrs.put("DESC", "date time of last login");
        attrs.put("EQUALITY", "generalizedTimeMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.24"); // Generalized Time (yyyyMMddHHmmssZ)
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.5");
        attrs.put("NAME", "dansStoreId");
        attrs.put("DESC", "A system identifier");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.6");
        attrs.put("NAME", "dansPreviousId");
        attrs.put("DESC", "A previous system identifier");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.7");
        attrs.put("NAME", "dansPid");
        attrs.put("DESC", "A persistent identifier");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.8");
        attrs.put("NAME", "dansMigrationDate");
        attrs.put("DESC", "date time of migration");
        attrs.put("EQUALITY", "generalizedTimeMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.24"); // Generalized Time (yyyyMMddHHmmssZ)
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.9");
        attrs.put("NAME", "dansNewsletter");
        attrs.put("DESC", "opts for newsletter");
        attrs.put("EQUALITY", "booleanMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.7"); // Boolean syntax
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.10");
        attrs.put("NAME", "dansAltTel");
        attrs.put("DESC", "Alternative telephone number");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        // Federative user mapping, federative user id
        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.11");
        attrs.put("NAME", "fedUserId");
        attrs.put("DESC", "A federative user identifier");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        // Federative user mapping, dans user id
        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.12");
        attrs.put("NAME", "dansUserId");
        attrs.put("DESC", "A dans user identifier for the mapping from a federative user id");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        // A test attribute
        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.1.13");
        attrs.put("NAME", "dansTestAttr");
        attrs.put("DESC", "A dans test attribute");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        // objectClasses
        objectClasses = new ArrayList<Attributes>();

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.2.1");
        attrs.put("NAME", "dansUser");
        attrs.put("DESC", "An entry which represents a user of one of the dans applications");
        attrs.put("SUP", "inetOrgPerson");
        attrs.put("STRUCTURAL", "true");
        Attribute must = new BasicAttribute("MUST", "uid");
        must.add("objectclass");
        attrs.put(must);
        Attribute may = new BasicAttribute("MAY");
        may.add("dansState");
        may.add("dansPrefixes");
        may.add("dansAcceptConditionsOfUse");
        may.add("dansNewsletter");
        may.add("dansLastLogin");
        may.add("dansAltTel");
        attrs.put(may);
        objectClasses.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.2.2");
        attrs.put("NAME", "dansIdMap");
        attrs.put("DESC", "An entry which represents a collection of identifiers");
        attrs.put("SUP", "top");
        attrs.put("STRUCTURAL", "true");
        must = new BasicAttribute("MUST", "dansStoreId");
        must.add("objectclass");
        attrs.put(must);
        may = new BasicAttribute("MAY");
        may.add("dansPreviousId");
        may.add("dansPid");
        may.add("dansMigrationDate");
        attrs.put(may);
        objectClasses.add(attrs);

        // Federative user mapping
        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.0.2.3");
        attrs.put("NAME", "dansFedIdMap");
        attrs.put("DESC", "An entry which represents a mapping from an Fedarative user Id to a Dans user Id");
        attrs.put("SUP", "top");
        attrs.put("STRUCTURAL", "true");
        must = new BasicAttribute("MUST", "fedUserId");
        must.add("objectclass");
        attrs.put(must);
        may = new BasicAttribute("MAY");
        may.add("dansUserId");
        attrs.put(may);
        objectClasses.add(attrs);

    }

    public List<Attributes> getAttributeTypes()
    {
        return attributeTypes;
    }

    public List<Attributes> getObjectClasses()
    {
        return objectClasses;
    }

    @Override
    public String getSchemaName()
    {
        return SCHEMA_NAME;
    }

}
