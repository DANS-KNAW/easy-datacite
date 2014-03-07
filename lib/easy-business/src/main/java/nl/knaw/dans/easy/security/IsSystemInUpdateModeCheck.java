package nl.knaw.dans.easy.security;

import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;

public class IsSystemInUpdateModeCheck extends AbstractCheck
{
    private SystemReadOnlyStatus systemReadOnlyStatus;

    public IsSystemInUpdateModeCheck(SystemReadOnlyStatus systemReadOnlyStatus)
    {
        this.systemReadOnlyStatus = systemReadOnlyStatus;
    }

    @Override
    public String getProposition()
    {
        return PropositionBuilder.buildOrProposition("read only mode is", new Object[] {systemReadOnlyStatus.getReadOnly()});
    }

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        return !systemReadOnlyStatus.getReadOnly();
    }

    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        return super.startExplain(ctxParameters) + "\n\tCondition met = " + evaluate(ctxParameters);
    }
}
