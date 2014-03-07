package nl.knaw.dans.easy.security;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;

public final class HasRoleCheck extends AbstractCheck
{

    private final Role[] grantedForRoles;

    public HasRoleCheck(Role... roles)
    {
        super();
        grantedForRoles = roles;
    }

    public String getProposition()
    {
        synchronized (grantedForRoles)
        {
            return PropositionBuilder.buildOrProposition("SessionUser has role", grantedForRoles);
        }
    }

    public boolean evaluate(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;
        EasyUser sessionUser = ctxParameters.getSessionUser();
        if (sessionUser != null && !sessionUser.isAnonymous() && sessionUser.isActive())
        {
            synchronized (grantedForRoles)
            {
                conditionMet = sessionUser.hasRole(grantedForRoles);
            }
        }
        return conditionMet;
    }

    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        StringBuilder sb = super.startExplain(ctxParameters);

        EasyUser sessionUser = ctxParameters.getSessionUser();
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
            StringBuilder sb2 = new StringBuilder();
            for (Role role : sessionUser.getRoles())
            {
                sb2.append(role);
                sb2.append(" ");
            }
            if (sb2.length() == 0)
            {
                sb2.append("(none) ");
            }

            sb.append("\n\tsessionUser has roles " + sb2.toString());
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
        else
        {
            conditionMet = evaluate(ctxParameters);
            if (!conditionMet)
            {
                hints.add(CommonSecurityException.HINT_SESSION_USER_NOT_IN_ROLE);
            }
        }
        return conditionMet;
    }

}
