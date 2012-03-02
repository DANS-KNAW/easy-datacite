package nl.knaw.dans.easy.sword;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler.Reporter;
import nl.knaw.dans.easy.business.dataset.MetadataValidator;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.emd.validation.FormatValidator;
import nl.knaw.dans.easy.domain.form.FormDefinition;
import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataImpl;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataValidator;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class EasyMetadataFacade
{
    private static final String DEFAULT_EMD_VERSION = EasyMetadataValidator.VERSION_0_1;

    private static Logger      logger              = LoggerFactory.getLogger(EasyMetadataFacade.class);

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
            throw newSwordInputException("EASY metadata unmarshall exception: " + exception.getMessage(), exception);
        }
        return metadata;
    }

    /** Wraps exceptions after logging them. */
    private static SWORDErrorException newSwordInputException(final String message, final Throwable exception)
    {
        logger.error(message, exception);
        return new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, message);
    }

    /**
     * 
     * @param easyMetaData xml text representation
     * @return unmarshalled easyMetaData
     * @throws SWORDErrorException
     * @throws SWORDException
     */
    public static EasyMetadata validate(final byte[] easyMetaData) throws SWORDErrorException, SWORDException
    {
        validateSyntax(easyMetaData);
        final EasyMetadata unmarshalled = unmarshallEasyMetaData(easyMetaData);
        validateMandatoryFields(unmarshalled);
        validateControlledVocabulairies(unmarshalled);
        return unmarshalled;
    }

    /** Just a wrapper for exceptions. */
    private static void validateControlledVocabulairies(final EasyMetadata metadata) throws SWORDErrorException, SWORDException
    {
        final FormDefinition formDefinition = EasyMetadataFacade.getFormDefinition(metadata);
        if (!new MetadataValidator().validate(formDefinition, metadata))
            throw newSwordInputException("invalid meta data\n" + extractValidationMessages(formDefinition), null);
    }

    /** Just a wrapper for exceptions. */
    private static void validateMandatoryFields(final EasyMetadata metadata) throws SWORDErrorException, SWORDException
    {
         final EasySwordValidationReporter validationReporter = new EasySwordValidationReporter();
         FormatValidator.instance().validate(metadata, validationReporter);
         if (!validationReporter.isMetadataValid())
         throw newSwordInputException("invalid meta data: "+validationReporter.getMessages(), null);
    }

    /** Just a wrapper for exceptions. */
    private static void validateSyntax(final byte[] data) throws SWORDErrorException, SWORDException
    {
        final XMLErrorHandler handler = new XMLErrorHandler(Reporter.off);
        try
        {
            EasyMetadataValidator.instance().validate(handler, new String(data, "UTF-8"), DEFAULT_EMD_VERSION);
        }
        catch (final ValidatorException exception)
        {
            throw newSwordInputException("EASY metadata validation exception: " + exception.getMessage(), exception);
        }
        catch (final UnsupportedEncodingException exception)
        {
            throw newSwordInputException("EASY metadata encoding exception: " + exception.getMessage(), exception);
        }
        catch (final SAXException exception)
        {
            throw newSwordInputException("EASY metadata parse exception: " + exception.getMessage(), exception);
        }
        catch (final SchemaCreationException exception)
        {
            throw newSwordInputException("EASY metadata schema creation problem", exception);
        }
        if (!handler.passed())
            throw newSwordInputException("Invalid EASY metadata: \n" + handler.getMessages(), null);
    }

    private static String extractValidationMessages(final FormDefinition formDefinition)
    {
        String msg = "";
        for (final FormPage formPage : formDefinition.getFormPages())
            for (final PanelDefinition pDef : formPage.getPanelDefinitions())
            {
                final String prefix = " " + formPage.getLabelResourceKey() + "." + pDef.getLabelResourceKey();
                if (pDef.getErrorMessages().size() > 0)
                    msg += prefix + " " + Arrays.deepToString(pDef.getErrorMessages().toArray());
                final Map<Integer, List<String>> messages = pDef.getItemErrorMessages();
                for (final int i : messages.keySet())
                    if (messages.get(i).size() > 0)
                        msg += prefix + "." + i + messages.get(i);
            }
        return msg;
    }

    public static FormDefinition getFormDefinition(final EasyMetadata emd) throws SWORDErrorException
    {
        final MetadataFormat mdFormat = emd.getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        final DepositDiscipline discipline;
        try
        {
            discipline = Services.getDepositService().getDiscipline(mdFormat);
        }
        catch (final ServiceException e)
        {
            throw newSwordInputException("Cannot get deposit discipline.", e);
        }
        if (discipline == null)
                throw newSwordInputException("Cannot get deposit discipline.", null);
        final FormDefinition formDefinition = discipline.getEmdFormDescriptor().getFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_ARCHIVIST);
        if (formDefinition == null)
            throw newSwordInputException("Cannot get formdefinition for MetadataFormat " + mdFormat.toString(), null);
        return formDefinition;
    }
}
