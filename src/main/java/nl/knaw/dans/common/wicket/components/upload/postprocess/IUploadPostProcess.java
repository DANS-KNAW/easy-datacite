package nl.knaw.dans.common.wicket.components.upload.postprocess;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.wicket.components.upload.UploadStatus;

/**
 * Implementors of this interface may do some post processing on one or more uploaded files. An
 * implementation must be registered at the EasyUploadProcesess singleton. Chaining of files lists
 * occurs, thus each process may create more files that may then be used by the next process. No
 * multi-threading is used for the execution process. Another thread must be responsible for periodically
 * polling the status object.
 * 
 * @author lobo
 */
public interface IUploadPostProcess extends Serializable
{
    boolean needsProcessing(List<File> files);

    /**
     * Implements the execution of a postprocessor. A postprocessor may alter uploaded files, produce new
     * ones, filter out files or delete files simply by getting a list of files as input and returning a
     * list of files that needs to be considered uploaded.
     * 
     * @param files
     *        the list with files that are to be considered uploaded
     * @param destPath
     *        the original path in which the files were uploaded
     * @param clientParams
     *        parameters received from the client side (javascript)
     * @return a list with files that need to be considered as uploaded.
     * @throws UploadPostProcessException
     */
    List<File> execute(List<File> files, File destPath, Map<String, String> clientParams) throws UploadPostProcessException;

    UploadStatus getStatus();

    void cancel() throws UploadPostProcessException;

    void rollBack() throws UploadPostProcessException;
}
