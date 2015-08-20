package nl.knaw.dans.easy.ebiu.task;

import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.emd.validation.FormatValidator;
import nl.knaw.dans.easy.domain.emd.validation.base.ValidationReport;
import nl.knaw.dans.easy.domain.emd.validation.base.ValidationReporter;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.form.SubHeadingDefinition;
import nl.knaw.dans.easy.domain.form.TermPanelDefinition;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.ebiu.AbstractTask;
import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.Term;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;

public class EasyMetadataCheck extends AbstractTask {
    private boolean allowDcCreator = false;
    private boolean foundEasCreator = false;
    private boolean foundDcCreator = false;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        EasyMetadata emd = joint.getEasyMetadata();
        if (emd == null) {
            joint.setFitForDraft(false);
            return;
        }
        FormDefinition formDefinition = getFormDefinition(emd);
        for (FormPage formPage : formDefinition.getFormPages()) {
            List<PanelDefinition> panelDefinitions = formPage.getPanelDefinitions();
            iteratePanels(joint, emd, panelDefinitions);
        }

        validateSemantics(joint, emd);

        // semantical workarounds on form definitions

        if (hasNoAudience(emd)) {
            // Archaeological deposits don't have an audience on the form
            // ingesters however should set one
            warnNotFitForSubmit(joint, "no audience");
        }

        if (allowDcCreator && foundDcCreator == false && foundEasCreator == false) {
            // The forms require a complex creator format
            // ingesters however might only have a free format in the input
            warnNotFitForSubmit(joint, "no creator");
        }
    }

    public static boolean hasNoAudience(EasyMetadata emd) {
        return emd.getEmdAudience() == null || emd.getEmdAudience().toString().trim().length() == 0;
    }

    private void warnNotFitForSubmit(final JointMap joint, final String string) {
        Dataset dataset = joint.getDataset();
        String storeId = dataset == null ? "<no dataset>" : dataset.getStoreId();
        RL.warn(new Event(getTaskName(), storeId, string));
        joint.setFitForSubmit(false);
    }

    protected FormDefinition getFormDefinition(EasyMetadata emd) throws TaskException {
        MetadataFormat mdFormat = emd.getEmdOther().getEasApplicationSpecific().getMetadataFormat();

        DepositDiscipline depoDisc;
        try {
            depoDisc = Services.getDepositService().getDiscipline(mdFormat);
        }
        catch (ServiceException e) {
            RL.error(new Event("Cannot get deposit discipline.", e, e.getMessage(), "MetadataFormat is " + mdFormat));
            throw new TaskException("Cannot get deposit discipline.", e, this);
        }

        FormDefinition formDefinition = depoDisc.getEmdFormDescriptor().getFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_ARCHIVIST);
        return formDefinition;
    }

    private void iteratePanels(JointMap joint, EasyMetadata emd, List<PanelDefinition> panelDefinitions) {
        for (PanelDefinition pDef : panelDefinitions) {
            if (pDef instanceof TermPanelDefinition) {
                validateTerm(joint, emd, (TermPanelDefinition) pDef);
            }

            if (pDef instanceof SubHeadingDefinition) {
                SubHeadingDefinition shDef = (SubHeadingDefinition) pDef;
                List<PanelDefinition> pDefs = shDef.getPanelDefinitions();
                iteratePanels(joint, emd, pDefs);
            }
        }
    }

    private void validateTerm(JointMap joint, EasyMetadata emd, TermPanelDefinition tpDef) {
        Term term = new Term(tpDef.getTermName(), tpDef.getNamespacePrefix());
        List<MetadataItem> items = emd.getTerm(term);

        checkRequired(joint, tpDef, items);
        checkComplete(joint, tpDef, items);
    }

    private void checkRequired(JointMap joint, TermPanelDefinition tpDef, List<MetadataItem> items) {
        // required fields
        if (tpDef.isRequired() && items.isEmpty()) {
            if (allowDcCreator && tpDef.getId().equals("eas.creator")) {
                foundEasCreator = true;
                return;
            }

            joint.setFitForSubmit(false);
            RL.warn(new Event("Missing required field", "easyMetadata", tpDef.getId()));
        }
        if (allowDcCreator && tpDef.getId().equals("dc.creator")) {
            foundDcCreator = true;
        }
    }

    private void checkComplete(JointMap joint, TermPanelDefinition tpDef, List<MetadataItem> items) {
        // everything complete?
        for (int index = 0; index < items.size(); index++) {
            MetadataItem item = items.get(index);
            if (!item.isComplete()) {
                joint.setFitForSubmit(false);
                RL.warn(new Event("Incomplete value", "easyMetadata", tpDef.getId(), "index=" + index));
            }
        }
    }

    private void validateSemantics(final JointMap joint, EasyMetadata emd) {
        FormatValidator.instance().validate(emd, new ValidationReporter() {

            @Override
            public void setMetadataValid(boolean valid) {
                joint.setFitForSave(valid);
            }

            @Override
            public void addWarning(ValidationReport validationReport) {
                RL.warn(new Event("Semantic warning", "easyMetadata", "message=" + validationReport.getMessage(), "xpath="
                        + validationReport.getXpathExpression(), "sourceLink=" + validationReport.getSourceLink()));
            }

            @Override
            public void addInfo(ValidationReport validationReport) {
                RL.info(new Event("Semantic info", "easyMetadata", "message=" + validationReport.getMessage(),
                        "xpath=" + validationReport.getXpathExpression(), "sourceLink=" + validationReport.getSourceLink()));
            }

            @Override
            public void addError(ValidationReport validationReport) {
                RL.error(new Event("Semantic error", "easyMetadata", "message=" + validationReport.getMessage(), "xpath="
                        + validationReport.getXpathExpression(), "sourceLink=" + validationReport.getSourceLink()));
            }
        });

    }

    public void setAllowDcCreator(boolean allowDcCreator) {
        this.allowDcCreator = allowDcCreator;
    }

}
