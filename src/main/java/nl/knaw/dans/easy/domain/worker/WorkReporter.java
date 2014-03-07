package nl.knaw.dans.easy.domain.worker;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;

public class WorkReporter extends DefaultWorkListener
{

    private List<String> ingestedObjectIds = new ArrayList<String>();
    private List<String> updatedObjectIds = new ArrayList<String>();
    private List<String> purgedObjectIds = new ArrayList<String>();
    private List<String> retrievedObjectIds = new ArrayList<String>();
    private List<String> updatedMetadataUnitIds = new ArrayList<String>();

    private int ingestedDatasetCount;
    private int updatedDatasetCount;

    private int ingestedFolderItemCount;
    private int updatedFolderItemCount;

    private int ingestedFileItemCount;
    private int updatedFileItemCount;

    public void afterIngestDataset(Dataset dataset)
    {
        ingestedDatasetCount++;
        ingestedObjectIds.add(dataset.getStoreId());
    }

    public void afterIngestFile(FileItem fileItem)
    {
        ingestedFileItemCount++;
        ingestedObjectIds.add(fileItem.getStoreId());
    }

    public void afterIngestFolder(FolderItem folderItem)
    {
        ingestedFolderItemCount++;
        ingestedObjectIds.add(folderItem.getStoreId());
    }

    @Override
    public void afterIngest(DataModelObject dmo)
    {
        super.afterIngest(dmo);
        if (dmo instanceof Dataset)
            afterIngestDataset((Dataset) dmo);
        else if (dmo instanceof FileItem)
            afterIngestFile((FileItem) dmo);
        else if (dmo instanceof FolderItem)
            afterIngestFolder((FolderItem) dmo);
    }

    public void afterUpdateDataset(Dataset dataset)
    {
        updatedDatasetCount++;
        updatedObjectIds.add(dataset.getStoreId());
    }

    public void afterUpdateFile(FileItem fileItem)
    {
        updatedFileItemCount++;
        updatedObjectIds.add(fileItem.getStoreId());
    }

    public void afterUpdateFolder(FolderItem folderItem)
    {
        updatedFolderItemCount++;
        updatedObjectIds.add(folderItem.getStoreId());
    }

    @Override
    public void afterUpdate(DataModelObject dmo)
    {
        super.afterUpdate(dmo);
        if (dmo instanceof Dataset)
            afterUpdateDataset((Dataset) dmo);
        else if (dmo instanceof FileItem)
            afterUpdateFile((FileItem) dmo);
        else if (dmo instanceof FolderItem)
            afterUpdateFolder((FolderItem) dmo);
    }

    @Override
    public void afterPurge(DataModelObject dmo)
    {
        super.afterPurge(dmo);
        purgedObjectIds.add(dmo.getStoreId());
    }

    @Override
    public void afterUpdateMetadataUnit(DataModelObject dmo, MetadataUnit mdUnit)
    {
        updatedMetadataUnitIds.add(dmo.getStoreId() + "/" + mdUnit.getUnitId());
    }

    @Override
    public void afterRetrieveObject(DataModelObject dmo)
    {
        retrievedObjectIds.add(dmo.getStoreId());
    }

    public List<String> getIngestedObjectIds()
    {
        return ingestedObjectIds;
    }

    public List<String> getUpdatedObjectIds()
    {
        return updatedObjectIds;
    }

    public List<String> getUpdatedMetadataUnitIds()
    {
        return updatedMetadataUnitIds;
    }

    public List<String> getPurgedObjectIds()
    {
        return purgedObjectIds;
    }

    public List<String> getRetrievedObjectIds()
    {
        return retrievedObjectIds;
    }

    public int getIngestedDatasetCount()
    {
        return ingestedDatasetCount;
    }

    public int getUpdatedDatasetCount()
    {
        return updatedDatasetCount;
    }

    public int getIngestedFolderItemCount()
    {
        return ingestedFolderItemCount;
    }

    public int getUpdatedFolderItemCount()
    {
        return updatedFolderItemCount;
    }

    public int getIngestedFileItemCount()
    {
        return ingestedFileItemCount;
    }

    public int getUpdatedFileItemCount()
    {
        return updatedFileItemCount;
    }

    public int getPurgedObjectCount()
    {
        return purgedObjectIds.size();
    }

    public int getUpdatedMetadataUnitCount()
    {
        return updatedMetadataUnitIds.size();
    }

    public int getRetrievedObjectCount()
    {
        return retrievedObjectIds.size();
    }

    public int getIngestedObjectCount()
    {
        return ingestedObjectIds.size();
    }

    public int getUpdatedObjectCount()
    {
        return updatedObjectIds.size();
    }

    public int getTotalActionCount()
    {
        return getIngestedObjectCount() + getRetrievedObjectCount() + getUpdatedObjectCount() + getPurgedObjectCount() + getUpdatedMetadataUnitCount();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n").append("ingestedDatasetCount=").append(ingestedDatasetCount).append(" ingestedFolderItemCount=")
                .append(ingestedFolderItemCount).append(" ingestedFileItemCount=").append(ingestedFileItemCount).append("\n").append("updatedDatasetCount=")
                .append(updatedDatasetCount).append(" updatedFolderItemCount=").append(updatedFolderItemCount).append(" updatedFileItemCount=")
                .append(updatedFileItemCount).append(" updatedMetadataUnitCount=").append(getUpdatedMetadataUnitCount()).append("\n")
                .append("retrievedObjectCount=").append(getRetrievedObjectCount()).append(" purgedObjectCount=").append(getPurgedObjectCount())
                .append("\nTotalActionCount=").append(getTotalActionCount());
        return sb.toString();
    }

}
