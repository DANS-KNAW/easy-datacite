package nl.knaw.dans.easy.business.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.form.SubHeadingDefinition;
import nl.knaw.dans.easy.domain.form.TermPanelDefinition;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.Term;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;
import nl.knaw.dans.pf.language.emd.types.Spatial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SubmissionProcessor that validates metadata and reports in a web-ui-specific manner. If you want to do metadata validating and report in another
 * medium-specific manner (xml-specific for instance) than write your own.
 * 
 * @author henkb
 */
public class WebDepositFormMetadataValidator implements SubmissionProcessor {

    public static final String MSG_REQUIRED = "deposit.field_required";
    public static final String MSG_INCOMPLETE = "deposit.field_incomplete";
    public static final String MSG_NO_GROUP = "deposit.field_invalid_group";

    // To complete the form model, alongside a TermPanel, a ContainerPanel should be implemented. As for
    // now we do not validate containers.
    private static final String CONTAINERPANEL_NOT_IMPLEMENTED = "containerPanel not implemented";

    private static final Logger logger = LoggerFactory.getLogger(WebDepositFormMetadataValidator.class);

    private boolean metadataIsValid = true;
    private EasyMetadata easyMetadata;

    public boolean continueAfterFailure() {
        return true;
    }

    public boolean process(final DatasetSubmissionImpl submission) {
        final FormDefinition definition = submission.getFormDefinition();
        easyMetadata = submission.getDataset().getEasyMetadata();

        iterateFormPages(definition, easyMetadata);
        submission.setMetadataValid(metadataIsValid);
        return metadataIsValid;
    }

    public boolean validate(final FormDefinition formDefinition, final EasyMetadata easyMetadata) {
        iterateFormPages(formDefinition, easyMetadata);
        return metadataIsValid;
    }

    private void iterateFormPages(final FormDefinition definition, final EasyMetadata easyMetadata) {
        for (final FormPage formPage : definition.getFormPages()) {
            final List<PanelDefinition> panelDefinitions = formPage.getPanelDefinitions();
            iteratePanels(easyMetadata, panelDefinitions);
        }
    }

    private void iteratePanels(final EasyMetadata emd, final List<PanelDefinition> panelDefinitions) {
        for (final PanelDefinition pDef : panelDefinitions) {
            if (pDef instanceof TermPanelDefinition) {
                validateTerm(emd, (TermPanelDefinition) pDef);
            } else if (pDef instanceof SubHeadingDefinition) {
                final SubHeadingDefinition shDef = (SubHeadingDefinition) pDef;
                final List<PanelDefinition> pDefs = shDef.getPanelDefinitions();
                iteratePanels(emd, pDefs);
            }
        }
    }

    private void validateTerm(final EasyMetadata emd, final TermPanelDefinition tpDef) {
        if (!CONTAINERPANEL_NOT_IMPLEMENTED.equals(tpDef.getNamespacePrefix())) {
            final Term term = new Term(tpDef.getTermName(), tpDef.getNamespacePrefix());
            final List<MetadataItem> items = emd.getTerm(term);
            if (tpDef.getTermName().equals(Term.Name.LICENSE.termName)) {
                if (AccessCategory.NO_ACCESS.equals(emd.getEmdRights().getAccessCategory())) {
                    tpDef.setRequired(false);
                } else {
                    tpDef.setRequired(true);
                }
            }
            checkDependencies(tpDef, items);
            checkRequired(tpDef, items);
            checkComplete(tpDef, items);
        }
    }

    private void checkDependencies(TermPanelDefinition tpDef, List<MetadataItem> items) {
        for (int index = 0; index < items.size(); index++) {
            final MetadataItem item = items.get(index);
            String tpDefId = tpDef.getId();
            if (item instanceof BasicString && "dcterms.accessrights".equals(tpDefId)) {
                String value = ((BasicString) item).getValue();
                if (value != null && value.equals("GROUP_ACCESS")) {
                    if (!easyMetadata.audienceIsArchaeology()) {
                        metadataIsValid = false;
                        tpDef.addItemErrorMessage(index, MSG_NO_GROUP);
                    }
                }
            }
        }
    }

    private void checkRequired(final TermPanelDefinition tpDef, final List<MetadataItem> items) {
        // required fields
        if (tpDef.isRequired()) {
            if (items.isEmpty()) {
                metadataIsValid = false;
                logger.debug("Empty item on required field " + tpDef.getId());
                tpDef.addErrorMessage(MSG_REQUIRED);
            }
        }
    }

    private void checkComplete(final TermPanelDefinition tpDef, final List<MetadataItem> items) {
        // everything complete?

        int pointCounter = -1;
        int boxCounter = -1;
        for (int index = 0; index < items.size(); index++) {
            final MetadataItem item = items.get(index);
            String tpDefId = tpDef.getId();
            if (item instanceof Spatial) {
                final Spatial spatial = (Spatial) item;
                if ("eas.spatial.point".equals(tpDefId) && spatial.getPoint() != null) {
                    pointCounter++;
                    if (!item.isComplete()) {
                        metadataIsValid = false;
                        logger.debug("Incomplete item on field " + tpDefId);
                        tpDef.addItemErrorMessage(pointCounter, MSG_INCOMPLETE);
                    }
                }
                if ("eas.spatial.box".equals(tpDefId) && spatial.getBox() != null) {
                    boxCounter++;
                    if (!item.isComplete()) {
                        metadataIsValid = false;
                        logger.debug("Incomplete item on field " + tpDefId);
                        tpDef.addItemErrorMessage(boxCounter, MSG_INCOMPLETE);
                    }
                }
            } else if (!item.isComplete()) {
                metadataIsValid = false;
                logger.debug("Incomplete item on field " + tpDefId);
                tpDef.addItemErrorMessage(index, MSG_INCOMPLETE);
            }
        }
    }

}
