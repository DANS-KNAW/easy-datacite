package nl.knaw.dans.easy.tools.task.dump;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.ldap.ds.DirContextSupplier;
import nl.knaw.dans.common.ldap.ds.LdapClient;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;

public class DumpUserListTask extends AbstractTask {
    private File exportFile;
    private LdapClient ldapClient;
    private PrintWriter exportFileWriter;

    @Override
    public void run(JointMap joint) throws FatalTaskException {
        openExportFileWriter();

        try {
            DirContextSupplier d = ldapClient.getDirContextSupplier();
            DirContext ctx = d.getDirContext();
            NamingEnumeration<Binding> bindings = ctx.listBindings("ou=users,ou=easy,dc=dans,dc=knaw,dc=nl");

            while (bindings.hasMore()) {
                new UserWriter() //
                        .user(bindings.next()) //
                        .writer(exportFileWriter) //
                        .write(); //
            }
        }
        catch (NamingException e) {
            throw new FatalTaskException(e, this);
        }
        finally {
            closeExportFileWriter();
        }
    }

    private void openExportFileWriter() throws FatalTaskException {
        try {
            exportFileWriter = new PrintWriter(exportFile);
        }
        catch (IOException e) {
            throw new FatalTaskException(e, this);
        }
    }

    private void closeExportFileWriter() {
        if (exportFileWriter != null) {
            exportFileWriter.close();
        }
    }

    public void setExportFile(File exportFile) {
        this.exportFile = exportFile;
    }

    public void setLdapClient(LdapClient ldapClient) {
        this.ldapClient = ldapClient;
    }
}
