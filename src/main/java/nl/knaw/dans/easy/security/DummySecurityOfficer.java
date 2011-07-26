package nl.knaw.dans.easy.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Temporary SecurityOfficer for methods that are under construction.
 * 
 * @author ecco Nov 16, 2009
 */
public class DummySecurityOfficer extends AbstractCheck
{

    private static final Logger logger = LoggerFactory.getLogger(DummySecurityOfficer.class);

    public String getProposition()
    {
        return "[DummyAuthz is a dummy Authz]";
    }

    public boolean evaluate(ContextParameters ctxParameters)
    {
        logger.warn("\n\t***************************************" 
                  + "\n\t*  Still using DummySecurityOfficer!  *"
                  + "\n\t***************************************" 
                  + printStackTrace());
        return true;
    }

    public String explain(ContextParameters ctxParameters)
    {
        return "\nI'm a dummy!";
    }

    private String printStackTrace()
    {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
        {
            if (ste.getClassName().startsWith("nl.knaw")
                    && !ste.getClassName().startsWith("nl.knaw.dans.easy.business.security"))
            {
                sb.append("\n\t")
                .append("at ")
                .append(ste.getClassName())
                .append(" (")
                .append(ste.getFileName())
                .append(":")
                .append(ste.getLineNumber())
                .append(")");
            }
        }
        return sb.toString();
    }
    
    @Override
    public boolean getHints(ContextParameters ctxParameters, List<Object> hints)
    {
        return true;
    }

}
