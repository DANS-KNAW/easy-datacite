package nl.knaw.dans.easy.web.deposit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerException;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.UploadStatus;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.TransformPakbonPostProcessException;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.worker.WorkReporter;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.statistics.DatasetStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.statistics.UploadFileStatistics;
import nl.knaw.dans.pf.language.xml.exc.ValidatorException;
import nl.knaw.dans.platform.language.pakbon.Pakbon2EmdTransformer;
import nl.knaw.dans.platform.language.pakbon.PakbonValidator;
import nl.knaw.dans.platform.language.pakbon.PakbonValidatorCredentials;

import org.apache.commons.lang.StringUtils;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformPakbonPostProcess implements IUploadPostProcess
{
    private static final Logger LOG = LoggerFactory.getLogger(TransformPakbonPostProcess.class);

    private boolean canceled = false;
    private final UploadStatus status = new UploadStatus("Initializing pakbon transform process");

    private DatasetModel datasetModel;
    private String parentSid = "";
    
    public List<File> execute(final List<File> fileList, final File destPath, final Map<String, String> clientParams) throws UploadPostProcessException
    {  
    	try {
			String filePath = fileList.get(0).getPath();
			setStatus("Validating the xml-file...");
			if (isValidPakbon(new File(filePath))){
				setStatus("Transforming into Easy metadata...");
				byte [] result = new Pakbon2EmdTransformer().transform(new FileInputStream(filePath));

				setStatus("Writing Easy metadata...");
		        getDataset().replaceEasyMetadata(new String(result));
		        
				setStatus("Ingesting the xml-file...");
				ingestFile(fileList, destPath,clientParams);
				return fileList;
			}
			else {
				throwError(new TransformPakbonPostProcessException("Pakbon file is not valid"), "Error in validating pakbon xml-file");
			}
		} catch (TransformerException e) {
			throwError(e, "Error in transforming into Easy metadata");
		} catch (FileNotFoundException e) {
			throwError(e, "Error in transforming into Easy metadata");
		} catch (DomainException e) {
			throwError(e, "Error in writing Easy metadata");
		} catch (ServiceException e) {
			throwError(e, "Error in ingesting the xml-file");
		} catch (ValidatorException e) {
			throwError(e, "Error in validating pakbon xml-file");
		} catch (SOAPException e) {
			throwError(e, "Error in validating pakbon xml-file");
		} catch (IOException e) {
			throwError(e, "Error in validating pakbon xml-file");
		}
    	return null;
   }

    private void throwError(Exception error, String message) throws UploadPostProcessException {
    	
		setStatus(message);
    	cancel();
    	status.setError(true);
		LOG.error(error.getMessage());
    	throw new UploadPostProcessException(error);
    }
    
    
    public void cancel()
    {
        canceled = true;
    }

   public void rollBack() throws UploadPostProcessException
    {
        LOG.error("Programming error: processing code should be in business services and/or domain objects.");
    }

    
  private void ingestFile(final List<File> fileList, final File destPath, final Map<String, String> clientParams) throws ServiceException {
    	
        Dataset dataset = getDataset();

        if (parentSid.equals(""))
            parentSid = clientParams.get("parentSid");

        final double totalSize = fileList.size();

        try
        {
            DmoStoreId parentDmoStoreId = parentSid == null ? null : new DmoStoreId(parentSid);
            Services.getItemService().addDirectoryContents(EasySession.get().getUser(), dataset, parentDmoStoreId, destPath, fileList, new WorkReporter()
            {
                private double actionCount = 0;

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
        finally
        {
            // logging for statistics
            StatisticsLogger.getInstance().logEvent(StatisticsEvent.FILE_DEPOSIT, new DatasetStatistics(dataset), new UploadFileStatistics(fileList));
        }
    }
    
    private boolean isValidPakbon(File xml) throws ValidatorException, SOAPException, IOException
    {
        final String DEFAULT_USERNAME = "bergh";
        final String DEFAULT_PASSWORD = "cC!XzlKK";

//        PakbonValidator validator = new PakbonValidator();
//        ValidateXmlResponse response;
//        new PakbonValidatorCredentials(DEFAULT_USERNAME, DEFAULT_PASSWORD);
//		response = validator.validateXml(xml);
//		if (!response.getValidation().getValidXml()){
//			System.out.println("Validation of the pakbon xml-file failed.");
//		}
//        return response.getValidation().getValidXml();
        return true;
    }
    
    public void setStatus(String statusMessage)
    {
        status.setMessage(statusMessage);
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

    private Dataset getDataset()
    {
        return datasetModel.getObject();
    }
}
