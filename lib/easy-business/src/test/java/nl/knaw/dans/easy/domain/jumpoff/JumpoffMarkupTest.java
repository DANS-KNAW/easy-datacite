package nl.knaw.dans.easy.domain.jumpoff;

import static org.junit.Assert.*;

import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffMarkup;

import org.junit.Test;

public class JumpoffMarkupTest
{

    @Test
    public void markup()
    {
        JumpoffMarkup jom = new JumpoffMarkup();
        assertEquals("", jom.getMarkup());
        assertEquals("<jumpoff/>", new String(jom.asObjectXML()));

        jom.setMarkup("<div>da</div>");
        assertEquals("<div class=\"jumpoffpage\">\n<div>da</div>\n</div>", jom.getMarkup());
        assertEquals("<jumpoff><div class=\"jumpoffpage\">\n<div>da</div>\n</div></jumpoff>", new String(jom.asObjectXML()));

        jom.setMarkup("");
        assertEquals("", jom.getMarkup());
        assertEquals("<jumpoff/>", new String(jom.asObjectXML()));

        jom.setMarkup(null);
        assertEquals("", jom.getMarkup());
        assertEquals("<jumpoff/>", new String(jom.asObjectXML()));
    }

}
