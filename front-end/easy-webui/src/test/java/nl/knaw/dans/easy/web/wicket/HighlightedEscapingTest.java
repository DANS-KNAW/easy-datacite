package nl.knaw.dans.easy.web.wicket;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HighlightedEscapingTest {

    @Test
    public void test() {
        String input = "<em>keep the em <tags> but</em> not <script>alert('Injected!');</script>";
        String expected = "<em>keep the em &lt;tags&gt; but</em> not &lt;script&gt;alert(&#039;Injected!&#039;);&lt;/script&gt;";
        String escaped = HighlightedCharSequence.escapeString(input);
        assertEquals(expected, escaped);
    }

}
