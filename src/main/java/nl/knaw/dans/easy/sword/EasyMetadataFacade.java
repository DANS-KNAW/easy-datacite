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
import org.xml.sax.SAXException;

public class EasyMetadataFacade
{
    private static final String DEFAULT_EMD_VERSION = EasyMetadataValidator.VERSION_0_1;

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
        validateControlledVocabulairies(unmarshalled);
        validateMandatoryFields(unmarshalled);
        return unmarshalled;
    }

    /** Just a wrapper for exceptions. */
    private static void validateMandatoryFields(final EasyMetadata metadata) throws SWORDErrorException, SWORDException
    {
        final FormDefinition formDefinition = EasyMetadataFacade.getFormDefinition(metadata);
        if (!new MetadataValidator().validate(formDefinition, metadata))
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, ("invalid meta data\n" + extractValidationMessages(formDefinition)));
    }

    /** Just a wrapper for exceptions. */
    private static void validateControlledVocabulairies(final EasyMetadata metadata) throws SWORDErrorException, SWORDException
    {
         final EasySwordValidationReporter validationReporter = new EasySwordValidationReporter();
         FormatValidator.instance().validate(metadata, validationReporter);
         if (!validationReporter.isMetadataValid())
         throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, ("invalid meta data: "+validationReporter.getMessages()));
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
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "EASY metadata validation exception: " + exception.getMessage());
        }
        catch (final UnsupportedEncodingException exception)
        {
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "EASY metadata encoding exception: " + exception.getMessage());
        }
        catch (final SAXException exception)
        {
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "EASY metadata parse exception: " + exception.getMessage());
        }
        catch (final SchemaCreationException exception)
        {
            throw new SWORDException("EASY metadata schema creation problem",exception);
        }
        if (!handler.passed())
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "Invalid EASY metadata: \n" + handler.getMessages());
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
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "Cannot get deposit discipline.");
        }
        if (discipline == null)
                throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, "Cannot get deposit discipline.");
        final FormDefinition formDefinition = discipline.getEmdFormDescriptor().getFormDefinition(DepositDiscipline.EMD_DEPOSITFORM_ARCHIVIST);
        if (formDefinition == null)
            throw new SWORDErrorException(ErrorCodes.ERROR_BAD_REQUEST, ("Cannot get formdefinition for MetadataFormat " + mdFormat.toString()));
        return formDefinition;
    }
}
