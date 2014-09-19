package nl.knaw.dans.easy.web.wicket;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class HighlightedCharSequenceCharAtTest {
    private String input;
    private int charAt;
    private char expected;

    public HighlightedCharSequenceCharAtTest(String input, int charAt, char expected) {
        this.input = input;
        this.charAt = charAt;
        this.expected = expected;
    }

    @Test
    public void test() {
        assertEquals(expected, new HighlightedCharSequence(input).charAt(charAt));
    }

    @Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                // @formatter:off
                {"A <em>highlighted</em> string", 0, 'A'}, {"A <em>highlighted</em> string", 1, ' '}, {"A <em>highlighted</em> string", 2, 'h'},
                {"A <em>highlighted</em> string", 3, 'i'}, {"A <em>highlighted</em> string", 12, 'd'}, {"A <em>highlighted</em> string", 13, ' '},
                {"A <em>highlighted</em> string", 19, 'g'}, {"<em>0</em>", 0, '0'}, {"<em>0</em><em>1</em>", 1, '1'}, {"<em>0</em><em>1</em>2", 2, '2'},
                {"<em>0</em>1<em>2</em>", 1, '1'}
        // @formatter:on                
                });
    }
}
