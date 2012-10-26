package nl.knaw.dans.easy.security;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class IsDepositorOfFileItemCheck extends AbstractCheck
{

    @Override
    public String getProposition()
    {
        return "[SessionUser is depositor of FileItem]";
    }

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;

        EasyUser sessionUser = ctxParameters.getSessionUser();
        Dataset dataset = ctxParameters.getDataset();
        FileItem fileItem = ctxParameters.getFileItem();
        if (isDescendant(fileItem, dataset) && !sessionUser.isAnonymous() && sessionUser.isActive())
        {
            conditionMet = dataset.hasDepositor(sessionUser) && fileItem.isCreatedByDepositor();
        }
        return conditionMet;
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
            sb.append("\n\tdataset = null");
        }

        EasyUser sessionUser = ctxParameters.getSessionUser();
        if (sessionUser == null)
        {
            sb.append("\n\tsessionUser = null");
        }
        else if (!sessionUser.isActive())
        {
            sb.append("\n\tsessionUser is not active. sessionUser=" + sessionUser);
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

        if (fileItem != null && !fileItem.isCreatedByDepositor())
        {
            sb.append("\n\t'" + fileItem.getStoreId() + "' was not created by depositor.");
        }
        else if (isDescendant(fileItem, dataset) && sessionUser != null && sessionUser.isActive())
        {
            String depositorId = dataset.getAdministrativeMetadata().getDepositorId();
            String sessionUserId = sessionUser.getId();
            sb.append("\n\t'" + dataset.getStoreId() + "' was deposited by '" + depositorId + "'. " + "Id of sessionUser is '" + sessionUserId + "'.");
        }

        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }

}
