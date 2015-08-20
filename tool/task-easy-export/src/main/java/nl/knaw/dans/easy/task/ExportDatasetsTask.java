package nl.knaw.dans.easy.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import nl.knaw.dans.easy.db.DbUtil;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

public class ExportDatasetsTask extends AbstractTask {
    static final Logger logger = LoggerFactory.getLogger(ExportDatasetsTask.class);

    /**
     * Other tasks might also open and close a session. We must follow the same pattern to avoid getting a closed session. So don't ask for a session at
     * construction time.
     */
    private Session session;

    private File exportFolder;

    private String fedoraBaseUrl;

    public ExportDatasetsTask(File exportFolder, String fedoraBaseUrl) throws FileNotFoundException, ClassNotFoundException {
        if (exportFolder.exists())
            throw new IllegalArgumentException(exportFolder + " allready exists");
        this.exportFolder = exportFolder;
        this.fedoraBaseUrl = fedoraBaseUrl.endsWith("/") ? fedoraBaseUrl.substring(0, fedoraBaseUrl.length() - 1) : fedoraBaseUrl;
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        String datasetStoreId = joint.getDmoStoreId().toString();
        String datasetFolder = exportFolder + "/" + datasetStoreId + "/";
        if (new File(datasetFolder).exists()) {
            String msg = datasetStoreId + " repeated, export skipped";
            logger.warn(msg);
            throw new TaskCycleException(msg, this);
        }
        for (Object object : createQuery(datasetStoreId).list()) {
            FileItemVO fileItemVO = (FileItemVO) object;
            File file = new File(datasetFolder + fileItemVO.getPath());
            exportFile(fileItemVO.getSid(), file);
        }
    }

    private void exportFile(String storeId, File file) throws FatalTaskException {
        logger.info("exporting {} : {}", storeId, file.getPath());
        file.getParentFile().mkdirs();
        try {
            InputStream is = null;
            FileOutputStream os = null;
            try {
                is = getFileContentUrl(storeId).openConnection().getInputStream();
                os = new FileOutputStream(file);
                StreamUtils.copy(is, os);
            }
            finally {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            }
        }
        catch (IOException e) {
            throw new FatalTaskException(String.format("Error exporting file: %s %s", e, storeId, file), this);
        }
    }

    private URL getFileContentUrl(String fileStoreId) throws FatalTaskException {
        try {
            return new URL(String.format("%s/objects/%s/datastreams/EASY_FILE/content", fedoraBaseUrl, fileStoreId));
        }
        catch (MalformedURLException e) {
            throw new FatalTaskException("URL for file content was invalid", this);
        }
    }

    @Override
    /** Frees resources. Will be called even if run threw an exception. */
    public void close() throws TaskException, TaskCycleException, FatalTaskException {
        // close the session so other tasks get a fresh one
        if (session != null && session.isOpen())
            session.close();
    }

    private Criteria createQuery(String datasetStoreId) {
        session = DbUtil.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            Criteria descendants = session.createCriteria(FileItemVO.class);
            descendants.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            descendants.add(Restrictions.eq("datasetSid", datasetStoreId));
            return descendants;
        }
        finally {
            DbUtil.getSessionFactory().getCurrentSession().close();
        }
    }
}
