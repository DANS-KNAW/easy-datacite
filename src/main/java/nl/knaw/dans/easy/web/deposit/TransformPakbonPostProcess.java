package nl.knaw.dans.easy.web.deposit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.UploadStatus;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataUpdateStrategy;
import nl.knaw.dans.easy.business.md.amd.ReplaceAdditionalMetadataStrategy;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.WorkReporter;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.statistics.UploadFileStatistics;
import nl.knaw.dans.platform.language.pakbon.Pakbon2EmdTransformer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformPakbonPostProcess implements IUploadPostProcess
{
    private static final Logger LOG = LoggerFactory.getLogger(TransformPakbonPostProcess.class);

    private boolean canceled = false;

    private final UploadStatus status = new UploadStatus("Initializing pakbon transform process");

    private DatasetModel datasetModel;

    public void cancel() throws UploadPostProcessException
    {
        canceled = true;
    }

    public List<File> execute(final List<File> fileList, final File destPath, final Map<String, String> clientParams) throws UploadPostProcessException
    {  
    	try {
			String filePath = fileList.get(0).getPath();
			FileInputStream inputStream = new FileInputStream(filePath);
			byte [] result = new Pakbon2EmdTransformer().transform(inputStream);
	        getDataset().replaceEasyMetadata(new String(result));
			return fileList;
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DomainException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
   }

    public void rollBack() throws UploadPostProcessException
    {
        LOG.error("Programming error: processing code should be in business services and/or domain objects.");
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

}
