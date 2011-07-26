package nl.knaw.dans.easy.business.dataset;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollection;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataImpl;
import nl.knaw.dans.easy.domain.model.emd.types.IsoDate;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.LicenseComposer;
import nl.knaw.dans.easy.servicelayer.LicenseComposer.LicenseComposerException;
import nl.knaw.dans.easy.util.TestHelper;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

public class LicenseComposerTest extends TestHelper
{
    private static final String PDF_OUTPUT = "target/LicenseTestOutput";
    private static Dataset dataset;
    private static EasyUser depositor;
    private static EasyMetadataImpl metadata;
    private static FileStoreAccess fileStoreAccess;
	private static DisciplineCollection	disciplineCollection;
	private static DisciplineContainerImpl	discipline;

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        ClassPathHacker.addFile("../easy-webui/src/main/resources");
        ClassPathHacker.addFile("../easy-webui/src/main/java");
        final File metadataResource = ResourceLocator.getFile("nl/knaw/dans/easy/business/dataset/SampleMetaData.xml");

        fileStoreAccess = EasyMock.createMock(FileStoreAccess.class);
        dataset = EasyMock.createMock(Dataset.class);
        depositor = EasyMock.createMock(EasyUser.class);
        disciplineCollection = EasyMock.createMock(DisciplineCollection.class);
        metadata = (EasyMetadataImpl) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, metadataResource);       

        new Data().setFileStoreAccess(fileStoreAccess);
    }

    @Test
    public void testSample() throws Exception
    {
        final boolean isUnderEmbargo = false;
        final boolean generateSample = true;
                
        for (final AccessCategory accessCategory :AccessCategory.values()) {            
            prepare(isUnderEmbargo);
            EasyMock.expect(dataset.getAccessCategory()).andReturn(accessCategory).times(1);
            
            execute(generateSample, PDF_OUTPUT+"-sample-"+accessCategory+".pdf");
        }
    }

    @Test
    public void testSubmitted() throws Exception
    {
        // sample==false requires dateSubmitted
        final boolean generateSample = false; 
        final boolean isUnderEmbargo = true;
        prepare(isUnderEmbargo);
        
        EasyMock.expect(dataset.getDateAvailable()).andReturn(new DateTime("2011-04-14")).times(1);
        EasyMock.expect(dataset.getDateSubmitted()).andReturn(new IsoDate("2010-04-15")).times(1);
        EasyMock.expect(dataset.getAccessCategory()).andReturn(AccessCategory.GROUP_ACCESS).times(1);
        
        execute(generateSample, PDF_OUTPUT+"-submitted.pdf");
    }

    private void execute(final boolean generateSample, final String fileName) throws LicenseComposerException,
            FileNotFoundException, ObjectNotFoundException, DomainException {
        
        clearFile(fileName);
        
        LicenseComposer licenseComposer = new LicenseComposer(depositor, dataset, generateSample);
        licenseComposer.injectDisciplineCollection(disciplineCollection);

        EasyMock.replay(dataset, depositor, fileStoreAccess, disciplineCollection);

        licenseComposer.createPdf(new FileOutputStream(fileName));

        EasyMock.verify(dataset, depositor, fileStoreAccess, disciplineCollection);
        assertTrue(new File(fileName).exists());
    }
    
    private void clearFile(final String fileName) {
        final File file = new File(fileName);
        file.delete();
        assertTrue(!file.exists());
    }

    private void prepare(final boolean isUnderEmbargo) throws Exception
    {
        final String sid = "easy-dataset:123";
        final String[] fileNames ={"folder1/fileA.txt","folder1/fileB.txt","folder2/fileX.txt","folder2/fileY.txt"};

        EasyMock.reset(dataset, depositor, fileStoreAccess, disciplineCollection);
        EasyMock.expect(fileStoreAccess.getFilenames(sid, true)).andReturn(Arrays.asList(fileNames)).times(1);
        
        EasyMock.expect(depositor.getDisplayName()).andReturn("Jan Klaasen").times(1);
        EasyMock.expect(depositor.getOrganization()).andReturn("Leger van de Prins").times(1);
        EasyMock.expect(depositor.getAddress()).andReturn("Trompetdreef 1").times(1);
        EasyMock.expect(depositor.getPostalCode()).andReturn("1234 AB").times(1);
        EasyMock.expect(depositor.getCity()).andReturn("Den Helder").times(1);
        EasyMock.expect(depositor.getCountry()).andReturn("Nederland").times(1);
        EasyMock.expect(depositor.getTelephone()).andReturn("070-1234567").times(1);
        EasyMock.expect(depositor.getEmail()).andReturn("jan.klaasen@lvdp.nl").times(1);

        EasyMock.expect(dataset.isUnderEmbargo()).andReturn(isUnderEmbargo).times(1);
        EasyMock.expect(dataset.getPreferredTitle()).andReturn("A mocked dataset").times(1);
        EasyMock.expect(dataset.getEasyMetadata()).andReturn(metadata).times(1);
        EasyMock.expect(dataset.getStoreId()).andReturn(sid).times(1);

        String disciplineId = "easy-discipline:2";
		discipline = new DisciplineContainerImpl(disciplineId);
        discipline.setName("Humanities");
        EasyMock.expect(
        		disciplineCollection.getDisciplineBySid(disciplineId)
        	).andReturn(discipline); 
    }
}