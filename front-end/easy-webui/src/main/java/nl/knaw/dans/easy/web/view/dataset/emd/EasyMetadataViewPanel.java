package nl.knaw.dans.easy.web.view.dataset.emd;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.common.PropertiesMessage;
import nl.knaw.dans.easy.web.deposit.EmdPanelFactory;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;
import nl.knaw.dans.easy.web.wicket.RecursivePanel;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays EasyMetadata in a metadata-format-specific way. How and what is displayed is governed by the
 * formDefinition with id 'emd-view-definition' in the form-description xml of the metadata format.
 * 
 * @see src/main/resources/conf/discipline/emd/form-description/
 * @author ecco Feb 26, 2010
 */
public class EasyMetadataViewPanel extends AbstractDatasetModelPanel
{
    private static final long serialVersionUID = 2726623144989585861L;

    private static final Logger logger = LoggerFactory.getLogger(EasyMetadataViewPanel.class);

    private EmdPanelFactory panelFactory;

    @SpringBean(name ="depositService")
    private DepositService depositService;

    public EasyMetadataViewPanel(String wicketId, DatasetModel datasetModel)
    {
        super(wicketId, datasetModel);
        MetadataFormat emdFormat = getDataset().getEasyMetadata().getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        try
        {
            DepositDiscipline depoDiscipline = depositService.getDiscipline(emdFormat);
            FormDescriptor formDescriptor = depoDiscipline.getEmdFormDescriptor();

            FormDefinition formDefinition = formDescriptor.getFormDefinition(DepositDiscipline.EMD_VIEW_DEFINITION);
            FormPage formPage = formDefinition.getFormPage(DepositDiscipline.EMD_VIEW_DEFINITION + ".1");
            RecursivePanel recursivePanel = new RecursivePanel("recursivePane", getPanelFactory(), formPage);
            recursivePanel.setHeadVisible(false);
            add(recursivePanel);
        }
        catch (ServiceException e)
        {
            new PropertiesMessage("EasyMetadataViewPanel").errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error("Unable to display easyMetadata: ", e);
            throw new InternalWebError();
        }
    }

    private EmdPanelFactory getPanelFactory()
    {
        if (panelFactory == null)
        {
            panelFactory = new EmdPanelFactory(RecursivePanel.PANEL_WICKET_ID, this, getDatasetModel());
            logger.debug("Created transient panelFactory.");
        }
        return panelFactory;
    }

}
