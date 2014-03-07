package nl.knaw.dans.easy.domain.form;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.servicelayer.services.DepositService;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChoiceListTest
{
    private static final Logger logger = LoggerFactory.getLogger(ChoiceListTest.class);

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

    private void checkAllChoiceLists(DepositService depositService, PanelDefinition panel) throws ServiceException
    {
        if (panel instanceof StandardPanelDefinition)
        {
            StandardPanelDefinition spDef = (StandardPanelDefinition) panel;
            for (ChoiceListDefinition clDef : spDef.getChoiceListDefinitions())
            {
                if (clDef.getId().equals("custom.disciplines"))
                    logger.debug("SKIPPING CHOICELIST: " + clDef.getId() + " it needs Data.getEasyStore()");
                else
                {
                    logger.debug("TESTING CHOICELIST: " + clDef.getId());
                    ChoiceList choiceList = depositService.getChoices(clDef.getId(), null);
                    List<KeyValuePair> kvp = choiceList.getChoices();
                    kvp.toString();
                }
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
