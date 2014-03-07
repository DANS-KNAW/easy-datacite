package nl.knaw.dans.common.lang.repo.dummy;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDmoStore;
import nl.knaw.dans.common.lang.repo.BinaryUnit;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.DsUnitId;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.repo.exception.DmoStoreEventListenerException;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.relations.Relation;

import org.joda.time.DateTime;

public class DummyDmoStore extends AbstractDmoStore
{
    private static final long serialVersionUID = -3709691922750780482L;
    private DummySidDispenser dispenser;
    private DataModelObject returnDmo;
    private DateTime lastModified;
    private List<Relation> relations;

    // this one keeps the dummy stores unique if several are made
    private static int no = 1;

    public DummyDmoStore()
    {
        super("dummyStore" + no);// , new DummyDmoContext());;
        no++;
        dispenser = new DummySidDispenser();
    }

    @Override
    protected String doIngest(DataModelObject dmo, String logMessage) throws RepositoryException, ObjectSerializationException, DmoStoreEventListenerException,
            ObjectExistsException
    {
        return dispenser.nextSid(dmo.getDmoNamespace());
    }

    @Override
    protected DateTime doPurge(DataModelObject dmo, boolean force, String logMessage) throws DmoStoreEventListenerException, RepositoryException
    {
        return new DateTime();
    }

    public void retrieveReturns(DataModelObject dmo)
    {
        returnDmo = dmo;
    }

    @Override
    protected DataModelObject doRetrieve(DmoStoreId storeId) throws ObjectNotInStoreException, RepositoryException, ObjectDeserializationException
    {
        return returnDmo;
    }

    @Override
    protected DateTime doUpdate(DataModelObject dmo, boolean skipChangeChecking, String logMessage) throws DmoStoreEventListenerException, RepositoryException
    {
        return new DateTime();
    }

    public void getRelationsReturns(List<Relation> relations)
    {
        this.relations = relations;
    }

    @SuppressWarnings("unchecked")
    public List<Relation> getRelations(String subject, String predicate, String object) throws RepositoryException
    {
        return (List<Relation>) (relations == null ? Collections.emptyList() : relations);
    }

    public List<String> getSidsByContentModel(String contentModel) throws RepositoryException
    {
        return null;
    }

    public byte[] getObjectXML(String storeId) throws ObjectNotInStoreException, RepositoryException
    {
        return null;
    }

    public String nextSid(DmoNamespace objectNamespace) throws RepositoryException
    {
        return dispenser.nextSid(objectNamespace);
    }

    public void getLastModifiedReturns(DateTime lastModified)
    {
        this.lastModified = lastModified;
    }

    public DateTime getLastModified(String storeId) throws RepositoryException
    {
        return lastModified;
    }

    @Override
    public void addOrUpdateMetadataUnit(DmoStoreId dmoStoreId, MetadataUnit metadataUnit, String logMessage) throws RepositoryException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addOrUpdateBinaryUnit(DmoStoreId dmoStoreId, BinaryUnit binaryUnit, String logMessage) throws RepositoryException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public DateTime purgeUnit(DmoStoreId dmoStoreId, DsUnitId unitId, DateTime creationDate, String logMessage) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JumpoffDmo findJumpoffDmoFor(DataModelObject dmo) throws ObjectNotInStoreException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JumpoffDmo findJumpoffDmoFor(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean exists(DmoStoreId dmoStoreId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public byte[] getObjectXML(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DateTime getLastModified(DmoStoreId dmoStoreId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Relation> getRelations(DmoStoreId dmoStoreId, String predicate) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean addRelationship(DmoStoreId dmoStoreId, String relationship, String object, boolean isLiteral, String dataType) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean purgeRelationship(DmoStoreId dmoStoreId, String relationship, String object, boolean isLiteral, String dataType) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<DmoStoreId> getSidsByContentModel(DmoStoreId contentModelId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DmoStoreId> findSubordinates(DmoStoreId dmoStoreId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URL getFileURL(DmoStoreId dmoStoreId, DsUnitId unitId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URL getFileURL(DmoStoreId dmoStoreId, DsUnitId unitId, DateTime dateTime)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<UnitMetadata> getUnitMetadata(DmoStoreId dmoStoreId, DsUnitId unitId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<UnitMetadata> getUnitMetadata(DmoStoreId dmoStoreId) throws RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
