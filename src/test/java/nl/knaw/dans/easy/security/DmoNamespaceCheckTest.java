package nl.knaw.dans.easy.security;

import static org.junit.Assert.*;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.DmoNamespaceCheck;

import org.junit.Test;

public class DmoNamespaceCheckTest
{
    
    @Test
    public void explain()
    {
        DmoNamespaceCheck nsc = new DmoNamespaceCheck("dans-jumpoff", "easy-discipline");
        
        ContextParameters ctx = new ContextParameters("dans-jumpoff:41");
        //System.err.println(nsc.explain(ctx));
        assertTrue(nsc.evaluate(ctx));
        
        ctx = new ContextParameters("easy-dataset:43");
        //System.err.println(nsc.explain(ctx));
        assertFalse(nsc.evaluate(ctx));
    }

}
