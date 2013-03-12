package nl.knaw.dans.pf.language.ddm.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import nl.knaw.dans.pf.language.ddm.api.Ddm2EmdCrosswalk;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class CrosswalkTest
{
    private static Logger log = LoggerFactory.getLogger(CrosswalkTest.class);
    private final String input;

    public CrosswalkTest(final String input)
    {
        this.input = input;
    }

    @AfterClass
    public static void logSkippedElements()
    {
        // TODO collect skipped elements as logged by CrosswalkHandler.startElement 
    }

    @Test
    public void parse() throws Exception
    {
        final Ddm2EmdCrosswalk crosswalk = new Ddm2EmdCrosswalk();
        try
        {
            final EasyMetadata emd = crosswalk.createFrom(new File(input));
            if (emd != null)
                log.debug(input + "\n" + new EmdMarshaller(emd).getXmlString());
            else
                throw new Exception(input//
                        + " passed=" + crosswalk.getXmlErrorHandler().passed()//
                        + " warnings=" + crosswalk.getXmlErrorHandler().getWarnings().size() //
                        + " errors=" + crosswalk.getXmlErrorHandler().getErrors().size() //
                        + " fatalErrors=" + crosswalk.getXmlErrorHandler().getFatalErrors().size()//
                );
        }
        catch (Exception e)
        {
            // wrap the exception to show which one failed
            throw new Exception(input, e);
        }
        finally
        {
            log.debug(input + "\n" + crosswalk.getXmlErrorHandler().getMessages());
        }
    }

    @Parameters
    public static Collection<Object[]> provideConstructorParameters()
    {
        final Collection<Object[]> p = new ArrayList<Object[]>();
        p.add(makeHappyTest("ddm1.xml"));
        p.add(makeHappyTest("ddm2.xml"));
        p.add(makeHappyTest("ddm3.xml"));// warnings
        p.add(makeHappyTest("ddm4.xml"));// warnings
        p.add(makeHappyTest("ddm5.xml"));
        p.add(makeHappyTest("ddm6.xml"));
        p.add(makeHappyTest("ddm7.xml"));
        p.add(makeHappyTest("ddm8.xml"));
        p.add(makeHappyTest("ddm9.xml"));
        p.add(makeHappyTest("maxDDM.xml"));
        return p;
    }

    private static Object[] makeHappyTest(final String string)
    {
        return new Object[] {"src/test/resources/input/" + string};
    }
}
