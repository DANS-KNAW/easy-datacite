package nl.knaw.dans.pf.language.emd;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import nl.knaw.dans.pf.language.emd.types.Author;
import nl.knaw.dans.pf.language.emd.types.BasicDate;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.BasicRemark;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.IsoDate;
import nl.knaw.dans.pf.language.emd.types.Relation;
import nl.knaw.dans.pf.language.emd.types.Spatial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.PakbonStatus.*;

// ecco: CHECKSTYLE: OFF

public class EmdHelper
{
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyMetadataImpl.class);

    public static void populate(int repeatFill, EasyMetadata emd) throws URISyntaxException
    {
        for (MDContainer mdContainer : MDContainer.values())
        {
            EmdContainer emdContainer = emd.getContainer(mdContainer, false);

            populate(repeatFill, emdContainer);
        }

    }

    public static void populate(int repeatFill, EmdContainer emdContainer) throws URISyntaxException
    {
        for (Term term : emdContainer.getTerms())
        {
            List<?> list = emdContainer.get(term);
            fill(list, term, repeatFill);
        }
        if (emdContainer instanceof EmdOther)
        {
            EmdOther emdOther = (EmdOther) emdContainer;

            for (int i = 0; i < repeatFill; i++)
            {
                PropertyList propertyList = new PropertyList();
                propertyList.setComment("PropertyList " + i);
                propertyList.addProperty("key 1", "value 1");
                propertyList.addProperty("key 2", "value 2");
                emdOther.add(propertyList);
            }

            emdOther.getEasApplicationSpecific().setPakbonStatus(IMPORTED);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void fill(List list, Term term, int times) throws URISyntaxException
    {
        if (term.getType() == null)
        {
            LOGGER.warn("No type set for " + term.toString());
            return;
        }

        if (term.getType().equals(BasicString.class))
        {
            for (int i = 0; i < times; i++)
            {
                BasicString bs = getBasicString(term, i);
                list.add(bs);
            }
        }
        else if (term.getType().equals(Author.class))
        {
            for (int i = 0; i < times; i++)
            {
                Author author = new Author();
                author.setEntityId("123" + i);
                author.setIdentificationSystem(new URI("http://author.org/sys" + i));
                author.setInitials("abc");
                author.setPrefix("van");
                author.setSurname(term.getName().termName + " " + i);
                author.setTitle("Dr.");
                author.setOrganization("DANS");
                list.add(author);
            }
        }
        else if (term.getType().equals(BasicDate.class))
        {
            for (int i = 0; i < times; i++)
            {
                BasicDate bdate = new BasicDate("maart '98");
                bdate.setLanguage(new Locale("en", "US"));
                bdate.setSchemeId("basic.date.bla" + i);
                list.add(bdate);
            }
        }
        else if (term.getType().equals(BasicIdentifier.class))
        {
            for (int i = 0; i < times; i++)
            {
                BasicIdentifier bid = getBasicIdentifier(i);
                bid.setSchemeId("basic.identifier.bla" + i);
                list.add(bid);
            }
        }
        else if (term.getType().equals(BasicRemark.class))
        {
            for (int i = 0; i < times; i++)
            {
                BasicRemark remark = new BasicRemark(term.getName().termName + " " + i);
                remark.setAuthor("Author von Bar");
                remark.setLanguage("nl-NL");
                remark.setScheme("REM" + i);
                remark.setSchemeId("remark.bal.bal" + i);
                list.add(remark);
            }
        }
        else if (term.getType().equals(IsoDate.class))
        {
            for (int i = 0; i < times; i++)
            {
                IsoDate idate = new IsoDate();
                idate.setSchemeId("iso.date.bla" + i);
                list.add(idate);
            }
        }
        else if (term.getType().equals(Relation.class))
        {
            for (int i = 0; i < times; i++)
            {
                Relation rel = new Relation(getBasicString(term, i));
                rel.setEmphasis(i % 2 == 0);
                rel.setSubjectIdentifier(getBasicIdentifier(i));
                rel.setSubjectLink(new URI("http://relation.com/sys" + i));
                list.add(rel);
            }
        }
        else if (term.getType().equals(Spatial.class))
        {
            for (int i = 0; i < times; i++)
            {
                Spatial spat = new Spatial();
                spat.setPlace(getBasicString(term, i));
                if (i % 2 == 0)
                {
                    // Spatial.Point point = new Spatial.Point("POI" + i, 123.45, 456.78);
                    Spatial.Point point = new Spatial.Point("POI" + i, "123.45", "456.78");
                    point.setSchemeId("point.bla" + i);
                    spat.setPoint(point);
                }
                else
                {
                    // Spatial.Box box = new Spatial.Box("BOX" + i, 12.3, 23.4, 34.5, 45.6);
                    Spatial.Box box = new Spatial.Box("BOX" + i, "12.3", "23.4", "34.5", "45.6");
                    box.setSchemeId("bax.bla" + i);
                    spat.setBox(box);
                }
                list.add(spat);
            }
        }
        if (list.isEmpty())
        {
            LOGGER.warn("No fill-method for " + term.getType());
        }
    }

    private static BasicString getBasicString(Term term, int i)
    {
        BasicString bs = new BasicString(term.getName().termName + " " + i);
        bs.setLanguage(new Locale("nl", "NL"));
        bs.setScheme("BSS" + i);
        bs.setSchemeId("common.dc.type" + i);
        return bs;
    }

    private static BasicIdentifier getBasicIdentifier(int i) throws URISyntaxException
    {
        BasicIdentifier bid = new BasicIdentifier("ABC" + i);
        bid.setIdentificationSystem(new URI("http://pid.org/sys" + i));
        bid.setLanguage(new Locale("de", "DE"));
        bid.setScheme("BID" + i);
        bid.setSchemeId("BID.bla.foo" + i);
        return bid;
    }

}
