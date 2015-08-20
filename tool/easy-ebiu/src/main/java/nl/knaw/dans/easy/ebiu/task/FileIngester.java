package nl.knaw.dans.easy.ebiu.task;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.relations.AbstractRelations;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.item.ItemIngester;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItem;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileOntology;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.FileOntology.MetadataFormat;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.DefaultWorkListener;
import nl.knaw.dans.easy.ebiu.AbstractTask;
import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.xml.ResourceMetadata;
import nl.knaw.dans.easy.xml.ResourceMetadataList;

public class FileIngester extends AbstractTask {

    public static final String DEFAULT_RELATIVE_PATH = "filedata";

    private final String relativePath;

    private String absolutePath;

    public FileIngester() {
        this(DEFAULT_RELATIVE_PATH);
    }

    public FileIngester(String relativePath) {
        this.relativePath = relativePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public void run(JointMap joint) throws FatalTaskException {
        File rootFile = getRootFile(joint);
        if (!rootFile.exists()) {
            RL.warn(new Event("No filedata", rootFile.getAbsolutePath() + " does not exist."));
            return;
        }
        if (!rootFile.isDirectory()) {
            RL.error(new Event("Not a directory", rootFile.getAbsolutePath()));
            throw new TaskCycleException("Not a directory: " + rootFile.getAbsolutePath(), this);
        }

        EasyUser user = joint.getEasyUser();
        Dataset dataset = joint.getDataset();
        DmoStoreId parentId = null; // dataset is container
        IngestDelegator delegator = new IngestDelegator(joint);
        IngestListener listener = new IngestListener(joint);

        try {
            Services.getItemService().addDirectoryContents(user, dataset, parentId, rootFile, delegator, listener);
            joint.setCycleProcessingCompleted(isFinalStepInCycle());
        }
        catch (ServiceException e) {
            RL.error(new Event(e));
            throw new TaskCycleException(e, this);
        }
    }

    private File getRootFile(JointMap joint) {
        if (absolutePath == null) {
            return new File(joint.getCurrentDirectory(), relativePath);
        } else {
            return new File(absolutePath);
        }
    }

    private static class IngestDelegator extends ItemIngester.DefaultDelegator {

        private final ResourceMetadataList resourceMetadataList;
        private final HashMap<MetadataFormat, String> additionalMetadataFiles;

        public IngestDelegator(JointMap joint) {
            super(joint.getDataset());
            this.resourceMetadataList = joint.getResourceMetadataList();
            this.additionalMetadataFiles = joint.getAdditionalMetadataFiles();
        }

        @Override
        public void setFileRights(FileItem fileItem) {
            ResourceMetadata rm = getResourceMetadata(fileItem.getPath());
            if (rm != null) {
                setDiscoverRights(rm, fileItem);
                setReadRights(rm, fileItem);
            } else {
                super.setFileRights(fileItem);
            }
        }

        @Override
        public void addAdditionalMetadata(FileItem fileItem) {
            ResourceMetadata rm = getResourceMetadata(fileItem.getPath());
            if (rm != null && rm.hasAdditionalMetadata()) {
                fileItem.getFileItemMetadata().setAdditionalMetadata(rm.getAdditionalMetadata());
            } else {
                super.addAdditionalMetadata(fileItem);
            }
        }

        private void setDiscoverRights(ResourceMetadata rm, FileItem fileItem) {
            AccessCategory discoverCat = rm.getCategoryDiscover();
            if (discoverCat != null) {
                fileItem.setVisibleTo(VisibleTo.translate(discoverCat));
            } else {
                super.setDiscoverRights(fileItem);
            }
        }

        private void setReadRights(ResourceMetadata rm, FileItem fileItem) {
            AccessCategory readCat = rm.getCategoryRead();
            if (readCat != null) {
                fileItem.setAccessibleTo(AccessibleTo.translate(readCat));
            } else {
                super.setReadRights(fileItem);
            }
        }

        private ResourceMetadata getResourceMetadata(String path) {
            ResourceMetadata rm = null;
            if (resourceMetadataList != null) {
                rm = resourceMetadataList.getResourceMetadata(path);
            }
            return rm;
        }

        @Override
        public void addAdditionalRDF(FileItem fileItem) {
            if (additionalMetadataFiles != null) {
                for (Entry<MetadataFormat, String> entry : additionalMetadataFiles.entrySet()) {
                    String metadataFilePath = entry.getValue();
                    if (fileItem.getPath().contentEquals(metadataFilePath)) {
                        MetadataFormat metadataFormat = entry.getKey();
                        RL.info(new Event(getClass().getSimpleName(), "Adding additional RDF for additional metadata file: " + fileItem.getStoreId()
                                + " from file: " + metadataFilePath + " with format: " + metadataFormat));
                        final AbstractRelations relations = (AbstractRelations) fileItem.getRelations();
                        final FileOntology fileOntology = new FileOntology();
                        final String datasetURI = RelsConstants.getObjectURI(fileItem.getDatasetId().getStoreId());
                        relations.addRelation(fileOntology.isMetadataOn().get(), datasetURI);
                        relations.addRelation(fileOntology.hasMetadataFormat().get(), metadataFormat.toString(), RelsConstants.RDF_LITERAL);
                    }
                }
            }
        }

    }

    private static class IngestListener extends DefaultWorkListener {

        private final JointMap joint;

        public IngestListener(JointMap joint) {
            this.joint = joint;
        }

        @Override
        public void afterIngest(DataModelObject dmo) {
            if (dmo instanceof DatasetItem) {
                DatasetItem datasetItem = (DatasetItem) dmo;
                RL.info(new Event("Ingested datasetItem", joint.getCurrentDirectory().getPath(), datasetItem.getPath(), datasetItem.getStoreId()));
            }
        }

        @Override
        public void afterUpdate(DataModelObject dmo) {
            if (dmo instanceof DatasetItem) {
                DatasetItem datasetItem = (DatasetItem) dmo;
                RL.info(new Event("Updated datasetItem", joint.getCurrentDirectory().getPath(), datasetItem.getPath(), datasetItem.getStoreId()));
            }
        }

        @Override
        public void onException(Throwable t) {
            RL.error(new Event("While ingesting datasetItem", t, joint.getCurrentDirectory().getPath()));
        }
    }

}
