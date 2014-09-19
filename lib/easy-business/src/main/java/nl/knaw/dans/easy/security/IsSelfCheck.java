package nl.knaw.dans.easy.security;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public final class IsSelfCheck extends AbstractCheck {

    public String getProposition() {
        return "[SessionUser is user under edit]";
    }

    public boolean evaluate(ContextParameters ctxParameters) {
        boolean conditionMet;
        EasyUser sessionUser = ctxParameters.getSessionUser();
        String underEditId = getUnderEditId(ctxParameters);
        if (sessionUser == null || sessionUser.isAnonymous() || underEditId == null) {
            conditionMet = false;
        } else {
            conditionMet = sessionUser.getId().equals(underEditId);
        }
        return conditionMet;
    }

    private String getUnderEditId(ContextParameters ctxParameters) {
        EasyUser userUnderEdit = ctxParameters.getUserUnderEdit();
        String underEditId;
        if (userUnderEdit == null) {
            underEditId = (String) ctxParameters.getObject(String.class, 0);
        } else {
            underEditId = userUnderEdit.getId();
        }
        return underEditId;
    }

    @Override
    protected String explain(ContextParameters ctxParameters) {
        StringBuilder sb = super.startExplain(ctxParameters);

        EasyUser sessionUser = ctxParameters.getSessionUser();
        String underEditId = getUnderEditId(ctxParameters);
        if (sessionUser == null) {
            sb.append("\n\tsessionUser = null");
        } else if (sessionUser.isAnonymous()) {
            sb.append("\n\tsessionUser = anonymous");
        } else {
            sb.append("\n\tsessionUser userId = " + sessionUser.getId());
        }
        if (underEditId == null) {
            sb.append(", user under edit = null");
        } else {
            sb.append(", user under edit userId = " + underEditId);
        }
        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }

    @Override
    public boolean getHints(ContextParameters ctxParameters, List<Object> hints) {
        boolean conditionMet = false;
        EasyUser sessionUser = ctxParameters.getSessionUser();
        String underEditId = getUnderEditId(ctxParameters);
        if (sessionUser == null) {
            hints.add(CommonSecurityException.HINT_SESSION_USER_NULL);
        } else if (sessionUser.isAnonymous()) {
            hints.add(CommonSecurityException.HINT_SESSION_USER_ANONYMOUS);
        } else if (underEditId != null) {
            conditionMet = evaluate(ctxParameters);
            if (conditionMet) {
                hints.add(CommonSecurityException.HINT_SESSION_USER_SELF);
            }
        }
        return conditionMet;
    }

}
