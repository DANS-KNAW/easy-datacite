package nl.knaw.dans.easy.business.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public aspect UsesAspect
{
    
    private static final Logger logger = LoggerFactory.getLogger(UsesAspect.class);
    
    private boolean verbose = false;
    
    pointcut canDo() : withincode(* nl.knaw.dans.easy..*Test.*(..));
    
    pointcut components() : call(* nl.knaw.dans.c..*.*(..))
        || call(nl.knaw.dans.c..new(..));
    
    declare error : components() && !canDo()
        : "Illegal direct call to component layer. " +
          "Implementations of components should not be accessed directly. " +
          "Talk to their interfaces instead.";
    
    before() : components()
    {
        if (verbose)
            logger.debug("Direct call to component layer at" + AspectUtil.printSourceAndSignature(thisJoinPoint));
    }

}
