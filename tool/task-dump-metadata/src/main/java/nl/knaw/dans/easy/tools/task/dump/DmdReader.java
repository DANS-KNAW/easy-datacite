package nl.knaw.dans.easy.tools.task.dump;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.xml.Dom4jReader;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.DescriptiveMetadataImpl;
import nl.knaw.dans.easy.domain.model.DescriptiveMetadata;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class DmdReader {
    DescriptiveMetadata read(String fileSid) throws ReaderException {
        return findDescriptiveMetadata(fileSid);
    }

    private DescriptiveMetadata findDescriptiveMetadata(String fileSid) throws ReaderException {
        try {
            Dom4jReader reader = new Dom4jReader(new File(findDmdData(fileSid)));
            return new DescriptiveMetadataImpl((Element) reader.getNode("/additionalName"));
        }
        catch (DocumentException e) {
            throw new ReaderException(e, "Could not parse DMD XML");
        }
    }

    private URI findDmdData(final String fileSid) throws ReaderException {
        try {
            return Data.getEasyStore().getDescriptiveMetadataURL(new DmoStoreId(fileSid)).toURI();
        }
        catch (URISyntaxException e) {
            throw new ReaderException(e, "Could not write DMD file location as URI");
        }
        catch (RepositoryException e) {
            throw new ReaderException(e, "Could not write DMD file location as URI");
        }
    }
}
