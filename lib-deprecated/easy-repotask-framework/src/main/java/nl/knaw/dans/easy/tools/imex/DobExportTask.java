package nl.knaw.dans.easy.tools.imex;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.fedora.fox.AuditTrail;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.fox.DigitalObjectProperties;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.dmo.PidIterator;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DobExportTask extends AbstractTask {

    public static final String DEFAULT_EXPORT_FOLDER = "dob/export";
    public static final String OBJECT_NOT_FOUND = "object_not_found";

    private static final Logger logger = LoggerFactory.getLogger(DobExportTask.class);

    private final File exportDir;
    private final Set<DmoNamespace> dmoNamespaceSet = new LinkedHashSet<DmoNamespace>();

    private boolean cleaningDobs = true;
    private int exportCount;

    public DobExportTask() {
        this(DEFAULT_EXPORT_FOLDER);
    }

    public DobExportTask(String exportFolder) {
        this.exportDir = new File(exportFolder);
        exportDir.mkdirs();
    }

    public void setNamespaceList(List<String> namespaceList) {
        dmoNamespaceSet.clear();
        for (String namespace : namespaceList) {
            dmoNamespaceSet.add(new DmoNamespace(namespace));
        }
    }

    public void addNamespace(DmoNamespace namespace) {
        dmoNamespaceSet.add(namespace);
    }

    public boolean isCleaningDobs() {
        return cleaningDobs;
    }

    public void setCleaningDobs(boolean cleaningDobs) {
        this.cleaningDobs = cleaningDobs;
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        exportCount = 0;
        for (DmoNamespace dmoNamespace : dmoNamespaceSet) {
            try {
                exportObjextXml(dmoNamespace);
            }
            catch (ObjectNotInStoreException e) {
                RL.info(new Event(OBJECT_NOT_FOUND, e, dmoNamespace.toString()));
            }
            catch (RepositoryException e) {
                throw new FatalTaskException(e, this);
            }
            catch (IOException e) {
                throw new FatalTaskException(e, this);
            }
            catch (XMLSerializationException e) {
                throw new FatalTaskException(e, this);
            }
        }
        logger.info("Exported " + exportCount + " digital objects");
    }

    private void exportObjextXml(DmoNamespace dmoNamespace) throws ObjectNotInStoreException, RepositoryException, IOException, XMLSerializationException {
        PidIterator piter = new PidIterator(dmoNamespace);
        while (piter.hasNext()) {
            byte[] objectXml;
            String storeId = piter.next();
            if (isCleaningDobs()) {
                objectXml = getCleanObjectXml(storeId);
            } else {
                objectXml = Data.getEasyStore().getObjectXML(new DmoStoreId(storeId));
            }
            writeFile(storeId, objectXml);
        }

    }

    private byte[] getCleanObjectXml(String storeId) throws ObjectNotInStoreException, RepositoryException, XMLSerializationException {
        byte[] objectXml;
        DigitalObject dob = Application.getFedora().getObjectManager().getDigitalObject(storeId);
        DigitalObjectProperties dobProps = dob.getObjectProperties();
        dobProps.setProperty(DigitalObjectProperties.NAME_CREATED_DATE, null);
        dobProps.setProperty(DigitalObjectProperties.NAME_LASTMODIFIED_DATE, null);
        dob.removeDatastream(AuditTrail.STREAM_ID);
        objectXml = dob.asObjectXML(4);
        return objectXml;
    }

    private void writeFile(String storeId, byte[] objectXml) throws IOException {
        File xmlFile = new File(exportDir, storeId + ".xml");
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(xmlFile));
            out.write(objectXml);
            exportCount++;
            logger.debug(exportCount + " Exported " + xmlFile.getAbsolutePath());
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
