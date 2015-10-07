package nl.knaw.dans.easy.tools.task.dump;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.fedora.rdf.FedoraURIReference;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.FileItemMetadataImpl;
import nl.knaw.dans.easy.domain.model.FileItemMetadata;
import nl.knaw.dans.easy.tools.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;

import fedora.client.FedoraClient;
import fedora.server.types.gen.MIMETypedStream;

public class FileItemMetadataReader {
    private static final Logger logger = LoggerFactory.getLogger(FileItemMetadataReader.class);

    private int tries;
    private boolean includeFilePids;
    private boolean includeFileSha1Hash;
    private boolean includeMimeType;

    FileItemMetadataReader tries(int tries) {
        this.tries = tries;
        return this;
    }

    FileItemMetadataReader includeFilePids(boolean includeFilePids) {
        this.includeFilePids = includeFilePids;
        return this;
    }

    FileItemMetadataReader includeFileSha1Hash(boolean includeFileSha1Hash) {
        this.includeFileSha1Hash = includeFileSha1Hash;
        return this;
    }

    FileItemMetadataReader includeMimeType(boolean includeMimeType) {
        this.includeMimeType = includeMimeType;
        return this;
    }

    List<FileItemMetadataPrinter> read(String sid) {
        Exception exception = null;

        for (int i = 0; i < tries; ++i) {
            try {
                logger.debug(String.format("... getting file metadata try number %d/%d", i + 1, tries));
                final String dmoObjectRef = FedoraURIReference.create(sid);

                // @formatter:off
                final String query = String.format("select ?s from <#ri> where {" + "   ?s <%s> <%s> ."
                        + "   ?s <fedora-model:hasModel> <info:fedora/easy-model:EDM1FILE> .}", RelsConstants.DANS_NS.IS_SUBORDINATE_TO.stringValue(),
                        dmoObjectRef);
                // @formatter:on
                final Map<String, String> params = new HashMap<String, String>();
                params.put("lang", "sparql");
                params.put("query", query);

                final List<String> filePids = getFileListFromTuples(getTuples(params));
                final List<FileItemMetadataPrinter> files = new LinkedList<FileItemMetadataPrinter>();

                for (final String filePid : filePids) {
                    files.add(createFileItemMetadataPrinter(stripNamespace(filePid)));
                }

                return files;
            }
            catch (ReaderException e) {
                exception = e;
            }
        }

        throw new RuntimeException("Too many tries reading file metadata", exception);
    }

    private String stripNamespace(String filePid) {
        int slashIndex = filePid.indexOf('/');

        return slashIndex == -1 ? filePid : filePid.substring(slashIndex + 1);
    }

    private List<String> getFileListFromTuples(final TupleIterator tuples) throws ReaderException {
        final List<String> result = new LinkedList<String>();

        try {
            while (tuples.hasNext()) {
                result.add(tuples.next().get("s").toString());
            }

            return result;
        }
        catch (final TrippiException e) {
            throw new ReaderException(e, "Could not read file list from Fedora Resource Index result");
        }
    }

    private TupleIterator getTuples(final Map<String, String> params) throws ReaderException {
        final FedoraClient fc = getFedoraClient();
        try {
            return fc.getTuples(params);
        }
        catch (final IOException e) {
            throw new ReaderException(e, "Could not read tuples from Fedora Resource Index, params: '%s'", params);
        }
    }

    private FedoraClient getFedoraClient() throws ReaderException {
        try {
            return Application.getFedora().getRepository().getFedoraClient();
        }
        catch (final RepositoryException e) {
            throw new ReaderException(e, "Could not get Fedora Client");
        }
    }

    private FileItemMetadataPrinter createFileItemMetadataPrinter(final String filePid) throws ReaderException {
        try {
            return new FileItemMetadataPrinter(stripNamespace(filePid), findFileMetaData(filePid), findFileData(filePid), includeFilePids, includeFileSha1Hash,
                    includeMimeType);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new ReaderException(e, "Could not calculate hash for file.  Algorithm not supported: '%s'", e.getMessage());
        }
        catch (IOException e) {
            throw new ReaderException(e, "Could not calculate hash for file.  IOException: '%s'", e.getMessage());
        }
    }

    private FileItemMetadata findFileMetaData(final String filePid) throws ReaderException {
        logger.debug(String.format("... ... for file '%s'", filePid));
        try {
            final MIMETypedStream stream = Application.getFedora().getDatastreamAccessor().getDatastreamDissemination(filePid, FileItemMetadata.UNIT_ID, null);
            return (FileItemMetadata) JiBXObjectFactory.unmarshal(FileItemMetadataImpl.class, stream.getStream());
        }
        catch (final RepositoryException e) {
            throw new ReaderException(e, "Could not read file metadata from repository for file '%s'", filePid);
        }
        catch (final XMLDeserializationException e) {
            throw new ReaderException(e, "Could deserialize file metadata as XML for file '%s'", filePid);
        }
    }

    private URL findFileData(final String filePid) throws ReaderException {
        return Data.getEasyStore().getFileURL(new DmoStoreId(filePid));
    }
}
