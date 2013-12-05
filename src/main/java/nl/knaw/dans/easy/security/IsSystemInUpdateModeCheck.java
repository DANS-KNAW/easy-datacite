package nl.knaw.dans.easy.security;

import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;

public class IsSystemInUpdateModeCheck extends AbstractCheck
{
    private SystemReadOnlyStatus systemReadonlyStatus;

    public IsSystemInUpdateModeCheck(SystemReadOnlyStatus systemReadonlyStatus)
    {
        this.systemReadonlyStatus = systemReadonlyStatus;
    }

    @Override
    public String getProposition()
    {
        return PropositionBuilder.buildOrProposition("read only mode is", new Object[] {systemReadonlyStatus.getReadOnly()});
    }

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        return !systemReadonlyStatus.getReadOnly();
    }

    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        return super.startExplain(ctxParameters) + "\n\tCondition met = " + evaluate(ctxParameters);
    }
}
