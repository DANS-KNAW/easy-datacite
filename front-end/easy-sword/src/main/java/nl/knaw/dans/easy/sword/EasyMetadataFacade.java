package nl.knaw.dans.easy.sword;

import java.io.UnsupportedEncodingException;
import java.util.*;

import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler.Reporter;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.business.dataset.WebDepositFormMetadataValidator;
import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.emd.validation.FormatValidator;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.form.SubHeadingDefinition;
import nl.knaw.dans.easy.domain.form.TermPanelDefinition;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.Term;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;
import nl.knaw.dans.pf.language.emd.validation.EMDValidator;

import org.easymock.EasyMock;
import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.xml.sax.SAXException;

public class EasyMetadataFacade {
    private static final String DEFAULT_SYNTAX_VERSION = EMDValidator.VERSION_0_1;

    /** Just a wrapper for exceptions. */
    static void validateMandatoryFields(final EasyMetadata metadata) throws SWORDErrorException, SWORDException {
        final FormDefinition formDefinition = EasyBusinessFacade.getFormDefinition();
        List<PanelDefinition> archivistPanelDefinitions = formDefinition.getFormPages().get(0).getPanelDefinitions();
        customize(archivistPanelDefinitions);
        WebDepositFormMetadataValidator validator = new WebDepositFormMetadataValidator();
        if (!validator.validate(formDefinition, metadata)) {
            final List<String> messages = new ArrayList<String>();
            collectMessages(archivistPanelDefinitions, messages);
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
                    // in 2012-09 a mandatory eas.creator was added
                    // for DDM we allow the old fashioned dc.creator
                    // as 3rd party systems might not be able to provide the required details like humans
                    // the XSD makes sure we get one of the both
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

    /** Just a wrapper for exceptions. */
    static void validateSyntax(final byte[] data) throws SWORDErrorException, SWORDException {
        final XMLErrorHandler handler = new XMLErrorHandler(Reporter.off);
        try {
            EMDValidator.instance().validate(handler, new String(data, "UTF-8"), DEFAULT_SYNTAX_VERSION);
        }
        catch (final UnsupportedEncodingException exception) {
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "EASY metadata encoding exception: " + exception.getMessage());
        }
        catch (final SAXException exception) {
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "EASY metadata parse exception: " + exception.getMessage());
        }
        catch (nl.knaw.dans.pf.language.xml.exc.ValidatorException exception) {
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "EASY metadata validation exception: " + exception.getMessage());
        }
        catch (nl.knaw.dans.pf.language.xml.exc.SchemaCreationException exception) {
            throw new SWORDException("EASY metadata schema creation problem", exception);
        }
        if (!handler.passed())
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "Invalid EASY metadata: \n" + handler.getMessages());
    }
}
