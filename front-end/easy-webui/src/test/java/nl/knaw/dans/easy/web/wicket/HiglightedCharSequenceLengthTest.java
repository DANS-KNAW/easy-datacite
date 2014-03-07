package nl.knaw.dans.easy.web.wicket;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class HiglightedCharSequenceLengthTest
{
    private String input;
    private int expected;

    public HiglightedCharSequenceLengthTest(String input, int expected)
    {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void test()
    {
        assertEquals(expected, new HighlightedCharSequence(input).length());
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][] {// @formatter:off
                {"<em></em>", 0}, {"", 0}, {"1", 1}, {"<em>1</em>", 1}, {"0<em>1</em>2<em>34</em>5", 6}
                // @formatter:on
                });
    }
}
