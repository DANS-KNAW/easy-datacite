package nl.knaw.dans.easy.web.fileexplorer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ReadableValuesTest {

    @Test
    public void empty() {
        assertThat(new ReadableValues(set()).toString(), is(""));
    }

    @Test
    public void oneValue() {
        assertThat(new ReadableValues(set("one")).toString(), is("One"));
    }

    @Test
    public void twoValues() {
        assertThat(new ReadableValues(set("one", "two")).toString(), is("One, Two"));
    }

    @Test
    public void sorted() {
        assertThat(new ReadableValues(set("z-last", "a-first")).toString(), is("A-first, Z-last"));
    }

    @Test
    public void lowercaseRestOfChars() {
        assertThat(new ReadableValues(set("ONE", "TWO")).toString(), is("One, Two"));
    }

    @Test
    public void underscoreToSpace() {
        assertThat(new ReadableValues(set("Request_permission", "Group_permission")).toString(), is("Group permission, Request permission"));
    }

    private Set<Object> set(Object... os) {
        return new HashSet<Object>(Arrays.asList(os));
    }

}
