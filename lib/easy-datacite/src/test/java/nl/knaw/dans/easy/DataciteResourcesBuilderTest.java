package nl.knaw.dans.easy;

import nl.knaw.dans.easy.DataciteServiceConfiguration;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataciteResourcesBuilderTest {
    private static final String XSL_EMD2DATACITE = new DataciteServiceConfiguration().getXslEmd2datacite();
    private static final Logger logger = LoggerFactory.getLogger(DataciteResourcesBuilderTest.class);

    @Test
    public void invalidXsl() throws Exception {
        // covers just one of the exceptions thrown by the private method createDoiData
        EasyMetadata emd = new EmdBuilder().build();
        try {
            new DataciteResourcesBuilder("empty.xsl").create(emd);
        }
        catch (DataciteServiceException e) {
            assertThat(e.getMessage(), containsString(emd.getEmdIdentifier().getDansManagedDoi()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEmdA() throws Exception {
        new DataciteResourcesBuilder(XSL_EMD2DATACITE).create();
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEmdB() throws Exception {
        EasyMetadata[] emds = {};
        new DataciteResourcesBuilder(XSL_EMD2DATACITE).create(emds);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEmdC() throws Exception {
        EasyMetadata[] emds = null;
        new DataciteResourcesBuilder(XSL_EMD2DATACITE).create(emds);
    }

    @Test(expected = IllegalStateException.class)
    public void classpath() throws Exception {
        new DataciteResourcesBuilder("notFound.xsl").create();
    }

    @Test(expected = IllegalArgumentException.class)
    public void noDoi() throws Exception {
        EasyMetadata emd = new EmdBuilder().replaceAll("10.5072/dans-test-123", "\t").build();
        assertThat(emd.getEmdIdentifier().getDansManagedDoi(), equalTo("\t"));

        new DataciteResourcesBuilder(XSL_EMD2DATACITE).create(emd);
    }

    @Test
    public void once() throws Exception {

        EasyMetadata emd = new EmdBuilder().build();

        String out = new DataciteResourcesBuilder(XSL_EMD2DATACITE).create(emd);

        assertThat(out, containsString(emd.getEmdIdentifier().getDansManagedDoi()));
        assertThat(out, containsString(emd.getPreferredTitle()));
        logger.debug(out);
    }

    @Test
    public void twice() throws Exception {

        EasyMetadata emd1 = new EmdBuilder().build();
        EasyMetadata emd2 = new EmdBuilder().replaceAll("dans-test-123", "dans-test-456").build();

        String out = new DataciteResourcesBuilder(XSL_EMD2DATACITE).create(emd1, emd2);

        assertThat(out, containsString(emd1.getEmdIdentifier().getDansManagedDoi()));
        assertThat(out, containsString(emd2.getEmdIdentifier().getDansManagedDoi()));
        // further proof of the pudding is eating it: sending it to datacite
        logger.debug(out);
    }
}
