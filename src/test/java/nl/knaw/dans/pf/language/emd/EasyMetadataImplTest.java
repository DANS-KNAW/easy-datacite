package nl.knaw.dans.pf.language.emd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.pf.language.emd.binding.EasyMetadataFactory;
import nl.knaw.dans.pf.language.emd.exceptions.NoSuchTermException;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.Author;
import nl.knaw.dans.pf.language.emd.types.BasicDate;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.BasicRemark;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;
import nl.knaw.dans.pf.language.emd.types.EmdConstants.DateScheme;
import nl.knaw.dans.pf.language.emd.types.InvalidLanguageTokenException;
import nl.knaw.dans.pf.language.emd.types.IsoDate;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;
import nl.knaw.dans.pf.language.emd.types.Relation;
import nl.knaw.dans.pf.language.emd.types.Spatial;
import nl.knaw.dans.pf.language.emd.types.Spatial.Point;
import nl.knaw.dans.pf.language.emd.util.AbstractJibxTest;
import nl.knaw.dans.pf.language.emd.validation.EasyMetadataValidator;

import org.jibx.runtime.JiBXException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

// ecco: CHECKSTYLE: OFF

public class EasyMetadataImplTest extends AbstractJibxTest<EasyMetadataImpl>
{

    public EasyMetadataImplTest()
    {
        super(EasyMetadataImpl.class);
    }

    //@Test // used in javadoc package description
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

    //@Test // used for development
    public void printTerms()
    {
        EasyMetadata emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        for (Term term : emd.getTermsMap().keySet())
        {
            System.out.println(term.getQualifiedName() + "=true");
        }
    }

    @Test
    public void testMarshalAndUnmarshal() throws IOException, JiBXException, XMLException, SAXException, SchemaCreationException
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        Assert.assertTrue(emd.isEmpty());
        Assert.assertTrue(EasyMetadataValidator.instance().validate(emd).passed());
        String filename = marshal(emd);
        EasyMetadata emd2 = unmarshal(filename);
        Assert.assertTrue(emd2.isEmpty());
        Assert.assertTrue(EasyMetadataValidator.instance().validate(emd2).passed());
    }

    @Test
    public void testMarshalAndUnmarshal2() throws IOException, JiBXException, InvalidLanguageTokenException, URISyntaxException, XMLException, SAXException,
            SchemaCreationException
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        populate(emd);
        Assert.assertFalse(emd.isEmpty());
        Assert.assertTrue(EasyMetadataValidator.instance().validate(emd).passed());
        String filename = marshal(emd, "_pop");
        EasyMetadata emd2 = unmarshal(filename);
        check(emd2);
        Assert.assertTrue(EasyMetadataValidator.instance().validate(emd2).passed());
        System.out.println(emd2.asXMLString(4));
    }

    @Test
    public void testMarshalAndUnMarshal3() throws URISyntaxException, IOException, JiBXException, XMLException, SAXException, SchemaCreationException
    {
        int repeatFill = 2;
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        EmdHelper.populate(repeatFill, emd);
        Assert.assertTrue(EasyMetadataValidator.instance().validate(emd).passed());
        String filename = marshal(emd, "_filled");
        EasyMetadata emd2 = unmarshal(filename);
        for (MDContainer mdContainer : MDContainer.values())
        {
            EmdContainer emdContainer = emd2.getContainer(mdContainer, true);
            for (Term term : emdContainer.getTerms())
            {
                Assert.assertEquals(term.toString(), repeatFill, emdContainer.get(term).size());
            }
        }
        Assert.assertTrue(EasyMetadataValidator.instance().validate(emd2).passed());
    }

    @Test
    public void testNullValues() throws IOException, JiBXException, XMLException, SAXException, SchemaCreationException
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        BasicString bs = new BasicString();
        emd.getEmdTitle().getDcTitle().add(bs);
        marshal(emd, "_nullBasicString");

        Author author = new Author();
        emd.getEmdCreator().getEasCreator().add(author);
        marshal(emd, "_nullAuthor");

        BasicDate bd = new BasicDate();
        emd.getEmdDate().getDcDate().add(bd);
        marshal(emd, "_nullBasicDate");

        BasicIdentifier bi = new BasicIdentifier();
        emd.getEmdIdentifier().getDcIdentifier().add(bi);
        marshal(emd, "_nullBasicIdentifier");

        BasicRemark br = new BasicRemark();
        emd.getEmdOther().getEasRemarks().add(br);
        marshal(emd, "_nullBasicRemark");

        IsoDate id = new IsoDate();
        emd.getEmdDate().getEasDate().add(id);
        marshal(emd, "_nullIsoDate");

        Relation relation = new Relation();
        emd.getEmdRelation().getEasRelation().add(relation);
        marshal(emd, "_nullRelation");

        Spatial spatial = new Spatial();
        emd.getEmdCoverage().getEasSpatial().add(spatial);
        marshal(emd, "_nullSpatial");

        Spatial spatial2 = new Spatial();
        Point point = new Point(null, null, null);
        spatial2.setPoint(point);
        emd.getEmdCoverage().getEasSpatial().add(spatial2);
        marshal(emd, "_nullSpatialPoint");

        Assert.assertTrue(EasyMetadataValidator.instance().validate(emd).passed());

        //System.out.println(emd.toString(";"));
    }

    @Test
    public void testToString() throws URISyntaxException
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        EmdHelper.populate(3, emd);
        String toString = emd.toString(";");
        //System.out.println(toString);
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
        Assert.assertTrue(EasyMetadataValidator.instance().validate(emd).passed());
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
        Assert.assertTrue(EasyMetadataValidator.instance().validate(emd).passed());
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
        //        for (Term term : map.keySet())
        //        {
        //            System.out.println(term.getName() + " " + map.get(term));
        //        }
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

    //    @Test(expected = SchemaCreationException.class)
    //    public void testVersion() throws IOException, JiBXException, SAXException, XMLException, SchemaCreationException
    //    {
    //        EasyMetadata emd = unmarshal(VERSION_TEST_FILE);
    //        Assert.assertEquals("version read from file", emd.getVersion());
    //        EasyMetadataValidator.instance().validate(emd);
    //    }

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
            //System.out.println(mdContainer.toString());
            //                for (String value : values)
            //                {                   
            //                    System.out.println("\t" + value);
            //                }
        }

        System.out.println(dc.asXMLString(4));
    }

    @Test
    public void applicationSpecific() throws XMLException, SAXException, IOException, JiBXException, SchemaCreationException
    {
        EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        Assert.assertEquals(MetadataFormat.UNSPECIFIED, emd.getEmdOther().getEasApplicationSpecific().getMetadataFormat());
        emd.getEmdOther().getEasApplicationSpecific().setMetadataFormat(MetadataFormat.ARCHAEOLOGY);
        Assert.assertTrue(EasyMetadataValidator.instance().validate(emd).passed());

        String filename = marshal(emd);
        EasyMetadata emd2 = unmarshal(filename);

        Assert.assertTrue(EasyMetadataValidator.instance().validate(emd2).passed());
        Assert.assertEquals(MetadataFormat.ARCHAEOLOGY, emd2.getEmdOther().getEasApplicationSpecific().getMetadataFormat());
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
                //System.out.println(container.getClass());
                return count++;
            }

        });
        int count = (Integer) object;
        Assert.assertFalse(count == 0);
    }

    @Test
    public void testDirty()
    {
        EasyMetadata emd = EasyMetadataFactory.newEasyMetadata(MetadataFormat.UNSPECIFIED);
        assertFalse(emd.isDirty());
        ((EasyMetadataImpl) emd).setDirty(false);
        assertFalse(emd.isDirty());
        emd.getEmdAudience(); // lazy initialisation
        assertTrue(emd.isDirty());
        emd.getEmdAudience().getTermsAudience();
        assertTrue(emd.isDirty());

        emd.setDirty(false);
        assertFalse(emd.isDirty());

        emd.getEmdAudience().getTermsAudience().add(new BasicString(""));
        assertTrue(emd.isDirty());
        //System.err.println(emd.toString("|"));
        ((EasyMetadataImpl) emd).setDirty(false);
        assertFalse(emd.isDirty());
        emd.getEmdAudience().getTermsAudience().add(new BasicString(""));
        assertTrue(emd.isDirty());
        ((EasyMetadataImpl) emd).setDirty(false);
        emd.getEmdAudience().getTermsAudience().add(new BasicString("a"));
        assertTrue(emd.isDirty());
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
        //         <dcterms:created>bla bla</dcterms:created>
        //         <dcterms:created eas:scheme="W3CDTF">2011-07-14</dcterms:created>

        //System.out.println(emd.asXMLString(4));
    }

    @Ignore("Performance test.")
    @Test
    public void testDirtyPerformance() throws URISyntaxException
    {
        EasyMetadata emd = EasyMetadataFactory.newEasyMetadata(MetadataFormat.UNSPECIFIED);
        EmdHelper.populate(10, emd);
        int count = 100000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
        {
            ((EasyMetadataImpl) emd).setDirty(false); // 0.40 ms. at populate 1, 0.96 ms. at populate 10 -- toString("|")
        }
        long end = System.currentTimeMillis();
        System.err.println(end - start);

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
        {
            emd.isDirty(); // 0.41 ms. at populate 1, 1.00 ms. at populate 10 -- toString("|")
        }
        end = System.currentTimeMillis();
        System.err.println(end - start);

        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
        {
            emd.setVersionable(1 % 2 == 0); // 0.00001 ms.
        }
        end = System.currentTimeMillis();
        System.err.println(end - start);

        // a simple call to a boolean setter is about 40000 to 100000 times faster than a call to emd.setDirty(false)
        // with string comparison.
    }

    private void check(EasyMetadata emd2)
    {
        Assert.assertFalse(emd2.isEmpty());
        Assert.assertEquals("0.1", emd2.getVersion());

        Assert.assertEquals("title foo", emd2.getEmdTitle().getDcTitle().get(0).getValue());
        Assert.assertEquals("nld-NLD", emd2.getEmdTitle().getDcTitle().get(0).getLanguage());
        Assert.assertEquals("easy", emd2.getEmdTitle().getDcTitle().get(0).getScheme());
        Assert.assertEquals("alternative foo", emd2.getEmdTitle().getTermsAlternative().get(0).getValue());

        Assert.assertEquals("dc creator", emd2.getEmdCreator().getDcCreator().get(0).getValue());
        Assert.assertEquals("NOP", emd2.getEmdCreator().getDcCreator().get(0).getScheme());
        Assert.assertEquals("Fields, DC", emd2.getEmdCreator().getEasCreator().get(0).toString());
        Assert.assertEquals("http://www.rug.nl/", emd2.getEmdCreator().getEasCreator().get(1).getIdentificationSystem().toString());

        Assert.assertEquals("subject foo", emd2.getEmdSubject().getDcSubject().get(0).getValue());

        Assert.assertEquals("dc description", emd2.getEmdDescription().getDcDescription().get(0).getValue());
        Assert.assertEquals("1. one; 2. two", emd2.getEmdDescription().getTermsTableOfContents().get(0).getValue());
        Assert.assertEquals("bla bla", emd2.getEmdDescription().getTermsAbstract().get(0).getValue());

        Assert.assertEquals("VERLAG", emd2.getEmdPublisher().getDcPublisher().get(0).getScheme());

        Assert.assertEquals("dc contributor", emd2.getEmdContributor().getDcContributor().get(0).getValue());
        Assert.assertEquals("Isigrim, Herr Doctor I von", emd2.getEmdContributor().getEasContributor().get(0).toString());

        BasicDate bd = emd2.getEmdDate().getDcDate().get(0);
        Assert.assertEquals("maart '97", bd.getValue());
        Assert.assertNull(bd.getDateTime());
        Assert.assertNull(bd.getScheme());

        bd = emd2.getEmdDate().getTermsCreated().get(0);
        Assert.assertEquals("2008", bd.getValue());
        Assert.assertTrue(bd.getDateTime().toString().startsWith("2008"));
        Assert.assertEquals(DateScheme.W3CDTF, bd.getScheme());

        Assert.assertEquals("2008-08", emd2.getEmdDate().getTermsValid().get(0).getValue());
        Assert.assertEquals("2008-08-05", emd2.getEmdDate().getTermsAvailable().get(0).getValue());
        Assert.assertEquals("2008-08-05T10", emd2.getEmdDate().getTermsIssued().get(0).getValue());
        Assert.assertEquals("2008-08-05T10:02", emd2.getEmdDate().getTermsModified().get(0).getValue());
        Assert.assertEquals("2008-08-05T10:02:11", emd2.getEmdDate().getTermsDateSubmitted().get(0).getValue());

        IsoDate eDate = emd2.getEmdDate().getEasDate().get(0);
        eDate.setFormat(IsoDate.Format.MILLISECOND);
        Assert.assertEquals("2008-08-05T01:17:24.898+0200", eDate.toString());
        eDate = emd2.getEmdDate().getEasDate().get(1);
        Assert.assertEquals("2008-08", eDate.toString());

        Assert.assertEquals("foo type", emd2.getEmdType().getDcType().get(0).getValue());

        Assert.assertEquals("http://isbn.org", emd2.getEmdIdentifier().getDcIdentifier().get(0).getIdentificationSystem().toString());
        Assert.assertEquals(EmdConstants.SCHEME_ISBN, emd2.getEmdIdentifier().getDcIdentifier().get(0).getScheme());

        Assert.assertEquals("foo source", emd2.getEmdSource().getDcSource().get(0).getValue());

        Assert.assertEquals("foo language", emd2.getEmdLanguage().getDcLanguage().get(0).getValue());

        Assert.assertEquals("FOOid", emd2.getEmdRelation().getDcRelation().get(0).getValue());

        Assert.assertEquals("Amsterdam", emd2.getEmdCoverage().getEasSpatial().get(0).getPlace().getValue());
        Assert.assertEquals("scheme=foo x=1.2 y=2.30", emd2.getEmdCoverage().getEasSpatial().get(0).getPoint().toString());

        Assert.assertEquals("incomplete", emd2.getEmdCoverage().getEasSpatial().get(1).getPlace().getValue());
        Assert.assertEquals("scheme=foo x=null y=2.30", emd2.getEmdCoverage().getEasSpatial().get(1).getPoint().toString());

        Assert.assertEquals("DANS", emd2.getEmdRights().getTermsLicense().get(0).getScheme());
        Assert.assertEquals("more blah", emd2.getEmdRights().getTermsRightsHolder().get(0).getValue());

        Assert.assertEquals("foo audience", emd2.getEmdAudience().getTermsAudience().get(0).getValue());

        Assert.assertEquals("henk van den berg", emd2.getEmdOther().getEasRemarks().get(0).getAuthor());
    }

    private void populate(EasyMetadata emd) throws URISyntaxException
    {
        emd.getEmdTitle().getDcTitle().add(new BasicString("title foo", new Locale("nl", "NL"), "easy"));
        emd.getEmdTitle().getDcTitle().add(new BasicString("title bar"));
        emd.getEmdTitle().getTermsAlternative().add(new BasicString("alternative foo"));
        emd.getEmdTitle().getTermsAlternative().add(new BasicString("alternative bar", new Locale("en", "us"), "not-so-easy"));

        emd.getEmdCreator().getDcCreator().add(new BasicString("dc creator", new Locale("fr", "FR"), "NOP"));
        emd.getEmdCreator().getEasCreator().add(new Author(null, "dc", null, "Fields"));
        Author creator = new Author("prof. dr.", "abcd", "de la", "Foo");
        creator.setEntityId("13579");
        creator.setIdentificationSystem(new URI("http://www.rug.nl/"));
        emd.getEmdCreator().getEasCreator().add(creator);

        emd.getEmdSubject().getDcSubject().add(new BasicString("subject foo"));
        emd.getEmdSubject().getDcSubject().add(new BasicString("subject bar", new Locale("de", "de"), "ABC"));

        emd.getEmdDescription().getDcDescription().add(new BasicString("dc description", new Locale("es", "es")));
        emd.getEmdDescription().getTermsTableOfContents().add(new BasicString("1. one; 2. two"));
        emd.getEmdDescription().getTermsAbstract().add(new BasicString("bla bla"));

        emd.getEmdPublisher().getDcPublisher().add(new BasicString("publisher foo", new Locale("se", "se"), "VERLAG"));

        emd.getEmdContributor().getDcContributor().add(new BasicString("dc contributor", new Locale("si", "si"), ""));
        emd.getEmdContributor().getEasContributor().add(new Author("Herr Doctor", "I", "von", "Isigrim"));

        emd.getEmdDate().getDcDate().add(new BasicDate("maart '97"));
        emd.getEmdDate().getTermsCreated().add(new BasicDate("2008"));
        emd.getEmdDate().getTermsValid().add(new BasicDate("2008-08", new Locale("be", "be"), DateScheme.Period));
        emd.getEmdDate().getTermsAvailable().add(new BasicDate("2008-08-05"));
        emd.getEmdDate().getTermsIssued().add(new BasicDate("2008-08-05T10"));
        emd.getEmdDate().getTermsModified().add(new BasicDate("2008-08-05T10:02"));
        emd.getEmdDate().getTermsDateSubmitted().add(new BasicDate("2008-08-05T10:02:11"));
        //emd.getEmdDate().getTermsDateAccepted().add(new BasicDate("2008-08-20T10:02:11"));

        emd.getEmdDate().getEasDate().add(new IsoDate("2008-08-05T01:17:24.898+0200"));
        IsoDate edate = new IsoDate("2008-08");
        emd.getEmdDate().getEasDate().add(edate);

        emd.getEmdType().getDcType().add(new BasicString("foo type", new Locale("fo", "ba"), "TYPO"));

        emd.getEmdFormat().getTermsMedium().add(new BasicString("foo medium", new Locale("he", "be"), EmdConstants.SCHEME_IMT));

        BasicIdentifier bi = new BasicIdentifier("AA123", new Locale("ff", "do"), EmdConstants.SCHEME_ISBN);
        bi.setIdentificationSystem(new URI("http://isbn.org"));
        emd.getEmdIdentifier().getDcIdentifier().add(bi);

        BasicIdentifier simple = new BasicIdentifier("foo source");
        emd.getEmdSource().getDcSource().add((BasicIdentifier) simple);

        emd.getEmdLanguage().getDcLanguage().add(new BasicString("foo language"));

        BasicIdentifier bi2 = new BasicIdentifier("FOOid", new Locale("aa", "mo"), EmdConstants.SCHEME_ISBN);
        emd.getEmdRelation().getDcRelation().add(bi2);

        BasicIdentifier relationIdentifier = new BasicIdentifier("ask for info", new Locale("ho", "la"), null);
        relationIdentifier.setIdentificationSystem(new URI("mailto:info@dans.knaw.nl"));
        Relation relation = new Relation();
        relation.setEmphasis(true);

        relation.setSubjectTitle("Easy");
        relation.setSubjectIdentifier(relationIdentifier);
        relation.setSubjectLink(new URI("http://some.org/somepage"));
        emd.getEmdRelation().getEasRelation().add(relation);

        emd.getEmdCoverage().getEasSpatial().add(new Spatial("Amsterdam", new Spatial.Point("foo", "1.2", "2.30")));
        emd.getEmdCoverage().getEasSpatial().add(new Spatial("incomplete", new Spatial.Point("foo", null, "2.30")));

        emd.getEmdRights().getTermsLicense().add(new BasicString("bla die bla", new Locale("pa", "ma"), "DANS"));
        emd.getEmdRights().getTermsRightsHolder().add(new BasicString("more blah", new Locale("pa", "ma"), "DANS"));
        emd.getEmdAudience().getTermsAudience().add(new BasicString("foo audience"));

        BasicRemark remark = new BasicRemark("--EINDE--");
        remark.setAuthor("henk van den berg");
        emd.getEmdOther().getEasRemarks().add(remark);
    }

}
