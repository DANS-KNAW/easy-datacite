package nl.knaw.dans.easy.security;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class FileItemContentsAccessCheck extends AbstractCheck
{

    @Override
    public String getProposition()
    {
        return "[Profile of sessionUser matches access rights for the contents of the FileItem]";
    }

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;
        FileItem fileItem = ctxParameters.getFileItem();
        EasyUser sessionUser = ctxParameters.getSessionUser();
        Dataset dataset = ctxParameters.getDataset();

        if (isDescendant(fileItem, dataset) && isValidUser(sessionUser))
        {
            int userProfile = dataset.getAccessProfileFor(sessionUser);
            conditionMet = fileItem.isAccessibleFor(userProfile);
        }
        return conditionMet;
    }

    private boolean isValidUser(EasyUser sessionUser)
    {
        return sessionUser != null && (sessionUser.isAnonymous() || !sessionUser.isBlocked());
    }

    private boolean isDescendant(FileItem fileItem, Dataset dataset)
    {
        return fileItem != null && fileItem.isDescendantOf(dataset);
    }

    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        StringBuilder sb = super.startExplain(ctxParameters);

        Dataset dataset = ctxParameters.getDataset();
        if (dataset == null)
        {
            sb.append("\n\tdataset = null --> cannot compute userProfile");
        }

        EasyUser sessionUser = ctxParameters.getSessionUser();
        if (sessionUser == null)
        {
            sb.append("\n\tsessionUser = null --> cannot compute userProfile");
        }
        else if (sessionUser.isBlocked())
        {
            sb.append("\n\tsessionUser is blocked");
        }

        FileItem fileItem = ctxParameters.getFileItem();
        if (fileItem == null)
        {
            sb.append("\n\tfileItem = null");
        }

        if (!isDescendant(fileItem, dataset) && fileItem != null & dataset != null)
        {
            sb.append("\n\t'" + fileItem.getStoreId() + "' is not a descendant of '" + dataset.getStoreId() + "'");
        }

        if (isDescendant(fileItem, dataset) && isValidUser(sessionUser))
        {
            int profile = dataset.getAccessProfileFor(sessionUser);
            AccessCategory fileItemAccessCat = fileItem.getReadAccessCategory();
            int mask = AccessCategory.UTIL.getBitMask(fileItemAccessCat);
            sb.append("\n\tsessionUser=").append(sessionUser).append("\n\tprofile of sessionUser=").append(profile).append(" (").append(
                    AccessCategory.UTIL.getStates(profile)).append(") ").append("\n\tfileItem id=").append(fileItem.getStoreId()).append(" fileItem mask=")
                    .append(mask).append(" (").append(fileItemAccessCat).append(") ").append("\n\t((mask & profile) > 0)=").append((mask & profile) > 0);
        }

        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }

}
