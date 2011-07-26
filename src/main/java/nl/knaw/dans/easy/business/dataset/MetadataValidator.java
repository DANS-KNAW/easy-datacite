package nl.knaw.dans.easy.business.dataset;

import java.util.List;

import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.form.SubHeadingDefinition;
import nl.knaw.dans.easy.domain.form.TermPanelDefinition;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.Term;
import nl.knaw.dans.easy.domain.model.emd.types.MetadataItem;
import nl.knaw.dans.easy.domain.model.emd.types.Spatial;

public class MetadataValidator implements SubmissionProcessor
{
    
    public static final String MSG_REQUIRED = "deposit.field_required";
    public static final String MSG_INCOMPLETE = "deposit.field_incomplete";
    
    // To complete the form model, alongside a TermPanel, a ContainerPanel should be implemented. As for now we do not validate containers.
    private static final String CONTAINERPANEL_NOT_IMPLEMENTED = "containerPanel not implemented";
    
    public boolean continueAfterFailure()
    {
        return true;
    }

    public boolean process(DatasetSubmissionImpl submission)
    {
        submission.setMetadataValid(true);
        FormDefinition definition = submission.getFormDefinition();
        
        for (FormPage formPage : definition.getFormPages())
        {
            List<PanelDefinition> panelDefinitions = formPage.getPanelDefinitions();
            iteratePanels(submission, panelDefinitions);
        }
        return submission.isMetadataValid();
    }

    private void iteratePanels(DatasetSubmissionImpl submission, List<PanelDefinition> panelDefinitions)
    {
        for (PanelDefinition pDef : panelDefinitions)
        {
            if (pDef instanceof TermPanelDefinition)
            {
                validateTerm(submission, (TermPanelDefinition) pDef);
            }
            else if (pDef instanceof SubHeadingDefinition)
            {
                SubHeadingDefinition shDef = (SubHeadingDefinition) pDef;
                List<PanelDefinition> pDefs = shDef.getPanelDefinitions();
                iteratePanels(submission, pDefs);
            }
        }
    }

    private void validateTerm(DatasetSubmissionImpl submission, TermPanelDefinition tpDef)
    {
        if (!CONTAINERPANEL_NOT_IMPLEMENTED.equals(tpDef.getNamespacePrefix()))
        {
            EasyMetadata emd = submission.getDataset().getEasyMetadata();
            Term term = new Term(tpDef.getTermName(), tpDef.getNamespacePrefix());
            List<MetadataItem> items = emd.getTerm(term);
            
            checkRequired(submission, tpDef, items);
            checkComplete(submission, tpDef, items);
        }
    }

    private void checkRequired(DatasetSubmissionImpl submission, TermPanelDefinition tpDef, List<MetadataItem> items)
    {
        // required fields
        if (tpDef.isRequired())
        {
            if(items.isEmpty())
            {
                submission.setMetadataValid(false);
                tpDef.addErrorMessage(MSG_REQUIRED);
            }
        }
    }
    
    private void checkComplete(DatasetSubmissionImpl submission, TermPanelDefinition tpDef, List<MetadataItem> items)
    {
        // everything complete?
        
        int pointCounter = -1;
        int boxCounter = -1;
        for (int index = 0; index < items.size(); index ++)
        {
            MetadataItem item = items.get(index);
            if (item instanceof Spatial)
            {
                Spatial spatial = (Spatial) item;
                if ("eas.spatial.point".equals(tpDef.getId()) && spatial.getPoint() != null)
                {
                    pointCounter++;
                    if (!item.isComplete())
                    {
                        submission.setMetadataValid(false);
                        tpDef.addItemErrorMessage(pointCounter, MSG_INCOMPLETE);
                    }
                }
                if ("eas.spatial.box".equals(tpDef.getId()) && spatial.getBox() != null)
                {
                    boxCounter++;
                    if (!item.isComplete())
                    {
                        submission.setMetadataValid(false);
                        tpDef.addItemErrorMessage(boxCounter, MSG_INCOMPLETE);
                    }
                }
                
            }
            else if (!item.isComplete())
            {
                submission.setMetadataValid(false);
                tpDef.addItemErrorMessage(index, MSG_INCOMPLETE);
            }
        }
    }

}
