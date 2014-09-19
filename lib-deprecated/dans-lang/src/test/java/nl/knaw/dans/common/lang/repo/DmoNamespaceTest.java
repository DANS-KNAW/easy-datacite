package nl.knaw.dans.common.lang.repo;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class DmoNamespaceTest {

    @Test(expected = IllegalArgumentException.class)
    public void newNull() {
        new DmoNamespace(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newEmpty() {
        new DmoNamespace("");
    }

    @Test
    public void newMatch() {
        new DmoNamespace("foo");
        new DmoNamespace("foo-bar");
        new DmoNamespace("foo-123");
        new DmoNamespace("foo-bar123-9-D-Y");
    }

    @Test
    public void nonMatch() {
        assertFalse(isPassingMatch(" "));
        assertFalse(isPassingMatch("abc#"));
        assertFalse(isPassingMatch("\""));
        assertFalse(isPassingMatch("."));
        assertFalse(isPassingMatch("{}"));
        assertFalse(isPassingMatch(";"));
        assertFalse(isPassingMatch("@"));
    }

    private boolean isPassingMatch(String value) {
        try {
            new DmoNamespace(value);
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

}
