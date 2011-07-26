package nl.knaw.dans.easy.fedora.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.dummy.DummyUnitOfWork;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.DatasetItemContainerMetadata;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DatasetConverterTest
{
    
    private static final Logger logger = LoggerFactory.getLogger(DatasetConverterTest.class);

    private DatasetConverter datasetConverter = new DatasetConverter();
    
    private boolean verbose = Tester.isVerbose();
    
    @Test
    public void testConversion() throws XMLSerializationException, RepositoryException, DomainException, ObjectNotFoundException
    {
    	DummyUnitOfWork dmoUow = new DummyUnitOfWork();

    	DatasetImpl dataset = new DatasetImpl("easy-dataset:123");
        dataset.getAdministrativeMetadata().setDepositorId("kees4");
        dataset.getEasyMetadata().getEmdTitle().getDcTitle().add(new BasicString("Test Dataset"));
        
        dmoUow.attach(dataset);
        
        dataset.addFileOrFolder(new FileItemImpl("dummy-file:123"));
        
        DigitalObject dob = datasetConverter.serialize(dataset);
        if (verbose)
            logger.debug("\n" + dob.asXMLString(4) + "\n");

        DatasetImpl dataset2 = new DatasetImpl("easy-dataset:123");
        datasetConverter.deserialize(dob, dataset2);
        logger.debug(dataset2.getClass().getName());
        assertEquals(dataset.getStoreId(), dataset2.getStoreId());
        
        AdministrativeMetadata amdConverted = dataset2.getAdministrativeMetadata();
        assertFalse(amdConverted.isDirty());
        assertEquals(dataset.getAdministrativeMetadata().asXMLString(), amdConverted.asXMLString());
        
        EasyMetadata emdConverted = dataset2.getEasyMetadata();
        assertFalse(emdConverted.isDirty());
        assertEquals(dataset.getEasyMetadata().asXMLString(), emdConverted.asXMLString());
        
        DatasetItemContainerMetadata icmd = dataset2.getDatasetItemContainerMetadata();
        assertFalse(icmd.isDirty());
        assertEquals(dataset.getDatasetItemContainerMetadata().asXMLString(), icmd.asXMLString());
        
        if (verbose)
            logger.debug("\n" + datasetConverter.serialize(dataset2).asXMLString(4) + "\n");
    }
}
