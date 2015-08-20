package nl.knaw.dans.easy.tools.task.dump;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.domain.dataset.AdministrativeMetadataImpl;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.tools.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fedora.server.types.gen.MIMETypedStream;

public class AmdReader {
    private static final Logger logger = LoggerFactory.getLogger(AmdReader.class);

    AdministrativeMetadata read(String datasetSid) {
        try {
            return findAdministrativeMetadata(datasetSid);
        }
        catch (ReaderException e) {
            throw new RuntimeException(e);
        }

    }

    private AdministrativeMetadata findAdministrativeMetadata(String datasetSid) throws ReaderException {
        try {
            final MIMETypedStream stream = Application.getFedora().getDatastreamAccessor()
                    .getDatastreamDissemination(datasetSid, AdministrativeMetadata.UNIT_ID, null);
            return (AdministrativeMetadata) JiBXObjectFactory.unmarshal(AdministrativeMetadataImpl.class, stream.getStream());
        }
        catch (final RepositoryException e) {
            throw new ReaderException(e, "Could not read administrative metadata from repository for file '%s'", datasetSid);
        }
        catch (final XMLDeserializationException e) {
            throw new ReaderException(e, "Could deserialize administrative metadata as XML for file '%s'", datasetSid);
        }
    }

}
