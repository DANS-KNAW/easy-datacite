package nl.knaw.dans.easy.sword;

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
import nl.knaw.dans.easy.servicelayer.services.DisciplineCollectionService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.pf.language.ddm.api.Ddm2EmdCrosswalk;
import nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.binding.Encoding;
import nl.knaw.dans.pf.language.xml.vocabulary.MapFromXSD;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.html.HtmlWriter;

/** Shows how/which DDM fields appear in a license document as meta data */
public class DdmEmdDocumentation
{
    private static final String OUTPUT = "target/doc/";
    private static final Ddm2EmdCrosswalk crosswalk = new Ddm2EmdCrosswalk();
    private static final EasyUser MOCKED_DEPOSITOR = EasyMock.createMock(EasyUser.class);

    private class MyComposer extends LicenseComposer
    {
        public MyComposer(final EasyUser depositor, final Dataset dataset, final boolean generateSample) throws LicenseComposerException
        {
            super(depositor, dataset, generateSample);
        }

        /** Makes a protected method visible for testing purposes. */
        protected Element formatMetaData(final Document document) throws LicenseComposerException
        {
            return super.formatMetaData(document);
        }

        String getEMDasHTML() throws Exception
        {
            final Document document = new Document();
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlWriter.getInstance(document, outputStream);
            document.open();
            document.add(formatMetaData(document));
            document.close();
            return outputStream.toString();
        }
    }

    @BeforeClass
    public static void init() throws Exception
    {
        setHome();
        new Services().setDisciplineService(mockDisciplineService());
        new File("target/doc").mkdirs();
    }

    @Test
    public void basics() throws Exception
    {
        run("ddm.xml");
    }

    @Test
    public void withExtensions() throws Exception
    {
        run("ddm-extensions.xml");
    }

    private void run(final String fileName) throws Exception
    {
        final EasyMetadata emd = crosswalk.createFrom(readFile(fileName));
        writeFile(new File(OUTPUT + "emd-from-" + fileName), new EmdMarshaller(emd).getXmlString());
        final String html = new MyComposer(MOCKED_DEPOSITOR, mockDataset(emd), true).getEMDasHTML();
        writeFile(new File(OUTPUT + "emd-from-" + fileName + ".html"), html);
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
        return new EasyDisciplineCollectionService(){
            public DisciplineContainer getDisciplineById(final DmoStoreId dmoStoreId){
                final String key = dmoStoreId.toString();
                final String value = disciplines.get(key);
                final DisciplineContainer container = EasyMock.createMock(DisciplineContainer.class);
                EasyMock.expect(container.getName()).andStubReturn(value==null?key:value);
                EasyMock.replay(container);
                return container;
                
            }
        };
    }

    private String readFile(final String string) throws Exception
    {
        final byte[] xml = StreamUtil.getBytes(new FileInputStream("src/test/resources/input/" + string));
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
