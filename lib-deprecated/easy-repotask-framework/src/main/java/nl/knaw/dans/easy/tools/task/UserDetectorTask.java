package nl.knaw.dans.easy.tools.task;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;

public class UserDetectorTask extends AbstractTask {
    /*
     * Is it required that the user of the application (System.getProperty("user.name")) has the same username as the EasyUser that is being used in calls to
     * the business.service layer.
     */
    private boolean proxyUserAllowed;

    private boolean archivistRequired = true;

    public boolean isProxyUserAllowed() {
        return proxyUserAllowed;
    }

    public void setProxyUserAllowed(boolean proxyUserAllowed) {
        this.proxyUserAllowed = proxyUserAllowed;
    }

    public boolean isArchivistRequired() {
        return archivistRequired;
    }

    public void setArchivistRequired(boolean archivistRequired) {
        this.archivistRequired = archivistRequired;
    }

    @Override
    public void run(JointMap joint) throws FatalTaskException {
        EasyUser easyUser;
        if (Application.getApplicationUser() != null) // by commandline authentication
        {
            easyUser = Application.getApplicationUser();
            RL.info(new Event(RL.GLOBAL, "EasyUser is authenticated user: " + easyUser));
        } else {
            easyUser = detectUser();
        }

        try {
            enforceProxyUserRule(easyUser.getId());
            enforceArchivistRule(easyUser);
        }
        catch (CommonSecurityException e) {
            throw new FatalTaskException(e, this);
        }

        joint.setEasyUser(easyUser);
    }

    private EasyUser detectUser() throws FatalTaskException {
        String systemUsername = getSystemUsername();
        String proxyUsername = Application.getProgramArgs().getUsername();

        String easyUsername;
        if (proxyUserAllowed && proxyUsername != null) {
            easyUsername = proxyUsername;
            RL.info(new Event(RL.GLOBAL, "Using proxy username " + proxyUsername));
        } else {
            easyUsername = systemUsername;
            RL.info(new Event(RL.GLOBAL, "Using system username " + systemUsername));
        }

        EasyUser easyUser;
        try {
            easyUser = Data.getUserRepo().findById(easyUsername);
            RL.info(new Event(RL.GLOBAL, "Found easyUser " + easyUser));
        }
        catch (ObjectNotInStoreException e) {
            throw new FatalTaskException("No user with uid '" + easyUsername + "'", e, this);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

        return easyUser;
    }

    private void enforceArchivistRule(EasyUser easyUser) throws CommonSecurityException {
        if (archivistRequired && !easyUser.hasRole(Role.ARCHIVIST)) {
            throw new CommonSecurityException("Tasks can only be executed by easyUser with role ARCHIVIST. easyUser=" + easyUser.toString() + " "
                    + easyUser.getRoles());
        }

    }

    private void enforceProxyUserRule(String proxyUsername) throws CommonSecurityException {
        String systemUsername = getSystemUsername();
        if (!proxyUserAllowed && proxyUsername != null && !systemUsername.equals(proxyUsername)) {
            throw new CommonSecurityException("No proxy user allowed. systemUsername=" + systemUsername + " proxyUsername=" + proxyUsername);
        }
    }

    protected String getSystemUsername() {
        return System.getProperty("user.name");
    }

}
