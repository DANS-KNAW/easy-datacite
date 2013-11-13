package nl.knaw.dans.easy.web.view.dataset;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.wicket.components.upload.UploadStatus;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.easy.domain.model.Dataset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UploadSingleFilePostProcess implements IUploadPostProcess
{
    protected static final Logger logger = LoggerFactory.getLogger(UploadSingleFilePostProcess.class);

    private final UploadStatus status = new UploadStatus("Initializing upload process");

    private Dataset dataset = null;

    @Override
    public void cancel() throws UploadPostProcessException
    {
        throw new UploadPostProcessException(new UnsupportedOperationException(this.getClass().getName() + ".rollBack"));
    }

    @Override
    public List<File> execute(final List<File> files, final File destPath, final Map<String, String> clientParams) throws UploadPostProcessException
    {
        if (files == null || files.size() != 1)
        {
            throw new UploadPostProcessException(buildMessage(files));
        }
        final File file = files.get(0);
        final String fileName = file.toString();
        status.setMessage(fileName);
        processUploadedFile(file, dataset);
        return files;
    }

    abstract void processUploadedFile(File file, Dataset dataset) throws UploadPostProcessException;

    @Override
    public UploadStatus getStatus()
    {
        return status;
    }

    boolean needsProcessing(final List<File> files, final String extension)
    {
        if (files == null || files.size() == 1)
        {
            final String fileName = files.get(0).toString();
            if (fileName.toLowerCase().endsWith(extension))
            {
                status.setMessage("detected " + fileName);
                return true;
            }
        }
        logger.error(buildMessage(files));
        return false;
    }

    String buildMessage(final List<File> files)
    {
        return "too few or too many files to upload: " + Arrays.deepToString(files.toArray());
    }

    public void setDataset(final Dataset dataset)
    {
        this.dataset = dataset;
    }

}
