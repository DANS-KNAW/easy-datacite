package nl.knaw.dans.easy.tools.task.dump;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyMetadataReader {
    private static final Logger logger = LoggerFactory.getLogger(EasyMetadataReader.class);
    private final int tries;

    EasyMetadataReader(int tries) {
        this.tries = tries;
    }

    EasyMetadata read(String sid) {
        RepositoryException exception = null;

        for (int i = 0; i < tries; ++i) {
            try {
                logger.debug(String.format("... reading EASY dataset metadata for %s, try number %d/%d", sid, i + 1, tries));
                return Data.getEasyStore().getEasyMetaData(new DmoStoreId(sid), null);
            }
            catch (final RepositoryException e) {
                exception = e;
            }
        }

        throw new RuntimeException(String.format("Could not %s after %d tries. Message: '%s'", "get EASY metadata", tries, exception.getMessage()), exception);
    }

}
