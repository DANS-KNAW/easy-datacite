package nl.knaw.dans.common.ldap.management;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import nl.knaw.dans.common.ldap.ds.Constants;

public abstract class LdapServerBuilder
{

    public static final String EASY_CONTEXT               = "ou=easy," + Constants.DANS_CONTEXT;
    public static final String EASY_GROUPS_CONTEXT        = "ou=groups," + EASY_CONTEXT;
    public static final String EASY_USERS_CONTEXT         = "ou=users," + EASY_CONTEXT;
    public static final String EASY_MIGRATION_CONTEXT     = "ou=migration," + EASY_CONTEXT;
    public static final String EASY_FEDERATION_CONTEXT     = "ou=federation," + EASY_CONTEXT;

    public static final String DCCD_CONTEXT               = "ou=dccd," + Constants.DANS_CONTEXT;
    public static final String DCCD_ORGANISATIONS_CONTEXT = "ou=organisations," + DCCD_CONTEXT;
    public static final String DCCD_USERS_CONTEXT         = "ou=users," + DCCD_CONTEXT;
    
    private DirContext ctx;
    private DirContext rootContext;
    private String securityCredentials;
    
    public LdapServerBuilder() throws NamingException
    {
       
    }

    public abstract String getProviderUrl();
    
    public abstract String getSecurityPrincipal();
    
    public String getSecurityCredentials()
    {
        if (securityCredentials == null)
        {
            securityCredentials = Constants.DEFAULT_SECURITY_CREDENTIALS;
        }
        return securityCredentials;
    }

    public void setSecurityCredentials(String securityCredentials)
    {
        this.securityCredentials = securityCredentials;
    }
    
    protected Hashtable<String, String> getEnvironment()
    {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, Constants.CONTEXT_FACTORY);
        env.put(Context.SECURITY_AUTHENTICATION, Constants.SIMPLE_AUTHENTICATION);

        env.put(Context.PROVIDER_URL, getProviderUrl());
        env.put(Context.SECURITY_PRINCIPAL, getSecurityPrincipal());
        env.put(Context.SECURITY_CREDENTIALS, getSecurityCredentials());
        return env;
    }

    public DirContext getDirContext() throws NamingException
    {
        if (ctx == null)
        {
            ctx = new InitialDirContext(getEnvironment());
        }
        return ctx;
    }

    public DirContext getRootContext() throws NamingException
    {
        if (rootContext == null)
        {
            rootContext = getDirContext().getSchema("");
        }
        return rootContext;
    }
    
    public List<AbstractSchema> getSchemas()
    {
        List<AbstractSchema> schemas = new ArrayList<AbstractSchema>();
        schemas.add(new DANSSchema());
        schemas.add(new EasySchema());
        schemas.add(new DCCDSchema());
        return schemas;
    }
    
    public void buildServer() throws NamingException, IOException
    {
        buildContexts();
        buildSchemas();
    }
    
    public void buildSchemas() throws NamingException, IOException
    {
        List<AbstractSchema> schemas = getSchemas();
        System.out.println("Count schemas = " + schemas.size());
        
        // create attributeTypes
        for (AbstractSchema schema : schemas)
        {
            buildAttributeTypes(schema);
        }
        
        // destroy objectClasses
        int count = schemas.size() - 1;
        for (int i = count; i >= 0; i--)
        {
            destroyObjectClasses(schemas.get(i));
        }
        
        // create objectClasses
        for (AbstractSchema schema : schemas)
        {
            buildObjectClasses(schema);
        }

    }

    protected void buildAttributeTypes(AbstractSchema schema) throws NamingException
    {
        System.out.println("BUILDING attributeTypes " + schema.getSchemaName() + " SCHEMA");
        for (Attributes attrs : schema.getAttributeTypes())
        {
            String name = "AttributeDefinition/" + attrs.get("NAME").get();
            if (!isSchemaBound(name))
            {
                createSchema(name, attrs);
            }
        }
    }
    
    protected void destroyObjectClasses(AbstractSchema schema) throws NamingException
    {
        System.out.println("DESTROYING objectClasses " + schema.getSchemaName() + " SCHEMA");
        for (Attributes attrs : schema.getObjectClasses())
        {
            String name = "ClassDefinition/" + attrs.get("NAME").get();
            if (isSchemaBound(name))
            {
                getRootContext().destroySubcontext(name);
                System.out.println("objectClass destroyed: " + name);
            }
        }
    }
    
    protected void buildObjectClasses(AbstractSchema schema) throws NamingException
    {
        System.out.println("BUILDING objectClasses " + schema.getSchemaName() + " SCHEMA");
        for (Attributes attrs : schema.getObjectClasses())
        {
            String name = "ClassDefinition/" + attrs.get("NAME").get();
            if (!isSchemaBound(name))
            {
                createSchema(name, attrs);
            }
        }
    }
    
    public void buildContexts() throws NamingException
    {
        buildDansContexts();
        buildEasyContexts();
        buildDccdContexts();
    }
    
    protected void buildDansContexts() throws NamingException
    {
        System.out.println("BUILDING DANS CONTEXTS");
        
        String dn = Constants.DANS_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("domain");
            oc.add("top");

            attrs.put(oc);
            attrs.put("dc", Constants.dcDANS);
            attrs.put("dc", Constants.dcKNAW);
            attrs.put("dc", Constants.dcNL);

            buildContext(dn, attrs);
        }

        dn = Constants.TEST_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", Constants.ouTEST);

            buildContext(dn, attrs);
        }

        dn = Constants.TEST_USERS_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", Constants.ouUSERS);

            buildContext(dn, attrs);
        }
        
        dn = Constants.TEST_MIGRATION_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", Constants.ouMIGRATION);

            buildContext(dn, attrs);
        }

        dn = Constants.TEST_FEDERATION_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", Constants.ouFEDERATION);

            buildContext(dn, attrs);
        }        

        System.out.println("END BUILDING DANS CONTEXTS");
    }
    
    protected void buildEasyContexts() throws NamingException
    {
        System.out.println("BUILDING EASY CONTEXT");
        
        String dn = EASY_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", "easy");

            buildContext(dn, attrs);
        }

        dn = EASY_GROUPS_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", "groups");

            buildContext(dn, attrs);
        }

        dn = EASY_USERS_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", "users");

            buildContext(dn, attrs);
        }
        
        dn = EASY_MIGRATION_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", "migration");

            buildContext(dn, attrs);
        }
        
        dn = EASY_FEDERATION_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", "federation");

            buildContext(dn, attrs);
        }
        
        System.out.println("END BUILDING EASY CONTEXTS");
    }
    
    protected void buildDccdContexts() throws NamingException
    {
        System.out.println("BUILDING DCCD CONTEXTS");

        String dn = DCCD_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", "dccd");

            buildContext(dn, attrs);
        }

        dn = DCCD_ORGANISATIONS_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", "organisations"); // was cn instaed of ou

            buildContext(dn, attrs);
        }

        dn = DCCD_USERS_CONTEXT;
        if (!isContextBound(dn))
        {
            Attributes attrs = new BasicAttributes();
            Attribute oc = new BasicAttribute("objectclass");
            oc.add("extensibleObject");
            oc.add("organizationalUnit");
            oc.add("top");

            attrs.put(oc);
            attrs.put("ou", "users");

            buildContext(dn, attrs);
        }

        System.out.println("END BUILDING DCCD CONTEXTS");
    }
    
    protected boolean isContextBound(String context) throws NamingException
    {
        boolean hasContext = false;
        try
        {
            getDirContext().listBindings(context);
            hasContext = true;
            System.out.println("Context found: " + context);
        }
        catch (NameNotFoundException e)
        {
            System.out.println("Context does not exist: " + context);
        }
        return hasContext;
    }
    
    protected void buildContext(String name, Attributes attrs) throws NamingException
    {
        getDirContext().createSubcontext(name, attrs);
        System.out.println("Added subContext: " + name);
    }
    
    protected void createSchema(String name, Attributes attrs) throws NamingException
    {
        getRootContext().createSubcontext(name, attrs);
        System.out.println("Added schema: " + name);
    }
    
    protected boolean isSchemaBound(String name) throws NamingException
    {
        boolean hasName = false;
        try
        {
            getRootContext().list(name);
            hasName = true;
            System.out.println("Schema found: " + name);
        }
        catch (NameNotFoundException e)
        {
            System.out.println("Schema does not exists: " + name);
        }
        return hasName;
    }

}
