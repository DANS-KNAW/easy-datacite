package nl.knaw.dans.easy.sword;

import nl.knaw.dans.easy.business.dataset.WebDepositFormMetadataValidator;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.form.SubHeadingDefinition;
import nl.knaw.dans.easy.domain.form.TermPanelDefinition;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DepositFormFacade {

    static void validate(final EasyMetadata metadata) throws SWORDErrorException, SWORDException {
        final FormDefinition formDefinition = EasyBusinessFacade.getArchivistFormDefinition();
        List<PanelDefinition> panelDefinitions = formDefinition.getFormPages().get(0).getPanelDefinitions();
        customize(panelDefinitions);
        WebDepositFormMetadataValidator validator = new WebDepositFormMetadataValidator();
        if (!validator.validate(formDefinition, metadata)) {
            final List<String> messages = new ArrayList<String>();
            collectMessages(panelDefinitions, messages);
            throw new SWORDErrorException(ErrorCodes.ERROR_CONTENT, "invalid meta data\n" + concat(messages));
        }
    }

    private static void collectMessages(List<PanelDefinition> panelDefinitions, final List<String> messages) {
        for (final PanelDefinition pDef : panelDefinitions) {
            if (pDef instanceof TermPanelDefinition) {
                TermPanelDefinition tpDef = (TermPanelDefinition) pDef;
                Map<Integer, List<String>> itemErrorMessages = tpDef.getItemErrorMessages();
                for (int i = 0; i < itemErrorMessages.size(); i++) {
                    List<String> itemMessages = itemErrorMessages.get(i);
                    messages.add(String.format("%s[%d]: %s", tpDef.getId(), i, concat(itemMessages)));
                }
                if (tpDef.getErrorMessages().size() > 0) {
                    messages.add(String.format("%s: %s", tpDef.getId(), concat(tpDef.getErrorMessages())));
                }
            } else if (pDef instanceof SubHeadingDefinition) {
                collectMessages(recursivePanelDefs(pDef), messages);
            }
        }
    }

    private static void customize(List<PanelDefinition> panelDefinitions) {
        for (final PanelDefinition pDef : panelDefinitions) {
            if (pDef instanceof TermPanelDefinition) {
                TermPanelDefinition tpDef = (TermPanelDefinition) pDef;
                if (tpDef.getId().equals("eas.creator")) {
                    // In 2012-09 a mandatory eas.creator was added.
                    // 3rd party systems might not be able to provide the required details like humans,
                    // so for DDM we allow the old fashioned dc.creator.
                    // The XSD makes sure we get one of the both.
                    tpDef.setRequired(false);
                }
            } else if (pDef instanceof SubHeadingDefinition) {
                customize(recursivePanelDefs(pDef));
            }
        }
    }

    private static List<PanelDefinition> recursivePanelDefs(PanelDefinition pDef) {
        return ((SubHeadingDefinition) pDef).getPanelDefinitions();
    }

    private static String concat(List<String> errorMessages) {
        return Arrays.toString(errorMessages.toArray());
    }
}
