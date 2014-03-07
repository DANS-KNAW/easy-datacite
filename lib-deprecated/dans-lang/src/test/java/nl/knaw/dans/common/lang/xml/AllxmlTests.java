package nl.knaw.dans.common.lang.xml;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( {SchemaCacheTest.class, XMLErrorHandlerTest.class, XMLValidatorTest.class})
public class AllxmlTests
{
    private AllxmlTests()
    {
        // never instantiate
    }

}
