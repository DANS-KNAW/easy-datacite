package nl.knaw.dans.common.ldap.ds;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.SortControl;
import javax.naming.ldap.SortResponseControl;

import org.junit.Ignore;
import org.junit.Test;

public class SortControlOnlineTest {

    @Ignore("Both apacheDS and openLdap do not support sorting")
    @Test
    public void sort() throws IOException {
        // Set up environment for creating initial context
        Hashtable<String, Object> env = new Hashtable<String, Object>(11);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:10389/ou=users,ou=test,dc=dans,dc=knaw,dc=nl");

        try {
            // Create initial context with no connection request controls
            LdapContext ctx = new InitialLdapContext(env, null);

            // Create a sort control that sorts based on CN
            String sortKey = "uid";
            ctx.setRequestControls(new Control[] {new SortControl(sortKey, Control.CRITICAL)});

            // Perform a search
            NamingEnumeration results = ctx.search("", "(objectclass=*)", new SearchControls());

            // Iterate over search results
            System.out.println("---->sort by cn");
            while (results != null && results.hasMore()) {
                // Display an entry
                SearchResult entry = (SearchResult) results.next();
                System.out.println(entry.getName());

                // Handle the entry's response controls (if any)
                if (entry instanceof HasControls) {
                    // ((HasControls)entry).getControls();
                }
            }
            // Examine the sort control response
            Control[] controls = ctx.getResponseControls();
            if (controls != null) {
                for (int i = 0; i < controls.length; i++) {
                    if (controls[i] instanceof SortResponseControl) {
                        SortResponseControl src = (SortResponseControl) controls[i];
                        if (!src.isSorted()) {
                            throw src.getException();
                        }
                    } else {
                        // Handle other response controls (if any)
                    }
                }
            }

            // Close when no longer needed
            ctx.close();
        }
        catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
