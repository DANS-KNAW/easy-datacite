package nl.knaw.dans.easy.tools.task.am.dataset;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.GroupImpl;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.util.Dialogue;

public class AddGroupTask extends AbstractTask {

    private int returnCode = -1;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        EasyUser user = Application.authenticate();

        String datasetSid = Dialogue.getInput("Enter storeId of dataset: ");

        try {
            Dataset dataset = Services.getDatasetService().getDataset(user, new DmoStoreId("easy-dataset:" + datasetSid));

            if (Dialogue.confirm("\n\nAre you sure you want add a group to the following dataset?" + "\nstoreId: " + datasetSid + "\ntitle: "
                    + dataset.getPreferredTitle() + "\ndepositor: " + dataset.getDepositor().getDisplayName() + "\n"))
            {
                System.out.println("1. " + Group.ID_ARCHEOLOGY);
                System.out.println("2. " + Group.ID_HISTORY);
                String groupNum = Dialogue.getInput("Enter number of group: ");
                if (groupNum.equals("1")) {
                    // add archeology to this group
                    dataset.addGroup(new GroupImpl(Group.ID_ARCHEOLOGY));
                    Services.getDatasetService().saveAdministrativeMetadata(user, dataset);
                    System.out.println("Added " + Group.ID_ARCHEOLOGY);
                    returnCode = 1;
                } else if (groupNum.equals("2")) {
                    // add history to this group
                    dataset.addGroup(new GroupImpl(Group.ID_HISTORY));
                    Services.getDatasetService().saveAdministrativeMetadata(user, dataset);
                    System.out.println("Added " + Group.ID_HISTORY);
                    returnCode = 2;
                } else {
                    System.out.println("No group added");
                }
            } else {
                System.out.println("Aborted action");
                returnCode = 0;
            }
        }
        catch (ObjectNotAvailableException e) {
            e.printStackTrace();
        }
        catch (CommonSecurityException e) {
            e.printStackTrace();
        }
        catch (ServiceException e) {
            e.printStackTrace();
        }
        catch (DataIntegrityException e) {
            e.printStackTrace();
        }
    }

    public int getReturnCode() {
        return returnCode;
    }

}
