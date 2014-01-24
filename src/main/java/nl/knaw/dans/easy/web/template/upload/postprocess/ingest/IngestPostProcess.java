package nl.knaw.dans.easy.web.template.upload.postprocess.ingest;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.UploadStatus;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.easy.business.item.ItemIngester;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.worker.WorkReporter;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.statistics.UploadFileStatistics;

import org.apache.commons.lang.StringUtils;

public class IngestPostProcess implements IUploadPostProcess
{
    private static final long serialVersionUID = 1L;

    private boolean canceled = false;

    private UploadStatus status = new UploadStatus("Initializing ingest process");

    private DatasetModel datasetModel;

    private String parentSid = "";

    public void cancel() throws UploadPostProcessException
    {
        canceled = true;
    }

    private String dump(Object[] files)
    {
        String s = Arrays.deepToString(files);
        return s + Arrays.toString(s.toCharArray()) + Arrays.toString(s.getBytes());
    }

    public List<File> execute(final List<File> fileList, final File destPath, final Map<String, String> clientParams) throws UploadPostProcessException
    {

        if (fileList.size() == 0)
            throw new UploadPostProcessException("Nothing to ingest.");
        if (destPath.listFiles(new ItemIngester.ListFilter(fileList)).length == 0)
        {
            StringBuffer message = new StringBuffer();
            message.append("Filter skips " + Arrays.toString(ItemIngester.SKIPPED_FILENAMES) + " and leaves nothing to ingest.");
            message.append("\nfiles:  " + dump(destPath.listFiles()));
            message.append("\nfilter: " + dump(fileList.toArray()));
            throw new UploadPostProcessException(message.toString());
        }
        Dataset dataset = getDataset();
        if ("".equals(parentSid) || parentSid == null)
            parentSid = clientParams.get("parentSid");
        final double totalSize = fileList.size();
        try
        {
            DmoStoreId parentDmoStoreId = parentSid == null ? null : new DmoStoreId(parentSid);
            Services.getItemService().addDirectoryContents(EasySession.get().getUser(), dataset, parentDmoStoreId, destPath, fileList, new WorkReporter()
            {
                private double actionCount;

                @Override
                public boolean onIngest(DataModelObject dmo)
                {
                    super.onIngest(dmo);
                    updateStatus(dmo.getLabel());
                    return canceled;
                }

                @Override
                public boolean onUpdate(DataModelObject dmo)
                {
                    super.onUpdate(dmo);
                    updateStatus(dmo.getLabel());
                    return canceled;
                }

                public boolean onWorkStart()
                {
                    super.onWorkStart();
                    setStatus(0, "preparing ingest...");
                    return canceled;
                }

                private void updateStatus(String name)
                {
                    String nameToDisplay = StringUtils.abbreviate(name, 20);
                    actionCount++;
                    double percentage = actionCount / totalSize;
                    setStatus((int) (percentage * 100D), nameToDisplay);
                }
            });

        }
        catch (ServiceException e)
        {
            throw new UploadPostProcessException(e);
        }
        finally
        {
            // logging for statistics
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.FILE_DEPOSIT, new DatasetStatistics(dataset), new UploadFileStatistics(fileList));
        }
        return fileList;
    }

    public void setStatus(int percent, String filename)
    {
        if (percent < 0)
            percent = 0;
        if (percent > 100)
            percent = 100;
        status.setMessage("Ingesting: " + percent + "% ");
        status.setPercentComplete(percent);
    }

    public UploadStatus getStatus()
    {
        return status;
    }

    public boolean needsProcessing(List<File> files)
    {
        return true;
    }

    public void setModel(DatasetModel datasetModel)
    {
        this.datasetModel = datasetModel;
    }

    private Dataset getDataset() throws UploadPostProcessException
    {
        return datasetModel.getObject();
    }

    public String getParentSid()
    {
        return parentSid;
    }

    public void setParentSid(String parentSid)
    {
        this.parentSid = parentSid;
    }

}
