package nl.knaw.dans.pf.language.emd;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.pf.language.emd.binding.EasyMetadataFactory;
import nl.knaw.dans.pf.language.emd.exceptions.NoSuchTermException;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicDate;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.IsoDate;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

// ecco: CHECKSTYLE: OFF

public class EasyMetadataImplTest
{

    // @Test // used in javadoc package description
    public void printContainersAndTerms()
    {
        EasyMetadata emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        for (MDContainer mdContainer : MDContainer.values())
        {
            System.out.println(mdContainer.toString());
            EmdContainer emdContainer = emd.getContainer(mdContainer, false);
            for (Term term : emdContainer.getTerms())
            {
                System.out.println("\t" + term.getNamespace().prefix + ":" + term.getName().termName + " ({@link " + term.getType().getName() + "})");
            }

        }
    }

    // @Test // used for development
    public void printTerms()
    {
        EasyMetadata emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        for (Term term : emd.getTermsMap().keySet())
        {
            System.out.println(term.getQualifiedName());
        }
    }

    @Test
    public void testToString() throws URISyntaxException
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        EmdHelper.populate(3, emd);
        String toString = emd.toString(";");
        // System.out.println(toString);
        Assert.assertTrue(toString.startsWith("title;http://purl.org/dc/elements/1.1/;title 0;title 1;title 2"));
        Assert.assertTrue(toString.endsWith("remarks;http://easy.dans.knaw.nl/easy/easymetadata/eas/;remarks 0;remarks 1;remarks 2"));
    }

    @Test
    public void testContainerToString() throws XMLException, SAXException, SchemaCreationException
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        Assert.assertEquals("", emd.toString("&", MDContainer.Audience));

        emd.getEmdAudience().getTermsAudience().add(new BasicString("xyz"));
        Assert.assertEquals("xyz", emd.toString("&", MDContainer.Audience));

        emd.getEmdAudience().getTermsAudience().add(new BasicString("abc"));
        Assert.assertEquals("xyz&abc", emd.toString("&", MDContainer.Audience));
    }

    @Test
    public void testTermToString() throws XMLException, SAXException, SchemaCreationException
    {
        Term term1 = new Term(Term.Name.RIGHTS, Term.Namespace.DC);
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        Assert.assertEquals("", emd.toString("*", term1));

        emd.getEmdRights().getDcRights().add(new BasicString("forbidden"));
        Assert.assertEquals("forbidden", emd.toString("*", term1));

        emd.getEmdRights().getDcRights().add(new BasicString("strictly"));
        Assert.assertEquals("forbidden*strictly", emd.toString("*", term1));

        Term created = new Term(Term.Name.CREATED, Term.Namespace.DCTERMS);
        Assert.assertEquals("", emd.toString("*", created));

        emd.getEmdDate().getTermsCreated().add(new BasicDate("1992-1994"));
        Assert.assertEquals("1992-1994", emd.toString("*", created));

        emd.getEmdDate().getEasCreated().add(new IsoDate("2004"));
        Assert.assertEquals("1992-1994", emd.toString("*", created));

        created = new Term(Term.Name.CREATED);
        Assert.assertEquals("1992-1994*2004", emd.toString("*", created));
    }

    @Test(expected = NoSuchTermException.class)
    public void testNoSuchTermException()
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        Term created = new Term(Term.Name.CREATED, Term.Namespace.DC);
        emd.toString(".. will throw exception ..", created);
    }

    @Test
    public void testGetTermsMap()
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        Map<Term, MDContainer> map = emd.getTermsMap();
        // for (Term term : map.keySet())
        // {
        // System.out.println(term.getName() + " " + map.get(term));
        // }
        Assert.assertEquals(74, map.size());

        Term term = new Term(Term.Name.TITLE, Term.Namespace.DC);
        Assert.assertNotNull(emd.getTermsMap().get(term));
        Assert.assertEquals(MDContainer.Title, emd.getTermsMap().get(term));
    }

    @Test
    public void testGetContainer()
    {
        EasyMetadata emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        Assert.assertNull(emd.getContainer(MDContainer.Title, true));
        EmdTitle titleContainer = emd.getEmdTitle();
        Assert.assertSame(titleContainer, emd.getContainer(MDContainer.Title, true));

        Assert.assertNotNull(emd.getContainer(MDContainer.Creator, false));
    }

    @Test
    public void testGetTerm()
    {
        EasyMetadata emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        List<MetadataItem> titles = emd.getTerm(new Term(Term.Name.TITLE, Term.Namespace.DC));
        Assert.assertNotNull(titles);
        Assert.assertSame(titles, emd.getEmdTitle().getDcTitle());
    }

    // @Test(expected = SchemaCreationException.class)
    // public void testVersion() throws IOException, JiBXException, SAXException, XMLException,
    // SchemaCreationException
    // {
    // EasyMetadata emd = unmarshal(VERSION_TEST_FILE);
    // Assert.assertEquals("version read from file", emd.getVersion());
    // EasyMetadataValidator.instance().validate(emd);
    // }

    @Test
    public void testDublinCoreMetadata() throws Exception
    {
        EasyMetadata emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        DublinCoreMetadata dc = emd.getDublinCoreMetadata();
        for (PropertyName name : PropertyName.values())
        {
            List<String> values = dc.get(name);
            Assert.assertTrue(values.isEmpty());
        }

        EmdHelper.populate(3, (EasyMetadataImpl) emd);
        dc = emd.getDublinCoreMetadata();
        for (PropertyName name : PropertyName.values())
        {
            List<String> values = dc.get(name);
            Assert.assertFalse(values.isEmpty());
            // System.out.println(mdContainer.toString());
            // for (String value : values)
            // {
            // System.out.println("\t" + value);
            // }
        }

    }

    @Test
    public void visitChildrenNonEmpty()
    {
        EasyMetadata emd = EasyMetadataFactory.newEasyMetadata(MetadataFormat.UNSPECIFIED);
        Object object = emd.visitChildren(false, new EmdVisitor()
        {
            private int count;

            public Object container(EmdContainer container)
            {
                return count++;
            }

        });
        Assert.assertNull(object);
    }

    @Test
    public void visitChildrenEvenEmpty()
    {
        EasyMetadata emd = EasyMetadataFactory.newEasyMetadata(MetadataFormat.UNSPECIFIED);
        Object object = emd.visitChildren(true, new EmdVisitor()
        {
            private int count;

            public Object container(EmdContainer container)
            {
                // System.out.println(container.getClass());
                return count++;
            }

        });
        int count = (Integer) object;
        Assert.assertFalse(count == 0);
    }

    @Test
    public void dcTermsCreated() throws Exception
    {
        EasyMetadata emd = new EasyMetadataImpl();
        BasicDate bd = new BasicDate("bla bla");
        emd.getEmdDate().getTermsCreated().add(bd);
        bd = new BasicDate("2011-07-14");
        emd.getEmdDate().getTermsCreated().add(bd);

        // gives
        // <dcterms:created>bla bla</dcterms:created>
        // <dcterms:created eas:scheme="W3CDTF">2011-07-14</dcterms:created>

        // System.out.println(emd.asXMLString(4));
    }

}
