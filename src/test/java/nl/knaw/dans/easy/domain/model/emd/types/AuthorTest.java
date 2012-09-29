package nl.knaw.dans.easy.domain.model.emd.types;

import nl.knaw.dans.easy.domain.model.emd.types.Author;
import nl.knaw.dans.easy.domain.model.emd.types.EmdConstants;

import org.junit.Assert;
import org.junit.Test;

// ecco: CHECKSTYLE: OFF

public class AuthorTest
{

    @Test
    public void testConstructor()
    {
        Author author = new Author();
        Assert.assertEquals(null, author.getScheme());
    }

    @Test
    public void testToString()
    {
        Author author = new Author("prof. dr. ir.", "HBAM", "van den", "Berg");
        Assert.assertEquals("Berg, prof. dr. ir. HBAM van den", author.toString());
        author.setTitle("");
        Assert.assertEquals("Berg, HBAM van den", author.toString());
        author.setTitle(" ");
        Assert.assertEquals("Berg, HBAM van den", author.toString());
        author.setTitle(null);
        Assert.assertEquals("Berg, HBAM van den", author.toString());
        author.setInitials(" hbam  ");
        Assert.assertEquals("Berg, HBAM van den", author.toString());

        Assert.assertEquals(null, author.getScheme());
        author.setEntityId("123");
        Assert.assertEquals("123", author.getEntityId());
        Assert.assertEquals(EmdConstants.SCHEME_DAI, author.getScheme());
    }

}
