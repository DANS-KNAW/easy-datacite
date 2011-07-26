package nl.knaw.dans.common.ldap.ds;

import java.util.Hashtable;
import java.util.Map.Entry;

import javax.naming.AuthenticationException;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import nl.knaw.dans.common.lang.ldap.DateTimeTranslator;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for performing basic operations on a directory service.
 * 
 * @author ecco Feb 4, 2009
 */
public class LdapClient
{

    private static final DateTimeTranslator DT_TRANSLATOR     = new DateTimeTranslator();

    /**
     * Logger for logging.
     */
    private static final Logger             logger            = LoggerFactory.getLogger(LdapClient.class);

    private DirContextSupplier              dirContextSupplier;
    private boolean                         updatingLastLogin = true;

    public LdapClient()
    {

    }

    /**
     * Constructs a new LdapClient.
     * 
     * @param dirContextSupplier
     *        supplies a fresh {@link DirContext} for each client call
     */
    public LdapClient(DirContextSupplier dirContextSupplier)
    {
        this.dirContextSupplier = dirContextSupplier;
    }

    /**
     * Set the supplier for DirContext.
     * 
     * @param contextSupplier
     *        the DirContextSupplier this client will use
     */
    public void setDirContextSupplier(DirContextSupplier contextSupplier)
    {
        this.dirContextSupplier = contextSupplier;
    }

    /**
     * Get the DirContextSupplier in use with this client.
     * 
     * @return the DirContextSupplier in use with this client
     */
    public DirContextSupplier getDirContextSupplier()
    {
        return dirContextSupplier;
    }

    /**
     * Is this client updating the 'dansLastLogin' attribute after a successful authentication?
     * 
     * @return <code>true</code> if it does, <code>false</code> otherwise
     */
    public boolean isUpdatingLastLogin()
    {
        return updatingLastLogin;
    }

    /**
     * Set whether this client should update the 'dansLastLogin' attribute after a successful authentication. The
     * default is <code>true</code>.
     * 
     * @param updatingLastLogin
     *        <code>true</code> if it should, <code>false</code> otherwise
     */
    public void setUpdatingLastLogin(boolean updatingLastLogin)
    {
        this.updatingLastLogin = updatingLastLogin;
        logger.debug("Updating last login set to " + updatingLastLogin);
    }

    /**
     * Get the attributes associated with the given rdn in the given subContext.
     * 
     * @param rdn
     *        a relative distinguished name (i.e "uid=willem")
     * @param subContext
     *        the sub context where to look, relative to the root
     * @return the attributes associated with the given rdn
     * @throws NameNotFoundException
     *         if the rdn was not found in the sub context
     * @throws NamingException
     *         for all exceptions
     */
    public Attributes getAttributes(String rdn, String subContext) throws NameNotFoundException, NamingException
    {
        DirContext ctx = dirContextSupplier.getDirContext();
        Attributes attrs = null;
        try
        {
            Name name = composeName(rdn, subContext);
            attrs = ctx.getAttributes(name);
        }
        finally
        {
            ctx.close();
        }
        return attrs;
    }

    /**
     * Get the attributes associated with the given rdn in the given subContext.
     * 
     * @param rdn
     *        a relative distinguished name (i.e "uid=willem")
     * @param subContext
     *        the sub context where to look, relative to the root
     * @param attrIds
     *        the list of attribute id's to get
     * @return the listed attributes associated with the given rdn
     * @throws NameNotFoundException
     *         if the rdn was not found in the sub context
     * @throws NamingException
     *         for all exceptions
     */
    public Attributes getAttributes(String rdn, String subContext, String[] attrIds) throws NameNotFoundException,
            NamingException
    {
        DirContext ctx = dirContextSupplier.getDirContext();
        Attributes attrs = null;
        try
        {
            Name name = composeName(rdn, subContext);
            attrs = ctx.getAttributes(name, attrIds);
        }
        finally
        {
            ctx.close();
        }
        return attrs;
    }

    private Name composeName(String rdn, String subContext) throws InvalidNameException
    {
        Name name = new LdapName(subContext);
        name.addAll(new LdapName(rdn));
        return name;
    }

    /**
     * Update the entry with the given rdn, within the given sub context, relative to the base context of this
     * LdapClient. This method <b>replaces</b> all previously assigned attributes with the new ones.
     * 
     * @param rdn
     *        the relative distinguished name of the entry to update
     * @param subContext
     *        the sub context, relative to the root of this LdapClient
     * @param attrs
     *        the new attributes for the entry
     * @throws NamingException
     *         for all exceptions
     */
    public void modifyEntry(String rdn, String subContext, Attributes attrs) throws NamingException
    {
        DirContext ctx = dirContextSupplier.getDirContext();
        try
        {
            Name name = composeName(rdn, subContext);
            ctx.modifyAttributes(name, DirContext.REPLACE_ATTRIBUTE, attrs);
        }
        finally
        {
            ctx.close();
        }
    }

    /**
     * Add a new entry under the given sub context, relative to this LdapClients base context, as the given rdn.
     * 
     * @param rdn
     *        the relative distinguished name of the entry to add
     * @param subContext
     *        the sub context, relative to the root of this LdapClient
     * @param attrs
     *        the attributes of the new entry
     * @throws NameAlreadyBoundException
     *         if the rdn is already bound
     * @throws NamingException
     *         for all exceptions
     */
    public void addEntry(String rdn, String subContext, Attributes attrs) throws NameAlreadyBoundException,
            NamingException
    {
        DirContext ctx = dirContextSupplier.getDirContext();
        try
        {
            Name name = composeName(rdn, subContext);
            ctx.bind(name, null, attrs);
        }
        finally
        {
            ctx.close();
        }
    }

    /**
     * Delete the entry with the given rdn, within the given sub context, relative to the base context of this
     * LdapClient.
     * 
     * @param rdn
     *        the relative distinguished name of the entry to delete
     * @param subContext
     *        the sub context, relative to the root of this LdapClient
     * @throws NamingException
     *         for all exceptions
     */
    public void deleteEntry(String rdn, String subContext) throws NamingException
    {
        DirContext ctx = dirContextSupplier.getDirContext();
        try
        {
            Name name = composeName(rdn, subContext);
            ctx.unbind(name);
        }
        finally
        {
            ctx.close();
        }
    }

    /**
     * List the bindings from the given sub context.
     * 
     * @param subContext
     *        sub context, may be the empty string ("")
     * @return bindings in the given sub context
     * @throws NamingException
     *         in case of oops
     */
    public NamingEnumeration<Binding> listBindings(String subContext) throws NamingException
    {
        DirContext ctx = dirContextSupplier.getDirContext();
        NamingEnumeration<Binding> list = null;
        try
        {
            list = ctx.listBindings(subContext);
        }
        finally
        {
            ctx.close();
        }
        return list;
    }

    /**
     * Authenticate the dn found in the given subContext, with the given filter against the given password.
     * <ol>
     * <li>If no dn is found returns false;</li>
     * <li>If a lookup with found dn as security principal and given password cannot be done, returns false;</li>
     * <li>Otherwise returns true</li>
     * </ol>
     * 
     * @param password
     *        password to authenticate with
     * @param subContext
     *        sub context relative to the root
     * @param filter
     *        filter to find the dn (i.e. "(&(objectClass=inetOrgPerson)(uid=jan))"
     * @param objectClasses
     *        the ldap objectClasses of the authenticated entity in reverse hierarchical order. Maybe <code>null</code>
     *        if updatingLastLogin is off.
     * @throws NamingException
     *         for exceptions
     */
    public boolean authenticate(String password, String subContext, String filter, String... objectClasses)
            throws NamingException
    {
        boolean authenticated = false;
        NamingEnumeration<SearchResult> result = search(subContext, filter);
        String dn = null;
        if (result.hasMore())
        {
            SearchResult r = result.next();
            if (r.isRelative())
            {
                dn = r.getNameInNamespace();
            }
            else
            {
                dn = r.getName();
            }
        }
        if (dn == null)
        {
            logger.debug("User not found in sub context '" + subContext + "' with filter " + filter);
        }
        else
        {
            DirContext ctx = dirContextSupplier.getDirContext();
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            try
            {
                ctx.lookup(dn);
                authenticated = true;
                logger.debug("Successfully authenticated: " + dn);

                if (isUpdatingLastLogin() && objectClasses != null && objectClasses.length > 0)
                {
                    updateLastLogin(dn, objectClasses);
                }

            }
            catch (AuthenticationException e)
            {
                logger.debug("Invalid pass. Failed authentication for " + dn);
            }
            finally
            {
                ctx.close();
            }
        }
        return authenticated;
    }

    private void updateLastLogin(String dn, String... objectClasses) throws NamingException, InvalidNameException
    {
        Attributes attrs = new BasicAttributes();
        Attribute oc = new BasicAttribute("objectclass");
        for (String objectClass : objectClasses)
        {
            oc.add(objectClass);
        }
        attrs.put(oc);
        attrs.put("dansLastLogin", DT_TRANSLATOR.toLdap(new DateTime()));

        DirContext ctx = dirContextSupplier.getDirContext();
        try
        {
            ctx.modifyAttributes(new LdapName(dn), DirContext.REPLACE_ATTRIBUTE, attrs);
            logger.debug("Modified last login of " + dn);
        }
        finally
        {
            ctx.close();
        }
    }

    public NamingEnumeration<SearchResult> search(String subContext, String filter) throws NamingException
    {
        return search(dirContextSupplier.getDirContext(), subContext, filter);
    }

    public NamingEnumeration<SearchResult> search(DirContext ctx, String subContext, String filter)
            throws NamingException
    {
        return search(ctx, subContext, filter, SearchControls.SUBTREE_SCOPE);
    }

    public NamingEnumeration<SearchResult> search(DirContext ctx, String subContext, String filter, int scope)
            throws NamingException
    {
        NamingEnumeration<SearchResult> resultEnum;
        try
        {
            resultEnum = null;
            Name name = new LdapName(subContext);
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(scope);
            resultEnum = ctx.search(name, filter, ctls);
        }
        finally
        {
            ctx.close();
        }
        return resultEnum;
    }

    public NamingEnumeration<SearchResult> search(String subContext, String filter, SearchControls ctls)
            throws NamingException
    {
        return search(dirContextSupplier.getDirContext(), subContext, filter, ctls);
    }

    public NamingEnumeration<SearchResult> search(DirContext ctx, String subContext, String filter, SearchControls ctls)
            throws NamingException
    {
        NamingEnumeration<SearchResult> resultEnum;
        try
        {
            resultEnum = null;
            Name name = new LdapName(subContext);
            resultEnum = ctx.search(name, filter, ctls);
        }
        finally
        {
            ctx.close();
        }
        return resultEnum;
    }

    // NON OFFICIAL CODE

    void logEnvironment() throws NamingException
    {
        DirContext ctx = dirContextSupplier.getDirContext();
        Hashtable<?, ?> env = ctx.getEnvironment();
        for (Entry<?, ?> entry : env.entrySet())
        {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    void getSchema(String subContext) throws NamingException
    {
        DirContext ctx = dirContextSupplier.getDirContext();
        // Get the schema tree root
        DirContext schema = ctx.getSchema("");

        // Get schema object for "person"
        DirContext personSchema = (DirContext) schema.lookup("ClassDefinition/person");

        // Get "person" object's attributes
        Attributes personAttrs = personSchema.getAttributes("");

        System.out.println(personAttrs);

        // Close the context when we're done
        ctx.close();

    }

    void addAttributes(String rdn, String subContext, Attributes attrs) throws NamingException
    {
        DirContext ctx = dirContextSupplier.getDirContext();
        try
        {
            Name name = new LdapName(subContext);
            name.addAll(new LdapName(rdn));
            ctx.modifyAttributes(name, DirContext.ADD_ATTRIBUTE, attrs);
        }
        finally
        {
            ctx.close();
        }

    }

    //
    void list(String subContext) throws NamingException
    {
        DirContext ctx = dirContextSupplier.getDirContext();
        NamingEnumeration<Binding> list = ctx.listBindings(subContext);

        while (list.hasMore())
        {
            Binding binding = list.next();
            System.err.println(binding.getName());
            // DirContext context = (DirContext) binding.getObject();
            // System.out.println(context.getNameInNamespace());
            // Attributes attrs = context.getAttributes("");
            // NamingEnumeration<? extends Attribute> all = attrs.getAll();
            // while (all.hasMoreElements())
            // {
            // Attribute attr = all.next();
            // System.out.println(attr);
            // if (attr.getID().equals("userPassword"))
            // {
            // byte[] b = (byte[]) attr.get();
            // System.out.println(new String(b));
            // }
            // }
        }

        // while (list.hasMore())
        // {
        // NameClassPair nc = (NameClassPair) list.next();
        // System.out.println(nc.getName());
        // }

        ctx.close();

    }

    Attributes getRootAttributes(String name) throws NamingException
    {
        return dirContextSupplier.getDirContext().getAttributes(name);
    }

    void lookup(String name) throws NamingException
    {
        DirContext ctx = dirContextSupplier.getDirContext();

        System.out.println(ctx.lookup(name));

    }

}
