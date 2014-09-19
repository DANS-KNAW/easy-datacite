package nl.knaw.dans.pf.language.emd;

import nl.knaw.dans.pf.language.emd.Term;
import nl.knaw.dans.pf.language.emd.types.BasicDate;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

import org.junit.Assert;
import org.junit.Test;

// ecco: CHECKSTYLE: OFF

public class EmdDateTest {

    @Test
    public void testToString() {
        EmdDate emdDate = new EmdDate();
        Assert.assertEquals("", emdDate.toString());
        Assert.assertEquals("", emdDate.toString(", "));
        Assert.assertEquals("", emdDate.toString(", ", true));
        Assert.assertEquals("", emdDate.toString(", ", false));

        emdDate.getDcDate().add(new BasicDate("bar"));
        Assert.assertEquals("bar", emdDate.toString());
        Assert.assertEquals("bar", emdDate.toString(", "));
        Assert.assertEquals("\ndate, http://purl.org/dc/elements/1.1/, bar", emdDate.toString(", ", true));
        Assert.assertEquals("bar", emdDate.toString(", ", false));

        BasicDate bdate = new BasicDate("2008-08-19");
        emdDate.getTermsCreated().add(bdate);
        Assert.assertEquals("bar;2008-08-19", emdDate.toString());
        Assert.assertEquals("bar, 2008-08-19", emdDate.toString(", "));
        Assert.assertEquals("\ndate, http://purl.org/dc/elements/1.1/, bar\ncreated, http://purl.org/dc/terms/, 2008-08-19", emdDate.toString(", ", true));
        Assert.assertEquals("bar, 2008-08-19", emdDate.toString(", ", false));

        IsoDate idate = new IsoDate("2008-08-20");
        emdDate.getEasDate().add(idate);
        Assert.assertEquals("bar;2008-08-19;2008-08-20", emdDate.toString());
        Assert.assertEquals("bar, 2008-08-19, 2008-08-20", emdDate.toString(", "));
        Assert.assertEquals(
                "\ndate, http://purl.org/dc/elements/1.1/, bar\ncreated, http://purl.org/dc/terms/, 2008-08-19\ndate, http://easy.dans.knaw.nl/easy/easymetadata/eas/, 2008-08-20",
                emdDate.toString(", ", true));
        Assert.assertEquals("bar, 2008-08-19, 2008-08-20", emdDate.toString(", ", false));

        // emdDate.getEasDate().add(new IsoDate());
        // System.out.println(emdDate.toString(", ", true));
    }

    @Test
    public void testTermToString() {
        EmdDate emdDate = new EmdDate();
        emdDate.getDcDate().add(new BasicDate("bar"));
        emdDate.getEasDate().add(new IsoDate());
        Assert.assertEquals("", emdDate.toString(", ", new Term(Term.Name.VALID, Term.Namespace.DCTERMS)));

        emdDate.getTermsValid().add(new BasicDate("2020-10-31"));
        Assert.assertEquals("2020-10-31", emdDate.toString(", ", new Term(Term.Name.VALID, Term.Namespace.DCTERMS)));

        IsoDate idate = new IsoDate("2008-08-20");
        emdDate.getEasValid().add(idate);

        Assert.assertEquals("2020-10-31, 2008-08-20", emdDate.toString(", ", new Term(Term.Name.VALID)));

    }

}
