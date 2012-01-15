package nl.knaw.dans.easy.domain.download;

import java.util.List;

import nl.knaw.dans.common.lang.repo.AbstractDataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.relations.Relations;
import nl.knaw.dans.easy.domain.download.DownloadList.Level;

public class DownloadHistory extends AbstractDataModelObject
{

    public static final int LIST_TYPE_DATASET = DownloadList.TYPE_MONTH;
    
    private static final long serialVersionUID = -3007955623753515227L;
    
    public static final DmoNamespace NAMESPACE = new DmoNamespace("easy-dlh");
    
    private String objectId;
    private DownloadList downloadList;
    
    public DownloadHistory(String storeId)
    {
        super(storeId);
    }

    public DownloadHistory(String storeId, int listType, Level level, String objectId)
    {
        super(storeId);
        this.objectId = objectId;
        downloadList = new DownloadList(listType, level);
    }

    public DmoNamespace getDmoNamespace()
    {
        return NAMESPACE;
    }

    public boolean isDeletable()
    {
        return isRegisteredDeleted();
    }
    
    @Override
    public List<MetadataUnit> getMetadataUnits()
    {
        List<MetadataUnit> mdUnits = super.getMetadataUnits();
        mdUnits.add(getDownloadList());
        return mdUnits;
    }
    
    @Override
    protected Relations newRelationsObject()
    {
        if (objectId == null)
        {
            return new DownloadHistoryRelations(this);
        }
        else
        {
            return new DownloadHistoryRelations(this, objectId);
        }
    }
    
    public String getObjectId()
    {
        if (objectId == null)
        {
            DownloadHistoryRelations relations = (DownloadHistoryRelations) getRelations();
            return relations.getObjectId();
        }
        else
        {
            return objectId;
        }
    }

    public DownloadList getDownloadList()
    {
        return downloadList;
    }

    /**
     * NOT PUBLIC API.
     * @param downloadList the thing to set
     */
    public void setDownloadList(DownloadList downloadList)
    {
        this.downloadList = downloadList;
    }
    

}
