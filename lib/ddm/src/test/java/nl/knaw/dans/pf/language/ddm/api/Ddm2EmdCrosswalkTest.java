package nl.knaw.dans.pf.language.ddm.api;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkException;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;
import org.dom4j.tree.DefaultElement;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/** Without validation, so pure crosswalk tests that execute without web access. */
public class Ddm2EmdCrosswalkTest {

    @Test
    public void spatialEnvelope() throws Exception {
        // @formatter:off
        String ddm = "<?xml version='1.0' encoding='utf-8'?><ddm:DDM" +
                "  xmlns:ddm='http://easy.dans.knaw.nl/schemas/md/ddm/'" +
                "  xmlns:gml='http://www.opengis.net/gml'" +
                "  xmlns:dcx-gml='http://easy.dans.knaw.nl/schemas/dcx/gml/'" +
                ">" +
                " <ddm:dcmiMetadata>" +
                "  <dcx-gml:spatial>" +
                "   <gml:boundedBy>" +
                "    <gml:Envelope srsName='http://www.opengis.net/def/crs/EPSG/0/28992'>" +
                "     <gml:lowerCorner>83575.4 455271.2 1.12</gml:lowerCorner>" +
                "     <gml:upperCorner>83575 455271 1</gml:upperCorner>" +
                "    </gml:Envelope>" +
                "   </gml:boundedBy>" +
                "  </dcx-gml:spatial>" +
                " </ddm:dcmiMetadata>" +
                "</ddm:DDM>";
        // @formatter:on

        DefaultElement top = firstEmdElementFrom(ddm);
        DefaultElement sub = (DefaultElement) top.elements().get(0);
        DefaultElement box = (DefaultElement) sub.elements().get(1);
        List<DefaultElement> points = box.elements();

        assertThat(top.elements().size(), is(1));
        assertThat(top.getQualifiedName(), is("emd:coverage"));
        assertThat(sub.getQualifiedName(), is("eas:spatial"));
        assertThat(box.getQualifiedName(), is("eas:box"));
        assertThat(points.get(0).getQualifiedName(), is("eas:north"));
    }

    @Test
    public void spatialBox() throws Exception {
        // @formatter:off
        String ddm = "<?xml version='1.0' encoding='utf-8'?><ddm:DDM" +
                "  xmlns:ddm='http://easy.dans.knaw.nl/schemas/md/ddm/'" +
                "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
                "  xmlns:dcterms='http://purl.org/dc/terms/'" +
                ">" +
                " <ddm:dcmiMetadata>" +
                "  <dcterms:spatial xsi:type='dcterms:Box'>name=Western Australia; northlimit=-13.5; southlimit=-35.5; westlimit=112.5; eastlimit=129</dcterms:spatial>"+
                " </ddm:dcmiMetadata>" +
                "</ddm:DDM>";
        // @formatter:on

        // not copied to EMD (dcterms:Box does not appear in mapping of sword packaging document)
        assertThat(firstEmdElementFrom(ddm).getQualifiedName(), is("emd:other"));
    }

    @Test
    public void spatialPoint() throws Exception {
        // @formatter:off
        String ddm = "<?xml version='1.0' encoding='utf-8'?><ddm:DDM" +
                "  xmlns:ddm='http://easy.dans.knaw.nl/schemas/md/ddm/'" +
                "  xmlns:dcterms='http://purl.org/dc/terms/'" +
                ">" +
                " <ddm:dcmiMetadata>" +
                "  <dcterms:spatial><Point><pos>1.0 2.0</pos></Point></dcterms:spatial>"+
                " </ddm:dcmiMetadata>" +
                "</ddm:DDM>";
        // @formatter:on

        DefaultElement firstEmdElement = firstEmdElementFrom(ddm);

        assertThat(firstEmdElement.elements().size(), is(1));
        assertThat(firstEmdElement.getQualifiedName(), is("emd:coverage"));
    }

    @Test
    public void temporalPlainText() throws Exception {
        // @formatter:off
        String ddm = "<?xml version='1.0' encoding='utf-8'?><ddm:DDM" +
                "  xmlns:ddm='http://easy.dans.knaw.nl/schemas/md/ddm/'" +
                "  xmlns:dcterms='http://purl.org/dc/terms/'" +
                ">" +
                " <ddm:dcmiMetadata>" +
                "  <dcterms:temporal>1992-2016</dcterms:temporal>"+
                " </ddm:dcmiMetadata>" +
                "</ddm:DDM>";
        // @formatter:on

        DefaultElement top = firstEmdElementFrom(ddm);

        DefaultElement sub = (DefaultElement) top.elements().get(0);

        assertThat(top.elements().size(), is(1));
        assertThat(top.getQualifiedName(), is("emd:coverage"));
        assertThat(sub.getQualifiedName(), is("dct:temporal"));
        assertThat(sub.getText(), is("1992-2016"));
    }

    @Test
    public void temporalABR() throws Exception {
        // @formatter:off
        String ddm = "<?xml version='1.0' encoding='utf-8'?><ddm:DDM" +
                "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
                "  xmlns:ddm='http://easy.dans.knaw.nl/schemas/md/ddm/'" +
                "  xmlns:dcterms='http://purl.org/dc/terms/'" +
                "  xmlns:abr='http://www.den.nl/standaard/166/Archeologisch-Basisregister/'" +
                ">" +
                " <ddm:dcmiMetadata>" +
                "  <dcterms:temporal xsi:type='abr:ABRperiode'>PALEOV</dcterms:temporal>" +
                " </ddm:dcmiMetadata>" +
                "</ddm:DDM>";
        // @formatter:on

        DefaultElement top = firstEmdElementFrom(ddm);

        DefaultElement sub = (DefaultElement) top.elements().get(0);

        assertThat(top.elements().size(), is(1));
        assertThat(top.getQualifiedName(), is("emd:coverage"));
        assertThat(sub.getQualifiedName(), is("dct:temporal"));
        assertThat(sub.getText(), is("PALEOV"));
        assertThat(sub.attributeCount(), is(2));
        assertThat(sub.attribute("schemeId").getQualifiedName(), is("eas:schemeId"));
        assertThat(sub.attribute("schemeId").getValue(), is("archaeology.dcterms.temporal"));
        assertThat(sub.attribute("scheme").getQualifiedName(), is("eas:scheme"));
        assertThat(sub.attribute("scheme").getValue(), is("ABR"));
    }

    @Test
    public void subjectPlainText() throws Exception {
        // @formatter:off
        String ddm = "<?xml version='1.0' encoding='utf-8'?><ddm:DDM" +
                "  xmlns:ddm='http://easy.dans.knaw.nl/schemas/md/ddm/'" +
                "  xmlns:dc='http://purl.org/dc/elements/1.1/'" +
                ">" +
                " <ddm:dcmiMetadata>" +
                "  <dc:subject>hello world</dc:subject>"+
                " </ddm:dcmiMetadata>" +
                "</ddm:DDM>";
        // @formatter:on

        DefaultElement top = firstEmdElementFrom(ddm);

        DefaultElement sub = (DefaultElement) top.elements().get(0);

        assertThat(top.elements().size(), is(1));
        assertThat(top.getQualifiedName(), is("emd:subject"));
        assertThat(sub.getQualifiedName(), is("dc:subject"));
        assertThat(sub.getText(), is("hello world"));
    }

    @Test
    public void subjectABR() throws Exception {
        // @formatter:off
        String ddm = "<?xml version='1.0' encoding='utf-8'?><ddm:DDM" +
                "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
                "  xmlns:ddm='http://easy.dans.knaw.nl/schemas/md/ddm/'" +
                "  xmlns:dc='http://purl.org/dc/elements/1.1/'" +
                "  xmlns:abr='http://www.den.nl/standaard/166/Archeologisch-Basisregister/'" +
                ">" +
                " <ddm:dcmiMetadata>" +
                "  <dc:subject xsi:type='abr:ABRcomplex'>DEPO</dc:subject>" +
                " </ddm:dcmiMetadata>" +
                "</ddm:DDM>";
        // @formatter:on

        DefaultElement top = firstEmdElementFrom(ddm);

        DefaultElement sub = (DefaultElement) top.elements().get(0);

        assertThat(top.elements().size(), is(1));
        assertThat(top.getQualifiedName(), is("emd:subject"));
        assertThat(sub.getQualifiedName(), is("dc:subject"));
        assertThat(sub.getText(), is("DEPO"));
        assertThat(sub.attributeCount(), is(2));
        assertThat(sub.attribute("schemeId").getQualifiedName(), is("eas:schemeId"));
        assertThat(sub.attribute("schemeId").getValue(), is("archaeology.dc.subject"));
        assertThat(sub.attribute("scheme").getQualifiedName(), is("eas:scheme"));
        assertThat(sub.attribute("scheme").getValue(), is("ABR"));
    }

    private DefaultElement firstEmdElementFrom(String ddm) throws XMLSerializationException, CrosswalkException {
        EasyMetadata emd = new Ddm2EmdCrosswalk(null).createFrom(ddm);
        return (DefaultElement) new EmdMarshaller(emd).getXmlElement().elementIterator().next();
    }
}
