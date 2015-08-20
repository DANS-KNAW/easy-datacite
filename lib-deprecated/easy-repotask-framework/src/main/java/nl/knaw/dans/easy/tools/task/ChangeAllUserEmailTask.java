package nl.knaw.dans.easy.tools.task;

import java.io.IOException;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.util.Reporter;

public class ChangeAllUserEmailTask extends AbstractTask {
    public static final String USER_INFO_REPORT = "change-all-user-email-info.csv";
    public static final String EMAIL_ADDRESS = "easyonfedora@gmail.com";

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        EasyUserRepo repo = Data.getUserRepo();
        try {
            addHeading();
            List<String> uids = repo.findAllEntries(0);
            for (String uid : uids) {
                EasyUser user = repo.findById(uid);
                changeEmail(user);
                repo.update(user);
            }
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }
        catch (IOException e) {
            throw new FatalTaskException(e, this);
        }
        finally {
            closeReport();
        }
    }

    public void closeReport() throws FatalTaskException {
        try {
            Reporter.closeFile(USER_INFO_REPORT);
        }
        catch (IOException e) {
            throw new FatalTaskException(e, this);
        }
    }

    private void addHeading() throws IOException {
        StringBuilder sb = new StringBuilder();
        append(sb, "USERNAME (UID)");
        append(sb, "FROM");
        append(sb, "TO");
        append(sb, "STATE");
        append(sb, "ROLES");
        append(sb, "GROUPS");
        append(sb, "LAST LOGIN");
        Reporter.appendReport(USER_INFO_REPORT, sb.toString());
    }

    private void changeEmail(EasyUser user) throws IOException {
        StringBuilder sb = new StringBuilder();
        String oldEmail = user.getEmail();

        if (oldEmail == null) {
            return;
        }

        if (!oldEmail.equals(EMAIL_ADDRESS) && !oldEmail.endsWith("dans.knaw.nl")) {
            append(sb, user.getId());
            append(sb, oldEmail);
            append(sb, EMAIL_ADDRESS);
            append(sb, user.getState());
            append(sb, user.getRoles());
            append(sb, user.getGroupIds());
            append(sb, user.getLastLoginDate() == null ? "-" : user.getLastLoginDate().toString("yyyy-MM-dd"));
            // change the email adres!
            user.setEmail(EMAIL_ADDRESS);
        } else {
            // user email not changed, should we log this?
        }
        Reporter.appendReport(USER_INFO_REPORT, sb.toString());
    }

    private StringBuilder append(StringBuilder sb, Object o) {
        return sb.append(o == null ? "" : o).append(";");
    }
}
