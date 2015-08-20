package nl.knaw.dans.easy.tools.task.dump;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.domain.dataset.PermissionSequenceListImpl;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.tools.Application;
import fedora.server.types.gen.MIMETypedStream;

public class PermissionSequenceListReader {
    PermissionSequenceList read(String datasetSid) {
        try {
            return findPermissionSequenceList(datasetSid);
        }
        catch (ReaderException e) {
            throw new RuntimeException(e);
        }
    }

    private PermissionSequenceList findPermissionSequenceList(String datasetSid) throws ReaderException {
        try {
            final MIMETypedStream stream = Application.getFedora().getDatastreamAccessor()
                    .getDatastreamDissemination(datasetSid, PermissionSequenceList.UNIT_ID, null);
            return (PermissionSequenceList) JiBXObjectFactory.unmarshal(PermissionSequenceListImpl.class, stream.getStream());
        }
        catch (final RepositoryException e) {
            throw new ReaderException(e, "Could not read permission sequences from repository for dataset '%s'", datasetSid);
        }
        catch (final XMLDeserializationException e) {
            throw new ReaderException(e, "Could deserialize permission sequences metadata as XML for dataset '%s'", datasetSid);
        }
    }

}
