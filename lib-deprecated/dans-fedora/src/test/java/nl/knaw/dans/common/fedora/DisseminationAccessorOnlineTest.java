package nl.knaw.dans.common.fedora;

import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.fox.DobState;
import nl.knaw.dans.common.lang.RepositoryException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.server.types.gen.MethodParmDef;
import fedora.server.types.gen.ObjectMethodsDef;

public class DisseminationAccessorOnlineTest extends AbstractRepositoryOnlineTest
{

    private static final Logger logger = LoggerFactory.getLogger(DisseminationAccessorOnlineTest.class);

    private static ObjectManager objManager;
    private static DisseminationAccessor dissemAccessor;

    //private boolean                    verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforelass() throws RepositoryException
    {
        objManager = new ObjectManager(getRepository());
        dissemAccessor = new DisseminationAccessor(getRepository());
    }

    @Test
    public void listMethods() throws RepositoryException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        String sid = objManager.ingest(dob, "ingest for test");

        ObjectMethodsDef[] methodDefs = dissemAccessor.listMethods(sid, null);

        for (ObjectMethodsDef methodDef : methodDefs)
        {
            logger.debug("methodName=" + methodDef.getMethodName());
            logger.debug("\tasOfDate=" + methodDef.getAsOfDate());
            logger.debug("\tserviceDefinitionPID=" + methodDef.getServiceDefinitionPID());
            MethodParmDef[] parmDefs = methodDef.getMethodParmDefs();
            for (MethodParmDef parmDef : parmDefs)
            {
                logger.debug("\tparmName=" + parmDef.getParmName());
            }
        }

        objManager.purgeObject(sid, false, "cleaning up");
    }
}
