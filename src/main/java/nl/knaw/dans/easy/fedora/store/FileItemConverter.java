package nl.knaw.dans.easy.fedora.store;

import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.store.AbstractDobConverter;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemMetadataImpl;
import nl.knaw.dans.easy.domain.model.DescriptiveMetadata;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileItemMetadata;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileItemConverter extends AbstractDobConverter<FileItemImpl>
{
    
    private static final Logger logger = LoggerFactory.getLogger(FileItemConverter.class);

    public FileItemConverter()
    {
        super(FileItem.NAMESPACE);
    }
    
    @Override
    public void deserialize(DigitalObject digitalObject, FileItemImpl fileItem) throws ObjectDeserializationException
    {
    	super.deserialize(digitalObject, fileItem);

        try
        {
            DatastreamVersion fmdVersion = digitalObject.getLatestVersion(FileItemMetadata.UNIT_ID);
            if (fmdVersion != null)
            {
                Element element = fmdVersion.getXmlContentElement();
                FileItemMetadataImpl fmd = (FileItemMetadataImpl) JiBXObjectFactory.unmarshal(FileItemMetadataImpl.class,
                        element);
                fmd.setTimestamp(fmdVersion.getTimestamp());
                fmd.setDirty(false);
                fileItem.setFileItemMetadata(fmd);
            }
            else
            {
                logger.warn("No fileItemMetadata found on retrieved digital object. sid=" + digitalObject.getSid());
            }
            
            DatastreamVersion dmdVersion = digitalObject.getLatestVersion(DescriptiveMetadata.UNIT_ID);
            if (dmdVersion != null)
            {
                Element element = dmdVersion.getXmlContentElement();
                fileItem.setDescriptiveMetadata(element);
            }
        }
        catch (XMLDeserializationException e)
        {
            throw new ObjectDeserializationException(e);
        }
    }

}
