package nl.knaw.dans.easy.fedora.store;

import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.store.AbstractDobConverter;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.domain.dataset.FolderItemImpl;
import nl.knaw.dans.easy.domain.dataset.ItemContainerMetadataImpl;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.DatasetItemContainerMetadata;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolderItemConverter extends AbstractDobConverter<FolderItemImpl>
{

    private static final Logger logger = LoggerFactory.getLogger(FolderItemConverter.class);

    public FolderItemConverter()
    {
        super(FolderItem.NAMESPACE);
    }

    @Override
    public void deserialize(DigitalObject digitalObject, FolderItemImpl folderItem) throws ObjectDeserializationException
    {
        super.deserialize(digitalObject, folderItem);

        try
        {
            DatastreamVersion fmdVersion = digitalObject.getLatestVersion(DatasetItemContainerMetadata.UNIT_ID);
            if (fmdVersion != null)
            {
                Element element = fmdVersion.getXmlContentElement();
                ItemContainerMetadataImpl fmd = (ItemContainerMetadataImpl) JiBXObjectFactory.unmarshal(ItemContainerMetadataImpl.class, element);
                fmd.setTimestamp(fmdVersion.getTimestamp());
                fmd.setDirty(false);
                folderItem.setItemContainerMetadata(fmd);
            }
            else
            {
                logger.warn("No folderItemMetadata found on retrieved digital object. sid=" + digitalObject.getSid());
            }
        }
        catch (XMLDeserializationException e)
        {
            throw new ObjectDeserializationException(e);
        }
    }

}
