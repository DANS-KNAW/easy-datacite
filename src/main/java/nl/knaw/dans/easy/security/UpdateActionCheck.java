package nl.knaw.dans.easy.security;

import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo.Action;

public class UpdateActionCheck extends AbstractCheck
{

    private final List<Action> allowedActions;
    private final String proposition;

    public UpdateActionCheck(Action... allowedActions)
    {
        super();
        this.allowedActions = Arrays.asList(allowedActions);
        proposition = PropositionBuilder.buildCollectionProposition("Update actions are confined to", this.allowedActions);
    }

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;
        UpdateInfo updateInfo = (UpdateInfo) ctxParameters.getObject(UpdateInfo.class, 0);
        if (updateInfo != null) // wo uber man nicht sprechen kann, muss mann schweigen
        {
            conditionMet = allowedActions.containsAll(updateInfo.getActions());
        }
        return conditionMet;
    }

    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        StringBuilder sb = super.startExplain(ctxParameters);
        UpdateInfo updateInfo = (UpdateInfo) ctxParameters.getObject(UpdateInfo.class, 0);
        if (updateInfo == null)
        {
            sb.append("\n\tupdateInfo = null");
        }
        else
        {
            sb.append("\n\tplanned update action(s): ").append(updateInfo.getActions());
        }
        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }

    public String getProposition()
    {
        return proposition;
    }

}
