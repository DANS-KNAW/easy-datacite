package nl.knaw.dans.easy.tools.imex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.os.OS;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.tools.dmo.DmoProcessor;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;

/**
 * Writes the objectXml of the processed Dmo to file.
 */
public class DmoExporter implements DmoProcessor<DataModelObject> {

    private final File exportDir;
    private final boolean allReadWrite;

    public DmoExporter(String exportDirName) throws IOException {
        this(new File(exportDirName), false);
    }

    public DmoExporter(String exportDirName, boolean allReadWrite) throws IOException {
        this(new File(exportDirName), allReadWrite);
    }

    public DmoExporter(File exportDir, boolean allReadWrite) throws IOException {
        this.exportDir = exportDir;
        this.allReadWrite = allReadWrite;
        exportDir.mkdirs();
        if (allReadWrite) {
            OS.setAllRWX(exportDir);
        }
    }

    @Override
    public void process(DataModelObject dmo) throws TaskExecutionException {
        try {
            byte[] objectXml = Data.getEasyStore().getObjectXML(dmo.getDmoStoreId());
            writeFile(objectXml, dmo.getStoreId());
        }
        catch (ObjectNotInStoreException e) {
            throw new TaskExecutionException(e);
        }
        catch (RepositoryException e) {
            throw new TaskExecutionException(e);
        }
        catch (IOException e) {
            throw new TaskExecutionException(e);
        }
    }

    @Override
    public boolean hasChangedDmo() {
        return false;
    }

    private void writeFile(byte[] objectXml, String storeId) throws IOException {
        File file = new File(exportDir, storeId + ".xml");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(objectXml);
        }
        finally {
            if (fos != null) {
                fos.close();
            }
        }

        if (allReadWrite) {
            OS.setAllRWX(file);
        }
    }

}
