package nl.knaw.dans.easy.sos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Test;

public class StoredObjectTest
{
    private static final int componentLength = 4;
    private static final int uuidStringLength = 32;

    /**
     * Checks some properties of the relative path, such as its length and the characters that must be
     * slashes.
     */
    @Test
    public void correctRelativePath()
    {
        String p = new StoredObject().getRelativePath();
        assertThat(p.length(), is(uuidStringLength + (uuidStringLength / componentLength) - 1));
        assertThat(p.charAt(componentLength * 1), is('/'));
        assertThat(p.charAt(componentLength * 2 + 1), is('/'));
        assertThat(p.charAt(componentLength * 3 + 2), is('/'));
        assertThat(p.charAt(componentLength * 4 + 3), is('/'));
        assertThat(p.charAt(componentLength * 5 + 4), is('/'));
        assertThat(p.charAt(componentLength * 6 + 5), is('/'));
        assertThat(p.charAt(componentLength * 7 + 6), is('/'));
        assertThat(p.charAt(p.length() - 1), is(not('/')));
    }

}
