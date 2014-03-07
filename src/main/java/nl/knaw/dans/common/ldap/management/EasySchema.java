package nl.knaw.dans.common.ldap.management;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

public class EasySchema extends AbstractSchema
{

    public static final String SCHEMA_NAME = "easy";

    private final List<Attributes> attributeTypes;
    private final List<Attributes> objectClasses;

    public EasySchema()
    {
        attributeTypes = new ArrayList<Attributes>();

        Attributes attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.3");
        attrs.put("NAME", "easyAcceptConditionsOfUse");
        attrs.put("DESC", "accepts the Conditions of Use of DANS-EASY");
        attrs.put("EQUALITY", "booleanMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.7"); // Boolean syntax
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.4");
        attrs.put("NAME", "easyRoles");
        attrs.put("DESC", "roles of an easyUser");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.5");
        attrs.put("NAME", "easyGroups");
        attrs.put("DESC", "groups an easyUser is part of");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.6");
        attrs.put("NAME", "easyFunction");
        attrs.put("DESC", "function of an easyUser");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.14");
        attrs.put("NAME", "easyDiscipline1");
        attrs.put("DESC", "primary discipline of an easyUser");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.15");
        attrs.put("NAME", "easyDiscipline2");
        attrs.put("DESC", "secondary discipline of an easyUser");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.16");
        attrs.put("NAME", "easyDiscipline3");
        attrs.put("DESC", "tertiary discipline of an easyUser");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.17");
        attrs.put("NAME", "easyHasConfirmedGeneralConditions");
        attrs.put("DESC", "User has confirmed the general conditions of use already");
        attrs.put("EQUALITY", "booleanMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.7"); // Boolean syntax
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.8");
        attrs.put("NAME", "easyDai");
        attrs.put("DESC", "Digitial Author Identifier of an easyUser");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.9");
        attrs.put("NAME", "easyOrganizationAddress");
        attrs.put("DESC", "Address of the organization an easyUser is affiliated with");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.10");
        attrs.put("NAME", "easyOrganizationCity");
        attrs.put("DESC", "City of the organization an easyUser is affiliated with");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.11");
        attrs.put("NAME", "easyOrganizationCountry");
        attrs.put("DESC", "Country of the organization an easyUser is affiliated with");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.12");
        attrs.put("NAME", "easyOrganizationTelephone");
        attrs.put("DESC", "Telephone number of an easyUser at the organization he is affiliated with");
        attrs.put("EQUALITY", "caseIgnoreMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.15"); // DirectoryString
        attributeTypes.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.1.13");
        attrs.put("NAME", "easyLogMyActions");
        attrs.put("DESC", "Allow actions to be displayed in log");
        attrs.put("EQUALITY", "booleanMatch");
        attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.7"); // Boolean syntax
        attrs.put("SINGLE-VALUE", "TRUE");
        attributeTypes.add(attrs);

        // objectClasses
        objectClasses = new ArrayList<Attributes>();

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.2.1");
        attrs.put("NAME", "easyUser");
        attrs.put("DESC", "An entry which represents a user of the easy application");
        attrs.put("SUP", "dansUser");
        attrs.put("STRUCTURAL", "true");
        Attribute must = new BasicAttribute("MUST", "uid");
        must.add("objectclass");
        attrs.put(must);
        Attribute may = new BasicAttribute("MAY");
        may.add("easyAcceptConditionsOfUse");
        may.add("easyRoles");
        may.add("easyGroups");
        may.add("easyFunction");
        may.add("easyDiscipline1");
        may.add("easyDiscipline2");
        may.add("easyDiscipline3");
        may.add("easyDai");
        may.add("easyOrganizationAddress");
        may.add("easyOrganizationCity");
        may.add("easyOrganizationCountry");
        may.add("easyOrganizationTelephone");
        may.add("easyLogMyActions");
        may.add("easyHasConfirmedGeneralConditions");

        attrs.put(may);
        objectClasses.add(attrs);

        attrs = new BasicAttributes(true);
        attrs.put("NUMERICOID", "1.3.6.1.4.1.33188.1.2.2");
        attrs.put("NAME", "easyGroup");
        attrs.put("DESC", "An entry which represents a group in the easy application");
        attrs.put("SUP", "organizationalUnit");
        attrs.put("STRUCTURAL", "true");
        must = new BasicAttribute("MUST", "ou");
        must.add("objectclass");
        attrs.put(must);
        may = new BasicAttribute("MAY");
        may.add("dansState");
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
