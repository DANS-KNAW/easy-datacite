package nl.knaw.dans.easy.sword;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler.Reporter;
import nl.knaw.dans.easy.domain.emd.validation.FormatValidator;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.form.SubHeadingDefinition;
import nl.knaw.dans.easy.domain.form.TermPanelDefinition;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.Term;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;
import nl.knaw.dans.pf.language.emd.validation.EMDValidator;

import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.xml.sax.SAXException;

public class EasyMetadataFacade
{
    private static final String DEFAULT_SYNTAX_VERSION = EMDValidator.VERSION_0_1;

    /** Just a wrapper for exceptions. */
    private static EasyMetadata unmarshallEasyMetaData(final byte[] data) throws SWORDErrorException
    {
        final EasyMetadata metadata;
        try
        {
            metadata = (EasyMetadata) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, data);
        }
        catch (final XMLDeserializationException exception)
        {
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, ("EASY metadata unmarshall exception: " + exception.getMessage()));
        }
        return metadata;
    }

    /**
     * @param easyMetaData
     *        xml text representation
     * @return unmarshalled easyMetaData
     * @throws SWORDErrorException
     * @throws SWORDException
     */
    public static EasyMetadata validate(final byte[] easyMetaData) throws SWORDErrorException, SWORDException
    {
        validateSyntax(easyMetaData);
        final EasyMetadata unmarshalled = unmarshallEasyMetaData(easyMetaData);
        validateControlledVocabulairies(unmarshalled);
        validateMandatoryFields(unmarshalled);
        return unmarshalled;
    }

    /** Just a wrapper for exceptions. To be replaced by implicit validation by the crosswalker. */
    private static void validateMandatoryFields(final EasyMetadata metadata) throws SWORDErrorException, SWORDException
    {
        final FormDefinition formDefinition = EasyBusinessFacade.getFormDefinition(metadata);
        final List<String> messages = new ArrayList<String>();
        for (final FormPage formPage : formDefinition.getFormPages())
            validatePanels(metadata, formPage.getPanelDefinitions(), messages);
        if (!messages.isEmpty())
        {
            final String message = "invalid meta data\n" + Arrays.toString(messages.toArray());
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, message);
        }
    }

    private static void validatePanels(final EasyMetadata emd, final List<PanelDefinition> panelDefinitions, final List<String> messages)
    {
        // equivalent of nl.knaw.dans.easy.tools.batch.EasyMetadataCheck.iteratePanels()
        for (final PanelDefinition pDef : panelDefinitions)
        {
            if (pDef instanceof TermPanelDefinition)
                validateSinglePanel(emd, (TermPanelDefinition) pDef, messages);
            if (pDef instanceof SubHeadingDefinition)
            {
                final SubHeadingDefinition shDef = (SubHeadingDefinition) pDef;
                validatePanels(emd, shDef.getPanelDefinitions(), messages);
            }
        }
    }

    private static void validateSinglePanel(final EasyMetadata emd, final TermPanelDefinition tpDef, final List<String> messages)
    {
        final Term term = new Term(tpDef.getTermName(), tpDef.getNamespacePrefix());
        final List<MetadataItem> items = emd.getTerm(term);

        // in 2012-09 a mandatory eas.creator was added, we stick to old behavior
        final boolean required = tpDef.isRequired() && !tpDef.getId().equals("eas.creator");
        if (required && items.isEmpty())
            messages.add("Missing required field " + tpDef.getId());
        for (int index = 0; index < items.size(); index++)
        {
            final MetadataItem item = items.get(index);
            if (!item.isComplete())
                messages.add("Incomplete value " + tpDef.getId() + " index=" + index);
        }
    }

    /** Just a wrapper for exceptions. */
    private static void validateControlledVocabulairies(final EasyMetadata metadata) throws SWORDErrorException, SWORDException
    {
        // equivalent of nl.knaw.dans.easy.tools.batch.EasyMetadataCheck.validateSemantics()
        final EasySwordValidationReporter validationReporter = new EasySwordValidationReporter();
        FormatValidator.instance().validate(metadata, validationReporter);
        if (!validationReporter.isMetadataValid())
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, ("invalid meta data: " + validationReporter.getMessages()));
    }

    /** Just a wrapper for exceptions. */
    private static void validateSyntax(final byte[] data) throws SWORDErrorException, SWORDException
    {
        final XMLErrorHandler handler = new XMLErrorHandler(Reporter.off);
        try
        {
            EMDValidator.instance().validate(handler, new String(data, "UTF-8"), DEFAULT_SYNTAX_VERSION);
        }
        catch (final UnsupportedEncodingException exception)
        {
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "EASY metadata encoding exception: " + exception.getMessage());
        }
        catch (final SAXException exception)
        {
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "EASY metadata parse exception: " + exception.getMessage());
        }
        catch (nl.knaw.dans.pf.language.xml.exc.ValidatorException exception)
        {
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "EASY metadata validation exception: " + exception.getMessage());
        }
        catch (nl.knaw.dans.pf.language.xml.exc.SchemaCreationException exception)
        {
            throw new SWORDException("EASY metadata schema creation problem", exception);
        }
        if (!handler.passed())
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "Invalid EASY metadata: \n" + handler.getMessages());
    }
}
