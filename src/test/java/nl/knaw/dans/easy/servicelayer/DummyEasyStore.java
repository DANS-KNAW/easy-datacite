package nl.knaw.dans.easy.servicelayer;
import java.net.URL;
import java.util.List;

import org.joda.time.DateTime;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.BinaryUnit;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoUpdateConcurrencyGuard;
import nl.knaw.dans.common.lang.repo.DmoContext;
import nl.knaw.dans.common.lang.repo.DmoFactory;
import nl.knaw.dans.common.lang.repo.DmoStoreEventListener;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.repo.exception.ConcurrentUpdateException;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;

public class DummyEasyStore implements EasyStore
{

    @Override
    public DownloadHistory findDownloadHistoryFor(DataModelObject objectDmo, String period) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DownloadHistory findDownloadHistoryFor(String objectStoreId, String period) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DownloadHistory> findDownloadHistoryFor(DataModelObject dmo) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addEventListener(DmoStoreEventListener storeEventListener)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean addRelationship(String storeId, String relationship, String object, boolean isLiteral, String dataType) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

//    @Override
//    public DataModelObject createDmo(Class<? extends DataModelObject> clazz) throws RepositoryException
//    {
//        // TODO Auto-generated method stub
//        return null;
//    }

//    @Override
//    public DmoContext getContext()
//    {
//        // TODO Auto-generated method stub
//        return null;
//    }

//    @Override
//    public DmoFactory getDmoFactory()
//    {
//        // TODO Auto-generated method stub
//        return null;
//    }

    @Override
    public URL getFileURL(String storeId, String unitId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URL getFileURL(String storeId, String unitId, DateTime dateTime)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DateTime getLastModified(String storeId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] getObjectXML(String storeId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Relation> getRelations(String subject, String predicate, String object) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Relation> getRelations(String storeId, String predicate) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getSidsByContentModel(String contentModel) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<UnitMetadata> getUnitMetadata(String storeId, String unitId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String ingest(DataModelObject storable, String logMessage) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInvalidated(DataModelObject dmo) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DateTime purge(DataModelObject object, boolean force, String logMessage) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean purgeRelationship(String storeId, String relationship, String object, boolean isLiteral, String dataType) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeEventListener(DmoStoreEventListener storeEventListener)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DataModelObject retrieve(String storeId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setConcurrencyGuard(DmoUpdateConcurrencyGuard concurrencyGuard)
    {
        // TODO Auto-generated method stub
        
    }

//    @Override
//    public void setContext(DmoContext namespaceRegistry)
//    {
//        // TODO Auto-generated method stub
//        
//    }

//    @Override
//    public void setDmoFactory(DmoFactory factory)
//    {
//        // TODO Auto-generated method stub
//        
//    }

    @Override
    public void setEventListeners(List<DmoStoreEventListener> storeEventListeners)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public DateTime update(DataModelObject storable, String logMessage) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DateTime update(DataModelObject dmo, boolean skipDirtyChecking, String logMessage) throws ConcurrentUpdateException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String nextSid(String objectNamespace) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public boolean isUpdateable(DataModelObject dmo, String changeOwner) throws RepositoryException
	{
		return false;
	}

	@Override
	public DateTime update(DataModelObject dmo, boolean skipDirtyChecking, String logMessage, String changer) throws ConcurrentUpdateException,
			RepositoryException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUpdateable(DataModelObject dmo) throws RepositoryException
	{
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public JumpoffDmo findJumpoffDmoFor(DataModelObject dmo) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JumpoffDmo findJumpoffDmoFor(String objectStoreId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<UnitMetadata> getUnitMetadata(String storeId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DateTime puregUnit(String storeId, String unitId, String logMessage) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addOrUpdateBinaryUnit(String storeId, BinaryUnit binaryUnit, String logMessage) throws RepositoryException
    {
        // TODO Auto-generated method stub
        
    }


    @Override
    public List<String> findSubordinates(String storeId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DmoStoreEventListener> getListeners()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EasyMetadata getEasyMetaData(String sid, DateTime asOfDateTime) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean exists(String storeId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void addOrUpdateMetadataUnit(String storeId, MetadataUnit metadataUnit, String logMessage) throws RepositoryException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public URL getFileURL(String sid)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URL getDescriptiveMetadataURL(String sid)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URL getStreamURL(String storeId, String streamId)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
