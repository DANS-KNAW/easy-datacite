package nl.knaw.dans.easy.security;

import static org.junit.Assert.*;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.DmoNamespaceCheck;

import org.junit.Test;

public class DmoNamespaceCheckTest
{
    
    @Test
    public void explain()
    {
        DmoNamespaceCheck nsc = new DmoNamespaceCheck(JumpoffDmo.NAMESPACE, DisciplineContainer.NAMESPACE);
        
        ContextParameters ctx = new ContextParameters("dans-jumpoff:41");
        //System.err.println(nsc.explain(ctx));
        assertTrue(nsc.evaluate(ctx));
        
        ctx = new ContextParameters("easy-dataset:43");
        //System.err.println(nsc.explain(ctx));
        assertFalse(nsc.evaluate(ctx));
    }

}
