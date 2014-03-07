package nl.knaw.dans.common.lang.user;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

public class PersonVOTest
{

    @Test
    public void telephonePattern()
    {
        assertTrue(Pattern.matches(PersonVO.PATTERN_TELEPHONE, "12345-67890"));
        assertTrue(Pattern.matches(PersonVO.PATTERN_TELEPHONE, "22-33"));
        assertFalse(Pattern.matches(PersonVO.PATTERN_TELEPHONE, "-"));
        assertFalse(Pattern.matches(PersonVO.PATTERN_TELEPHONE, " "));
        assertFalse(Pattern.matches(PersonVO.PATTERN_TELEPHONE, "  "));
    }

}
