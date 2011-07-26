package nl.knaw.dans.easy.fedora.store;

import nl.knaw.dans.common.fedora.fox.DatastreamVersion;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.store.AbstractDobConverter;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.domain.dataset.AdministrativeMetadataImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.ItemContainerMetadataImpl;
import nl.knaw.dans.easy.domain.dataset.PermissionSequenceListImpl;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItemContainerMetadata;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataImpl;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetConverter extends AbstractDobConverter<DatasetImpl>
{
    private static final Logger logger = LoggerFactory.getLogger(DatasetConverter.class);

    public DatasetConverter()
    {
        super(Dataset.NAMESPACE);
    }

    @Override
    public DigitalObject serialize(DatasetImpl dataset) throws ObjectSerializationException
    {
        try
        {
            dataset.setParents(dataset.getParentDisciplines());
        }
        catch (RepositoryException e)
        {
            throw new ApplicationException(e);
        }
        catch (ObjectNotFoundException e)
        {
            throw new ApplicationException(e);
        }
        catch (DomainException e)
        {
            throw new ApplicationException(e);
        }

        return super.serialize(dataset);
    }
    
    @Override
    public void prepareForUpdate(DatasetImpl dataset) throws ObjectSerializationException
    {
        try
        {
            dataset.setParents(dataset.getParentDisciplines());
        }
        catch (RepositoryException e)
        {
            throw new ApplicationException(e);
        }
        catch (ObjectNotFoundException e)
        {
            throw new ApplicationException(e);
        }
        catch (DomainException e)
        {
            throw new ApplicationException(e);
        }
    }

    @Override
    public void deserialize(DigitalObject digitalObject, DatasetImpl dmo) throws ObjectDeserializationException
    {
        super.deserialize(digitalObject, dmo);
        DatasetImpl dataset = (DatasetImpl) dmo;

        try
        {
            DatastreamVersion emdVersion = digitalObject.getLatestVersion(EasyMetadata.UNIT_ID);
            if (emdVersion != null)
            {
                Element element = emdVersion.getXmlContentElement();
                EasyMetadata emd = (EasyMetadata) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, element);
                emd.setTimestamp(emdVersion.getTimestamp());
                emd.setDirty(false);
                dataset.setEasyMetadata(emd);
            }
            else
            {
                logger.warn("No easyMetadata found on retrieved digital object. sid=" + digitalObject.getSid());
            }

            DatastreamVersion amdVersion = digitalObject.getLatestVersion(AdministrativeMetadata.UNIT_ID);
            if (amdVersion != null)
            {
                Element element = amdVersion.getXmlContentElement();
                AdministrativeMetadata amd = (AdministrativeMetadata) JiBXObjectFactory.unmarshal(
                        AdministrativeMetadataImpl.class, element);
                amd.setTimestamp(amdVersion.getTimestamp());
                amd.setDirty(false);
                dataset.setAdministrativeMetadata(amd);
            }
            else
            {
                logger.warn("No administrative metadata found on retrieved digital object. sid="
                        + digitalObject.getSid());
            }

            DatastreamVersion icmdVersion = digitalObject.getLatestVersion(DatasetItemContainerMetadata.UNIT_ID);
            if (icmdVersion != null)
            {
                Element element = icmdVersion.getXmlContentElement();
                ItemContainerMetadataImpl icmd = (ItemContainerMetadataImpl) JiBXObjectFactory.unmarshal(
                        ItemContainerMetadataImpl.class, element);
                icmd.setDirty(false);
                icmd.setTimestamp(icmdVersion.getTimestamp());
                dataset.setItemContainerMetadata(icmd);
            }

            DatastreamVersion pslVersion = digitalObject.getLatestVersion(PermissionSequenceList.UNIT_ID);
            if (pslVersion != null)
            {
                Element element = pslVersion.getXmlContentElement();
                PermissionSequenceList psl = (PermissionSequenceListImpl) JiBXObjectFactory.unmarshal(
                        PermissionSequenceListImpl.class, element);
                psl.setDirty(false);
                psl.setTimestamp(pslVersion.getTimestamp());
                dataset.setPermissionSequenceList(psl);
            }

        }
        catch (XMLDeserializationException e)
        {
            throw new ObjectDeserializationException(e);
        }
    }

}
