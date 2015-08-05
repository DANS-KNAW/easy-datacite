package nl.knaw.dans.common.fedora.store;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.fedora.fox.ContentDigestType;
import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.dataset.EasyFile;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemMetadataImpl;
import nl.knaw.dans.easy.domain.model.DescriptiveMetadata;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileItemMetadata;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileItemConverter extends AbstractDobConverter<FileItemImpl> {

    private static final Logger logger = LoggerFactory.getLogger(FileItemConverter.class);

    public FileItemConverter() {
        super(FileItem.NAMESPACE);
    }

    @Override
    public void deserialize(DigitalObject digitalObject, FileItemImpl fileItem) throws ObjectDeserializationException {
        super.deserialize(digitalObject, fileItem);

        try {
            DatastreamVersion fmdVersion = digitalObject.getLatestVersion(FileItemMetadata.UNIT_ID);
            if (fmdVersion != null) {
                Element element = fmdVersion.getXmlContentElement();
                FileItemMetadataImpl fmd = (FileItemMetadataImpl) JiBXObjectFactory.unmarshal(FileItemMetadataImpl.class, element);
                fmd.setTimestamp(fmdVersion.getTimestamp());
                fmd.setDirty(false);
                fileItem.setFileItemMetadata(fmd);
            } else {
                logger.warn("No fileItemMetadata found on retrieved digital object. sid=" + digitalObject.getSid());
            }

            DatastreamVersion dmdVersion = digitalObject.getLatestVersion(DescriptiveMetadata.UNIT_ID);
            if (dmdVersion != null) {
                Element element = dmdVersion.getXmlContentElement();
                fileItem.setDescriptiveMetadata(element);
            }

            DatastreamVersion efVersion = digitalObject.getLatestVersion(EasyFile.UNIT_ID);
            if (efVersion != null) {
                if (ContentDigestType.SHA_1 == ContentDigestType.forCode(efVersion.getChecksumType())) {
                    fileItem.setFileSha1Checksum(efVersion.getContentDigest());
                } else {
                    logger.warn("Checksum Type was not SHA_1 but: " + efVersion.getChecksumType());
                }
            }
        }
        catch (XMLDeserializationException e) {
            throw new ObjectDeserializationException(e);
        }
    }

}
