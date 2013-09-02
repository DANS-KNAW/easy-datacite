package nl.knaw.dans.common.ldap.management;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

public class DCCDSchema extends AbstractSchema
{

    public static final String SCHEMA_NAME = "dccd";

    private final List<Attributes> attributeTypes;
    private final List<Attributes> objectClasses;

    public DCCDSchema()
    {
        // attributetypes
        attributeTypes = new ArrayList<Attributes>();

        Attributes attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.2.1.4");
        attrs.put("NAME", "dccdRoles");
        attrs.put("DESC", "roles of a dccdUser");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.2.1.5");
        attrs.put("NAME", "dccdDAI");
        attrs.put("DESC", "Digital Author Identifier");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        // objectClasses
        objectClasses = new ArrayList<Attributes>();

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.2.2.1");
        attrs.put("NAME", "dccdUser");
        attrs.put("DESC", "An entry which represents a user of DCCD");
        attrs.put("SUP", "dansUser");// "inetOrgPerson");
        attrs.put("STRUCTURAL", "true");
        Attribute must = new BasicAttribute("MUST", "uid");
        must.add("objectclass");
        attrs.put(must);
        Attribute may = new BasicAttribute("MAY");
        may.add("dccdRoles");
        may.add("dccdDAI");
        attrs.put(may);
        objectClasses.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.2.2.2");
        attrs.put("NAME", "dccdUserOrganisation");
        attrs.put("DESC", "An entry which represents an organisation in the dccd application");
        attrs.put("SUP", "organizationalUnit");
        attrs.put("STRUCTURAL", "true");
        must = new BasicAttribute("MUST", "ou"); // was "cn"
        must.add("objectclass");
        attrs.put(must);
        may = new BasicAttribute("MAY");
        may.add("dansState");
        may.add("description");
        may.add("postalAddress");
        may.add("postalCode");
        may.add("l");
        may.add("st");
        may.add("uniqueMember");
        attrs.put(may);
        objectClasses.add(attrs);
    }

    @Override
    public String getSchemaName()
    {
        return SCHEMA_NAME;
    }

    @Override
    public List<Attributes> getAttributeTypes()
    {
        return attributeTypes;
    }

    @Override
    public List<Attributes> getObjectClasses()
    {
        return objectClasses;
    }

}
