package nl.knaw.dans.easy.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fall back implementation for methods that want to comply to the security framework but have, for the time being, no
 * restrictions regarding security.
 * 
 * @author ecco Nov 16, 2009
 */
public class NoSecurityOfficer extends AbstractCheck
{

    private static final Logger logger = LoggerFactory.getLogger(NoSecurityOfficer.class);

    public NoSecurityOfficer()
    {

    }

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        logger.warn("No direct security applied!");
        return true;
    }

    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        StringBuilder sb = super.startExplain(ctxParameters);
        sb.append("\n\tNoSecurityOfficer implements no security.");
        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }

    public String getProposition()
    {
        return "[NoSecurityOfficer implements no security]";
    }

    @Override
    public boolean getHints(ContextParameters ctxParameters, List<Object> hints)
    {
        return true;
    }

}
