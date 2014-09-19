package nl.knaw.dans.common.ldap.repo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.ldap.GenericRepo;
import nl.knaw.dans.common.lang.ldap.OperationalAttributes;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.user.RepoEntry;
import nl.knaw.dans.common.ldap.ds.LdapClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of a {@link GenericRepo}.
 * 
 * @author ecco Nov 20, 2009
 * @param <T>
 *        the type that is handled by this AbstractRepo
 */
public abstract class AbstractGenericRepo<T extends RepoEntry> implements GenericRepo<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractGenericRepo.class);

    private static final String CREATE_TIMESTAMP_ID = "createTimestamp";

    private static final String MODIFY_TIMESTAMP_ID = "modifyTimestamp";

    private final LdapClient client;

    private final String context;

    private final String rdn;

    private final String objectClassName;

    private final LdapMapper<T> ldapMapper;

    private String[] objectClassNamesArray;

    public AbstractGenericRepo(LdapClient client, String context, String rdn, LdapMapper<T> ldapMapper) {
        this.client = client;
        this.context = context;
        this.rdn = rdn;
        this.ldapMapper = ldapMapper;
        this.objectClassName = getObjectClassesArray()[0];
    }

    /**
     * Get the LdapClient this Repo talks to.
     * 
     * @return the LdapClient this Repo talks to
     */
    public LdapClient getClient() {
        return client;
    }

    /**
     * Get the distinguished name of the entry where type entries are kept.
     * 
     * @return the distinguished name of the entry where type entries are kept
     */
    public String getContext() {
        return context;
    }

    /**
     * Get the name of the objectClass of T.
     * 
     * @return name of the objectClass
     */
    public String getObjectClassName() {
        return objectClassName;
    }

    /**
     * Unmarshal the given attributes to an entry of type T.
     * 
     * @param attrs
     *        the attributes to unmarshal
     * @return unmarshalled entry
     * @throws LdapMappingException
     *         if unmarshalling went wrong
     */
    protected abstract T unmarshal(Attributes attrs) throws LdapMappingException;

    /**
     * Get the LdapMapper for the type T.
     * 
     * @return LdapMapper for type T
     */
    protected LdapMapper<T> getLdapMapper() {
        return ldapMapper;
    }

    /**
     * Get the ldap objectClasses of T in reverse hierarchical order.
     * 
     * @return the ldap objectClasses of T
     */
    public Set<String> getObjectClasses() {
        return getLdapMapper().getObjectClasses();
    }

    public String[] getObjectClassesArray() {
        if (objectClassNamesArray == null) {
            objectClassNamesArray = getObjectClasses().toArray(new String[] {});
        }
        return objectClassNamesArray;
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(String id) throws RepositoryException {
        boolean exists = true;
        try {
            findById(id);
        }
        catch (ObjectNotInStoreException e) {
            exists = false;
        }
        return exists;
    }

    /**
     * {@inheritDoc}
     */
    public T findById(String id) throws ObjectNotInStoreException, RepositoryException {
        T entry = null;
        try {
            Attributes attrs = client.getAttributes(getRdn(id), context);
            entry = unmarshal(attrs);
            if (logger.isDebugEnabled()) {
                logger.debug("Found entry with id '" + id + "' in " + context + ".");
            }
        }
        catch (NameNotFoundException e) {
            throw new ObjectNotInStoreException(e);
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
        return entry;
    }

    public List<T> findById(Collection<String> ids) throws ObjectNotInStoreException, RepositoryException {
        if (ids.size() == 0) {
            return Collections.emptyList();
        }

        StringBuilder filter = new StringBuilder("(&(objectClass=").append(objectClassName).append(")(|");
        for (String id : ids) {
            filter.append("(").append(getRdn(id)).append(")");
        }
        filter.append("))");
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        return search(filter.toString(), ctls);
    }

    /**
     * {@inheritDoc}
     */
    public String add(T entry) throws ObjectExistsException, RepositoryException {
        final String id = entry.getId();
        if (exists(id)) {
            final String msg = "An entry with id " + id + " already exists in " + context + ".";
            logger.debug(msg);
            throw new ObjectExistsException(msg);
        }
        Attributes attrs = getLdapMapper().marshal(entry, false);
        try {
            client.addEntry(getRdn(id), context, attrs);
            if (logger.isDebugEnabled()) {
                logger.debug("Added entry with id '" + id + "' to " + context + ".");
            }
        }
        catch (NameAlreadyBoundException e) {
            throw new ObjectExistsException(e);
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public String update(T entry) throws RepositoryException {
        final String id = entry.getId();
        Attributes attrs = getLdapMapper().marshal(entry, true);
        try {
            client.modifyEntry(getRdn(id), context, attrs);
            if (logger.isDebugEnabled()) {
                logger.debug("Updated entry with id '" + id + "' in " + context + ".");
            }
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(String id) throws RepositoryException {
        try {
            client.deleteEntry(getRdn(id), context);
            if (logger.isDebugEnabled()) {
                logger.debug("Deleted entry with id '" + id + "' from " + context + ".");
            }
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void delete(T entry) throws RepositoryException {
        delete(entry.getId());
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findAll() throws RepositoryException {
        String filter = "(objectClass=" + objectClassName + ")";
        List<T> entries = search(filter);
        if (logger.isDebugEnabled()) {
            logger.debug("Find all found " + entries.size() + " entries in context " + context + ".");
        }
        return entries;
    }

    public List<String> findAllEntries(int maxCount) throws RepositoryException {
        List<String> ids = new ArrayList<String>();
        String filter = "(&(objectClass=" + getObjectClassName() + ")(" + getRdn("*") + "))";
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        ctls.setCountLimit(maxCount);
        ctls.setReturningAttributes(new String[] {getRdn()});

        try {
            NamingEnumeration<SearchResult> resultEnum = getClient().search(getContext(), filter, ctls);
            while (resultEnum.hasMoreElements()) {
                SearchResult result = resultEnum.next();
                Attributes attrs = result.getAttributes();
                ids.add((String) attrs.get(getRdn()).get());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Found " + ids.size() + " " + getRdn() + "'s in " + getContext());
            }
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
        return ids;
    }

    /**
     * {@inheritDoc}
     */
    public OperationalAttributes getOperationalAttributes(String id) throws RepositoryException {
        LdapOperationalAttributes opa = new LdapOperationalAttributes();
        try {
            Attributes attrs = client.getAttributes(getRdn(id), context, new String[] {CREATE_TIMESTAMP_ID, MODIFY_TIMESTAMP_ID});
            if (attrs.size() > 0) {
                opa.setCreateTime((String) attrs.get(CREATE_TIMESTAMP_ID).get());
            }
            if (attrs.size() > 1) {
                opa.setModifyTime((String) attrs.get(MODIFY_TIMESTAMP_ID).get());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Got " + attrs.size() + " attribute(s) for '" + id + "' from " + context + ".");
            }
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
        return opa;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws RepositoryException {
        // nothing to close?
    }

    /**
     * Get the Relative Distinguished Name for the given id.
     * 
     * @param id
     *        the id of an entry of type T
     * @return the RDN of the entry
     */
    protected String getRdn(String id) {
        return rdn + "=" + id;
    }

    protected String getRdn() {
        return rdn;
    }

    /**
     * Do a one-level-scope search with the given filter in the context of this repo.
     * 
     * @param filter
     *        filter to use
     * @return list of entries that satisfy the filter criteria
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    protected List<T> search(String filter) throws RepositoryException {
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        return search(filter, ctls);
    }

    /**
     * Search with the given filter and search controls in the context of this repo.
     * 
     * @param filter
     *        filter to use
     * @param ctls
     *        controls to use
     * @return list of entries that satisfy the filter criteria
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    protected List<T> search(String filter, SearchControls ctls) throws RepositoryException {
        List<T> entries = new ArrayList<T>();
        try {
            NamingEnumeration<SearchResult> resultEnum = client.search(context, filter, ctls);
            while (resultEnum.hasMore()) {
                SearchResult result = resultEnum.next();
                T entry = unmarshal(result.getAttributes());
                entries.add(entry);
            }
        }
        catch (LdapMappingException e) {
            throw new RepositoryException(e);
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
        return entries;
    }

    /**
     * Censors humanoid search phrases. Humanoids have the inclination to come up with search phrases that can get an Ldap server totally upset. This method
     * censors the given stub and returns a text suitable for Ldap digestion.
     * 
     * @param stub
     *        uncensored search phrase
     * @return censored search phrase
     */
    protected static String censorHumanoidSearchPhrase(String stub) {
        String text = stub.replaceAll("[\\(\\)\\\\\\[\\]{}]", "*");
        text = text.replaceAll("\\A\\**", "");
        return text;
    }

    @SuppressWarnings("unused")
    private void printAttributes(Attributes attrs) throws NamingException {
        NamingEnumeration<? extends Attribute> attrEnum = attrs.getAll();
        while (attrEnum.hasMore()) {
            Attribute attr = attrEnum.next();
            for (int i = 0; i < attr.size(); i++) {
                logger.debug(attr.getID() + "=" + attr.get(i));
            }
        }
    }
}
