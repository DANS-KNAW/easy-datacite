package nl.knaw.dans.easy.business.dataset;


import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.exceptions.NoSuchTermException;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.util.TestHelper;

import org.junit.BeforeClass;
import org.junit.Test;

public class SubmissionDispatcherTest extends TestHelper
{
    
    private static EasyDepositService SERVICE;
    
    @BeforeClass
    public static void beforeClass() throws ServiceException
    {
        ClassPathHacker.addFile("../easy-webui/src/main/resources");
        SERVICE = new EasyDepositService();
        SERVICE.doBeanPostProcessing();
    }
    
    @Test
    public void testMetadataValidator() throws ServiceException
    {   // FIXME sometimes the test fails: race condition?
        SubmissionDispatcher dispatcher = new SubmissionDispatcher();
        List<SubmissionProcessor> processors = new ArrayList<SubmissionProcessor>();
        processors.add(new MetadataValidator());
        dispatcher.setProcessors(processors);
        
        MetadataFormat[] formats = MetadataFormat.values();
        
        for (MetadataFormat format : formats)
        {
            DepositDiscipline discipline = SERVICE.getDiscipline(format);
            FormDefinition definition = discipline.getEmdFormDescriptor().getFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_WIZARD);
            Dataset dataset = new DatasetImpl("dummy-dataset:1", format);
            
            DatasetSubmissionImpl submission = new DatasetSubmissionImpl(definition, dataset, null);
            
            
            try
            {
                dispatcher.process(submission);
            }
            catch (NoSuchTermException e)
            {
                System.out.println(format + " " + e.getMessage());
            }
            assertNotNull(submission.getFirstErrorPage());
        }
        
    }

}
