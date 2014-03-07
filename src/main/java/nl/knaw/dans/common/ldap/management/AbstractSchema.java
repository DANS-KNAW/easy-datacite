package nl.knaw.dans.common.ldap.management;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public abstract class AbstractSchema
{

    public abstract String getSchemaName();

    public abstract List<Attributes> getAttributeTypes();

    public abstract List<Attributes> getObjectClasses();

    public void exportForOpenLdap() throws IOException, NamingException
    {
        String filename = "schema/" + getSchemaName() + ".schema";
        File file = new File(filename);
        file.delete();
        RandomAccessFile ram = null;
        try
        {
            ram = new RandomAccessFile(filename, "rw");
            ram.writeBytes("# " + getSchemaName() + " schema\n");
            ram.writeBytes(listAttributesForOpenLdap());
            ram.writeBytes(listObjectsForOpenLdap());
        }
        finally
        {
            if (ram != null)
            {
                ram.close();
            }
        }
    }

    public String listAttributesForOpenLdap() throws NamingException
    {
        StringBuilder sb = new StringBuilder();
        for (Attributes attrs : getAttributeTypes())
        {
            sb.append(printAttributeTypeForOpenLdap(attrs));
        }
        return sb.toString();
    }

    public String listObjectsForOpenLdap() throws NamingException
    {
        StringBuilder sb = new StringBuilder();
        for (Attributes attrs : getObjectClasses())
        {
            sb.append(printObjectForOpenLdap(attrs));
        }
        return sb.toString();
    }

    // @formatter:off
    /* 
    
    attributetype ( 1.3.6.1.4.1.18060.0.4.1.2.21 
        NAME 'fullyQualifiedJavaClassName' 
        DESC 'The fully qualified name for a (Java) class' 
        EQUALITY caseExactIA5Match 
        SYNTAX 1.3.6.1.4.1.1466.115.121.1.26 
        SINGLE-VALUE)
        
     */
    // @formatter:on
    /*
     * Notice: for openldap the closing bracket *cannot* be on a new line. each definition *must* be
     * followed by a blank line.
     */

    public String printAttributeTypeForOpenLdap(Attributes attrs) throws NamingException
    {
        StringBuilder sb = new StringBuilder().append("\n").append("attributetype ( ").append(attrs.get("NUMERICOID").get()).append("\n\t")

        .append("NAME '" + attrs.get("NAME").get() + "'").append("\n\t")

        .append("DESC '" + attrs.get("DESC").get() + "'").append("\n\t")

        .append("EQUALITY " + attrs.get("EQUALITY").get()).append("\n\t")

        .append("SYNTAX " + attrs.get("SYNTAX").get());
        if (attrs.get("SINGLE-VALUE") != null && "true".equalsIgnoreCase((String) attrs.get("SINGLE-VALUE").get()))
        {
            sb.append("\n\t");
            sb.append("SINGLE-VALUE");
        }

        sb.append(" )\n");
        return sb.toString();
    }

    // @formatter:off
    /*
    objectclass ( 1.3.6.1.4.1.42.2.27.4.2.4
    NAME 'javaObject'
    DESC 'Java object representation'
    SUP top
    ABSTRACT
    MUST javaClassName
    MAY ( javaClassNames $ javaCodebase $
            javaDoc $ description ) )
     */
    // @formatter:on

    public String printObjectForOpenLdap(Attributes attrs) throws NamingException
    {
        StringBuilder sb = new StringBuilder().append("\n").append("objectclass ( ").append(attrs.get("NUMERICOID").get())

        .append("\n\t").append("NAME '" + attrs.get("NAME").get() + "'")

        .append("\n\t").append("DESC '" + attrs.get("DESC").get() + "'")

        .append("\n\t").append("SUP " + attrs.get("SUP").get());

        if (attrs.get("STRUCTURAL") != null && "true".equalsIgnoreCase((String) attrs.get("STRUCTURAL").get()))
        {
            sb.append("\n\t");
            sb.append("STRUCTURAL");
        }

        if (attrs.get("ABSTRACT") != null && "true".equalsIgnoreCase((String) attrs.get("ABSTRACT").get()))
        {
            sb.append("\n\t");
            sb.append("ABSTRACT");
        }

        Attribute must = (Attribute) attrs.get("MUST");
        if (must != null)
        {
            sb.append("\n\t");
            sb.append("MUST ( ");
            NamingEnumeration<?> nenum = must.getAll();
            while (nenum.hasMoreElements())
            {
                sb.append(nenum.next());
                sb.append(" $ ");
            }
            sb.delete(sb.length() - 3, sb.length());
            sb.append(" )");
        }

        Attribute may = (Attribute) attrs.get("MAY");
        if (may != null)
        {
            sb.append("\n\t");
            sb.append("MAY ( ");
            NamingEnumeration<?> nenum = may.getAll();
            while (nenum.hasMoreElements())
            {
                sb.append(nenum.next());
                sb.append(" $ ");
            }
            sb.delete(sb.length() - 3, sb.length());
            sb.append(" )");
        }

        sb.append(" )\n");
        return sb.toString();
    }

}
