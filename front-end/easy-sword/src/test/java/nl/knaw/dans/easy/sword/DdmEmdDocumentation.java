package nl.knaw.dans.easy.sword;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

import nl.knaw.dans.common.lang.HomeDirectory;
import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.util.StreamUtil;
import nl.knaw.dans.easy.business.services.EasyDisciplineCollectionService;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.LicenseComposer;
import nl.knaw.dans.easy.servicelayer.LicenseComposer.LicenseComposerException;
import nl.knaw.dans.easy.servicelayer.services.DisciplineCollectionService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.pf.language.ddm.api.Ddm2EmdCrosswalk;
import nl.knaw.dans.pf.language.ddm.api.Ddm2EmdHandlerMap;
import nl.knaw.dans.pf.language.ddm.api.OfflineDDMValidator;
import nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.binding.Encoding;
import nl.knaw.dans.pf.language.xml.vocabulary.MapFromXSD;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Shows how/which DDM fields appear in a license document as meta data */
public class DdmEmdDocumentation
{
    /** Same test file as in CrosswalkInlineTest */
    private static final File INPUT = new File("src/test/resources/input/demoDDM.xml");

    private static final String OUTPUT = "target/doc/emd-from-" + INPUT.getName();

    private static final Ddm2EmdCrosswalk crosswalker = new Ddm2EmdCrosswalk(new OfflineDDMValidator());
    private static final EasyUser MOCKED_DEPOSITOR = EasyMock.createMock(EasyUser.class);
    private static final Logger logger = LoggerFactory.getLogger(DdmEmdDocumentation.class);

    @BeforeClass
    public static void init() throws Exception
    {
        setHome();
        new Services().setDisciplineService(mockDisciplineService());
        new File("target/doc").mkdirs();
    }

    @Test
    public void crosswalk() throws Exception
    {
        final EasyMetadata emd = crosswalker.createFrom(readFile(INPUT));
        logger.info(crosswalker.getXmlErrorHandler().getMessages());
        writeFile(new File(OUTPUT), new EmdMarshaller(emd).getXmlString());
        writeFile(new File(OUTPUT + ".html"), getMetadataAsHTML(emd));
        assertThat(crosswalker.getXmlErrorHandler().getErrors().size(), is(0));
        assertThat(crosswalker.getXmlErrorHandler().getFatalErrors().size(), is(0));
    }

    private String getMetadataAsHTML(final EasyMetadata emd) throws Exception, LicenseComposerException
    {
        // get just a section of the license document
        return new LicenseComposer(MOCKED_DEPOSITOR, mockDataset(emd), true)
        {
            String getMetadataAsHTML() throws Exception
            {
                // no import because of conflicts with dom4j
                final com.lowagie.text.Document document = new com.lowagie.text.Document();
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                com.lowagie.text.html.HtmlWriter.getInstance(document, outputStream);
                document.open();
                document.add(formatMetaData(document));
                document.close();
                return outputStream.toString();
            }
        }.getMetadataAsHTML();
    }

    @Test
    public void handlerMapCoverage() throws Exception
    {
        final Document document = new SAXReader().read(INPUT);
        for (final String key : Ddm2EmdHandlerMap.getInstance().getKeys())
        {
            final String[] split = key.split("/");
            final String tag = split[1];
            final String localNameOfType = split[0];
            final String xpath;
            if (localNameOfType.length() == 0)
                xpath = "//" + tag;
            else
                xpath = "//" + tag + "[contains(@xsi:type,'" + localNameOfType + "')]";
            if (tag.equals("ddm:additional-xml"))
                ;
            else
            {
                final String msg = "<" + tag + "> with attribute xsi:type " + localNameOfType + " not in xml";
                assertTrue(msg, document.selectNodes(xpath).size() > 0);
            }
        }
    }

    private Dataset mockDataset(final EasyMetadata emd) throws Exception
    {
        final Dataset dataset = EasyMock.createMock(Dataset.class);
        EasyMock.expect(dataset.getEasyMetadata()).andStubReturn(emd);
        EasyMock.replay(dataset);
        return dataset;
    }

    private static DisciplineCollectionService mockDisciplineService() throws Exception
    {
        final Map<String, String> disciplines = new MapFromXSD(NameSpace.NARCIS_TYPE.xsd).getAppInfo2doc();
        return new EasyDisciplineCollectionService()
        {
            public DisciplineContainer getDisciplineById(final DmoStoreId dmoStoreId)
            {
                final String key = dmoStoreId.toString();
                final String value = disciplines.get(key);
                final DisciplineContainer container = EasyMock.createMock(DisciplineContainer.class);
                EasyMock.expect(container.getName()).andStubReturn(value == null ? key : value);
                EasyMock.replay(container);
                return container;

            }
        };
    }

    private String readFile(final File file) throws Exception
    {
        final byte[] xml = StreamUtil.getBytes(new FileInputStream(file));
        return new String(xml, Encoding.UTF8);
    }

    private void writeFile(final File file, final String string) throws Exception
    {
        final FileOutputStream os = new FileOutputStream(file);
        try
        {
            os.write(string.getBytes());
        }
        finally
        {
            os.close();
        }
    }

    public static void setHome()
    {
        final String home = (String) System.getProperties().get("EASY_SWORD_HOME");
        final HomeDirectory homeDirectory = new HomeDirectory()
        {
            @Override
            public File getHomeDirectory()
            {
                return new File(home);
            }

            @Override
            public String getHome()
            {
                return home;
            }
        };
        new ResourceLocator(homeDirectory);
    }
}
