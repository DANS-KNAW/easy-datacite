package nl.knaw.dans.pf.language.ddm.api;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EasyMetadataFactory;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkException;
import nl.knaw.dans.pf.language.xml.crosswalk.Crosswalker;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;

public class Ddm2EmdCrosswalk extends Crosswalker<EasyMetadata, DDMValidator>
{
    private static final Logger logger = LoggerFactory.getLogger(Ddm2EmdCrosswalk.class);

    /** Creates an instance. */
    public Ddm2EmdCrosswalk()
    {
        super(DDMValidator.instance(), Ddm2EmdHandlerMap.getInstance());
    }

    /**
     * Creates an object after validation against an XSD.
     * 
     * @param file
     *        with XML content
     * @return null in case of errors reported by {@link #getXmlErrorHandler()}
     * @throws CrosswalkException
     */
    public EasyMetadata createFrom(final File file) throws CrosswalkException
    {
        return validateEMD(gurardedWalk(file, newTarget()));
    }

    /**
     * Creates an object after validation against an XSD.
     * 
     * @param xml
     *        the XML content
     * @return null in case of errors reported by {@link #getXmlErrorHandler()}
     * @throws CrosswalkException
     */
    public EasyMetadata createFrom(final String xml) throws CrosswalkException
    {
        return validateEMD(guardedWalk(xml, newTarget()));
    }

    private EasyMetadata newTarget()
    {
        return EasyMetadataFactory.newEasyMetadata(MetadataFormat.UNSPECIFIED);
    }

    private EasyMetadata validateEMD(final EasyMetadata emd) throws CrosswalkException
    {
        if (getXmlErrorHandler().getErrors().size() > 0 || getXmlErrorHandler().getFatalErrors().size() > 0)
            throw new CrosswalkException(getXmlErrorHandler().getMessages());
        try
        {
            // empty fields in DDM are skipped and may cause missing mandatory fields
            final String validatedXML = new EmdMarshaller(emd).getXmlString();
            logger.debug(validatedXML);
            return emd;
        }
        catch (final XMLSerializationException e)
        {
            logger.error("resulting Easy Meta Data is invalid: ", e);
            throw new CrosswalkException("resulting Easy Meta Data is invalid: " + e.getMessage(), e);
        }
    }
}
