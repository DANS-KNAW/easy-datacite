package nl.knaw.dans.platform.language.pakbon;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class PakbonValidatorCredentialsTest
{

    @Test(expected = IllegalStateException.class)
    public void testSingleton() throws Exception
    {
        PakbonValidatorCredentials.reset();
        PakbonValidatorCredentials.instance();
    }

    @Test
    public void testUsernameAndPass() throws Exception
    {
        new PakbonValidatorCredentials("user", "pass");
        assertThat(PakbonValidatorCredentials.instance().getUsername(), is("user"));
        assertThat(PakbonValidatorCredentials.instance().getPassword(), is("pass"));
    }

}
