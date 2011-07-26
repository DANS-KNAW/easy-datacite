package nl.knaw.dans.easy.fedora;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( {
    TesterTest.class
})
public class AllTests
{
    private AllTests()
    {
        // never instantiate
    }

}

