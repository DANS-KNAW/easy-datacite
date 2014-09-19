package nl.knaw.dans.common.ldap.ds;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import nl.knaw.dans.common.lang.RepositoryException;

/**
 * Implementations of this interface are capable of supplying a javax.naming.directory.DirContext.
 * 
 * @author ecco
 */
public interface DirContextSupplier {

    /**
     * Get the javax.naming.directory.DirContext.
     * 
     * @return DirContext
     * @throws RepositoryException
     *         as the root exception for everything that can go wrong and eventually, sooner or later, will go wrong.
     */
    DirContext getDirContext() throws NamingException;

}
