package nl.knaw.dans.easy.security;

import nl.knaw.dans.easy.business.bean.SystemStatus;

public class UpdateEnabledCheck extends AbstractCheck
{

    @Override
    public String getProposition()
    {
        return PropositionBuilder.buildOrProposition("read only mode is", new Object[] {SystemStatus.instance().getReadOnly()});
    }

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        return !SystemStatus.instance().getReadOnly();
    }

    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        return super.startExplain(ctxParameters) + "\n\tCondition met = " + evaluate(ctxParameters);
    }
}
