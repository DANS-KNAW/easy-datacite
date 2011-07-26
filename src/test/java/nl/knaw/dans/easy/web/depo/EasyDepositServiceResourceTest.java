package nl.knaw.dans.easy.web.depo;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.form.ChoiceListDefinition;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;
import nl.knaw.dans.easy.domain.form.SubHeadingDefinition;
import nl.knaw.dans.easy.servicelayer.services.DepositService;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// test from web-ui to check all the resources.
public class EasyDepositServiceResourceTest
{
    
    private static final Logger logger = LoggerFactory.getLogger(EasyDepositServiceResourceTest.class);
    
    
    // test to see if all choice lists are available for a default locale
    @Ignore("On Continuum: java.lang.NoSuchMethodError: org.apache.xerces.impl.xs.XMLSchemaLoader.loadGrammar([Lorg/apache/xerces/xni/parser/XMLInputSource;)")
    @Test
    public void testChoiceListResources() throws ServiceException
    {
            DepositService depositService = new EasyDepositService();
            List<DepositDiscipline> disciplines = depositService.getDisciplines();
            for (DepositDiscipline discipline : disciplines)
            {
                FormDescriptor descriptor = discipline.getEmdFormDescriptor();
                FormDefinition depoDef = descriptor.getFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_WIZARD);
                List<FormPage> formPages = depoDef.getFormPages();
                for (FormPage formPage : formPages)
                {
                    List<PanelDefinition> panels = formPage.getPanelDefinitions();
                    for (PanelDefinition panel : panels)
                    {
                        
                        checkAllChoiceLists(depositService, panel);
                        
                    }
                }
            }
    }


    private void checkAllChoiceLists(DepositService depositService, PanelDefinition panel)
            throws ServiceException
    {
        if (panel instanceof StandardPanelDefinition)
        {
            StandardPanelDefinition spDef = (StandardPanelDefinition) panel;
            for (ChoiceListDefinition clDef : spDef.getChoiceListDefinitions())
            {
                logger.debug("TESTING CHOICELIST: " + clDef.getId());
                ChoiceList choiceList = depositService.getChoices(clDef.getId(), null);
                List<KeyValuePair> kvp = choiceList.getChoices();
                kvp.toString();
            }
        }
        else if (panel instanceof SubHeadingDefinition)
        {
            SubHeadingDefinition shDef = (SubHeadingDefinition) panel;
            for (PanelDefinition spd : shDef.getPanelDefinitions())
            {
                checkAllChoiceLists(depositService, spd);
            }
        }
    }

}
