package nl.knaw.dans.easy.security;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public final class IsDepositorOfDatasetCheck extends AbstractCheck
{

    public String getProposition()
    {
        return "[SessionUser is depositor of dataset]";
    }

    public boolean evaluate(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;

        EasyUser sessionUser = ctxParameters.getSessionUser();
        Dataset dataset = ctxParameters.getDataset();
        if (dataset != null && sessionUser != null && sessionUser.isActive())
        {
            conditionMet = dataset.hasDepositor(sessionUser);
        }
        return conditionMet;
    }

    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        StringBuilder sb = super.startExplain(ctxParameters);

        EasyUser sessionUser = ctxParameters.getSessionUser();
        Dataset dataset = ctxParameters.getDataset();

        if (sessionUser == null)
        {
            sb.append("\n\tsessionUser = null");
        }
        else if (sessionUser.isAnonymous())
        {
            sb.append("\n\tsessionUser = anonymous");
        }
        else if (!sessionUser.isActive())
        {
            sb.append("\n\tsessionUser.State is not ACTIVE, sessionUser.State is " + sessionUser.getState());
        }
        else
        {
            sb.append("\n\tsessionUser userId = " + sessionUser.getId());
        }

        if (dataset == null)
        {
            sb.append(", dataset = null");
        }
        else
        {
            sb.append(", dataset depositorId = " + dataset.getOwnerId());
        }

        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }

    @Override
    public boolean getHints(ContextParameters ctxParameters, List<Object> hints)
    {
        boolean conditionMet = false;
        EasyUser sessionUser = ctxParameters.getSessionUser();
        Dataset dataset = ctxParameters.getDataset();

        if (sessionUser == null)
        {
            hints.add(CommonSecurityException.HINT_SESSION_USER_NULL);
        }
        else if (!sessionUser.isActive())
        {
            hints.add(CommonSecurityException.HINT_SESSION_USER_NOT_ACTIVE);
        }
        else if (sessionUser.isAnonymous())
        {
            hints.add(CommonSecurityException.HINT_SESSION_USER_ANONYMOUS);
        }
        else if (dataset == null)
        {
            hints.add(CommonSecurityException.HINT_DATASET_NULL);
        }
        else
        {
            conditionMet = evaluate(ctxParameters);
            if (!conditionMet)
            {
                hints.add(CommonSecurityException.HINT_SESSION_USER_NOT_DEPOSITOR);
            }
        }
        return conditionMet;
    }

}
