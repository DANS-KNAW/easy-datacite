package nl.knaw.dans.easy.web.wicket;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class HighlightedCharSequenceSubSequenceTest {
    private String input;
    private int begin;
    private int end;
    private String expected;

    public HighlightedCharSequenceSubSequenceTest(String input, int begin, int end, String expected) {
        this.input = input;
        this.begin = begin;
        this.end = end;
        this.expected = expected;
    }

    @Test
    public void test() {
        assertEquals(expected, new HighlightedCharSequence(input).subSequence(begin, end));
    }

    @Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                // @formatter:off
                {"012345", 0, 6, "012345"}, {"012345", 1, 6, "12345"}, {"012345", 0, 5, "01234"}, {"<em>012345</em>", 0, 6, "<em>012345</em>"},
                {"<em>012345</em>", 1, 6, "<em>12345</em>"}, {"<em>012345</em>", 0, 5, "<em>01234</em>"}, {"012<em>3</em>45", 2, 4, "2<em>3</em>"},
                {"012<em>34</em>56", 2, 4, "2<em>3</em>"}, {"<em>012</em>3<em>45</em>", 2, 5, "<em>2</em>3<em>4</em>"},
                {"<em>012</em>3<em>4</em>5", 2, 6, "<em>2</em>3<em>4</em>5"}
        // @formatter:on                
                });
    }
}
