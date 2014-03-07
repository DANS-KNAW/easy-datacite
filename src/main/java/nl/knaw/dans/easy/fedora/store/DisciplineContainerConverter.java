package nl.knaw.dans.easy.fedora.store;

import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.store.AbstractDobConverter;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadata;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadataImpl;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisciplineContainerConverter extends AbstractDobConverter<DisciplineContainer>
{
    private static final Logger logger = LoggerFactory.getLogger(DisciplineContainerConverter.class);

    public DisciplineContainerConverter()
    {
        super(DisciplineContainer.NAMESPACE);
    }

    @Override
    public void deserialize(DigitalObject digitalObject, DisciplineContainer dmo) throws ObjectDeserializationException
    {
        super.deserialize(digitalObject, dmo);
        DisciplineContainerImpl discipline = (DisciplineContainerImpl) dmo;

        try
        {
            DatastreamVersion dmdVersion = digitalObject.getLatestVersion(DisciplineMetadata.UNIT_ID);
            if (dmdVersion != null)
            {
                Element element = dmdVersion.getXmlContentElement();
                DisciplineMetadataImpl dmd = (DisciplineMetadataImpl) JiBXObjectFactory.unmarshal(DisciplineMetadataImpl.class, element);
                dmd.setTimestamp(dmdVersion.getTimestamp());
                dmd.setDirty(false);
                discipline.setDisciplineMetadata(dmd);
            }
            else
            {
                logger.warn("No discipline metadata found on retrieved digital object. sid=" + digitalObject.getSid());
            }
        }
        catch (XMLDeserializationException e)
        {
            throw new ObjectDeserializationException(e);
        }
    }

}
