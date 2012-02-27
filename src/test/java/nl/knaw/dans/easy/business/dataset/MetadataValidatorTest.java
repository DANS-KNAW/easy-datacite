package nl.knaw.dans.easy.business.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.form.SubHeadingDefinition;
import nl.knaw.dans.easy.domain.form.TermPanelDefinition;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.util.TestHelper;

import org.junit.BeforeClass;
import org.junit.Test;


public class MetadataValidatorTest extends TestHelper
{
    
    private static EasyDepositService SERVICE;
    
    @BeforeClass
    public static void beforeClass() throws ServiceException
    {
        SERVICE = new EasyDepositService();
        SERVICE.doBeanPostProcessing();
    }
    
    @Test
    public void testValidation() throws ServiceException
    {
        MetadataFormat format = MetadataFormat.ARCHAEOLOGY;
        DepositDiscipline discipline = SERVICE.getDiscipline(format);
        FormDefinition definition = discipline.getEmdFormDescriptor().getFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_WIZARD);
        Dataset dataset = new DatasetImpl("dummy-dataset:1", format);
        
        DatasetSubmissionImpl submission = new DatasetSubmissionImpl(definition, dataset, null);
        MetadataValidator validator = new MetadataValidator();
        validator.process(submission);
        
        for (FormPage formPage : definition.getFormPages())
        {
            System.out.println(formPage.getId());
            List<PanelDefinition> panelDefinitions = formPage.getPanelDefinitions();
            iteratePanels(panelDefinitions);
        }
        
    }

    private void iteratePanels(List<PanelDefinition> panelDefinitions)
    {
        for (PanelDefinition pDef : panelDefinitions)
        {
            if (pDef instanceof TermPanelDefinition)
            {
                TermPanelDefinition tpDef = (TermPanelDefinition) pDef;
                System.out.println("\t" + tpDef.getId());
                for (String msgKey : tpDef.getErrorMessages())
                {
                    System.out.println("\t\t" + msgKey);
                }
            }
            else if (pDef instanceof SubHeadingDefinition)
            {
                SubHeadingDefinition shDef = (SubHeadingDefinition) pDef;
                List<PanelDefinition> pDefs = shDef.getPanelDefinitions();
                iteratePanels(pDefs);
            }
        }
        
    }

}
