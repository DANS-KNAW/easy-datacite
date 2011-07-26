package nl.knaw.dans.easy.web.wicket;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ShortenedCharSequenceModelTest
{
    private CharSequence input;
    private int shortenCount;
    private String expected;

    public ShortenedCharSequenceModelTest(CharSequence input, int shortenCount, String expected)
    {
        this.input = input;
        this.shortenCount = shortenCount;
        this.expected = expected;
    }

    @Test
    public void valid()
    {
        assertEquals(expected, new ShortenedCharSequenceModel(input, shortenCount, "...").getObject());
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][] {
                    // @formatter:off
                    {"1234567890123", 10, "1234567890..."}, 
                    {"", 10, ""}, 
                    {"", 0, ""}, 
                    {"1", 0, "..."}, 
                    {"12345", 0, "..."}, 
                    {"12345", 5, "12345"}, 
                    {"123456", 5, "12345..."}, 
                    {hlCharSeq("1234<em>56</em>"), 5, "1234<em>5</em>..."}, 
                    {hlCharSeq("1234<em>56</em>"), 6, "1234<em>56</em>"}, 
                    {hlCharSeq("1<em>23</em>4<em>56</em>"), 4, "1<em>23</em>4..."}
                    // @formatter:on
                });

    }

    private static CharSequence hlCharSeq(String markup)
    {
        return new HighlightedCharSequence(markup);
    }

}
