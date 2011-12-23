package nl.knaw.dans.easy.fedora.store;

import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.store.AbstractDobConverter;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.domain.collections.SimpleCollection;
import nl.knaw.dans.easy.domain.collections.SimpleCollectionImpl;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCollectionConverter extends AbstractDobConverter<SimpleCollectionImpl>
{
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleCollectionConverter.class);

    public SimpleCollectionConverter()
    {
        super(SimpleCollection.NAMESPACE);
    }
    
    @Override
    public void deserialize(DigitalObject dob, SimpleCollectionImpl simColl) throws ObjectDeserializationException
    {
        super.deserialize(dob, simColl);
        
        try
        {
            DatastreamVersion dcVersion = dob.getLatestVersion(DublinCoreMetadata.UNIT_ID);
            if (dcVersion != null)
            {
                Element element = dcVersion.getXmlContentElement();
                JiBXDublinCoreMetadata jdc = 
                    (JiBXDublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, element);
                jdc.setTimestamp(dcVersion.getTimestamp());
                simColl.setDcMetadata(jdc);
            }
            else
            {
                logger.warn("No easyMetadata found on retrieved digital object. sid=" + dob.getSid());
            }
        }
        catch (XMLDeserializationException e)
        {
            throw new ObjectDeserializationException(e);
        }
    }

}
