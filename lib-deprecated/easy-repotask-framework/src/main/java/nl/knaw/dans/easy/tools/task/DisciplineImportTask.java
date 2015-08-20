package nl.knaw.dans.easy.tools.task;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.collections.DmoContainerItem;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollectionImpl;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadata;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadataImpl;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisciplineImportTask extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(DisciplineImportTask.class);

    private boolean purgingBeforeIngest;
    private String categoriesFilename;
    private int seqNo;

    public boolean isPurgingBeforeIngest() {
        return purgingBeforeIngest;
    }

    public void setPurgeBeforeIngest(boolean purgingBeforeIngest) {
        this.purgingBeforeIngest = purgingBeforeIngest;
    }

    public String getCategoriesFilename() {
        return categoriesFilename;
    }

    public void setCategoriesFilename(String categoriesFilename) {
        this.categoriesFilename = categoriesFilename;
    }

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        File categoriesFile = new File(categoriesFilename);

        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(categoriesFile);
            doc.getRootElement();

            List<?> publicNodes = doc.selectNodes("//treetype[label='public']");
            if (publicNodes.size() > 1 || publicNodes.size() <= 0) {
                throw new FatalTaskException("Could not find the one public node in categories xml", this);
            }

            Element publicNode = (Element) publicNodes.get(0);

            // create discipline objects
            DisciplineContainerImpl rootDiscipline = new DisciplineContainerImpl(DisciplineCollectionImpl.EASY_DISCIPLINE_ROOT);
            addDisciplineChildren(rootDiscipline, publicNode);

            if (rootDiscipline.getChildren().size() == 0) {
                throw new FatalTaskException("Root node has no children?", this);
            }

            // ingest into Fedora
            ingestDiscipline(rootDiscipline);
        }
        catch (DocumentException e) {
            throw new FatalTaskException(e, this);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

    }

    private void addDisciplineChildren(DisciplineContainerImpl discipline, Element node) throws RepositoryException {
        Element branches = node.element("branches");
        if (branches == null)
            return;

        Iterator<?> branchIt = branches.elementIterator();
        while (branchIt.hasNext()) {
            Object branch = branchIt.next();
            if (branch instanceof Element && ((Element) branch).getName().equals("treetype")) {
                DisciplineContainerImpl child = convertTreetypeToDiscipline((Element) branch);
                addDisciplineChildren(child, (Element) branch);
                discipline.addChild(child);
            }
        }
    }

    private DisciplineContainerImpl convertTreetypeToDiscipline(Element branch) {
        seqNo++;
        DisciplineContainerImpl result = new DisciplineContainerImpl(DisciplineContainerImpl.NAMESPACE + ":" + seqNo);
        result.setLabel(branch.elementText("label"));

        DisciplineMetadata dmd = new DisciplineMetadataImpl();
        dmd.setOrder(seqNo);
        dmd.setOICode(branch.elementText("code"));
        dmd.setEasy1BranchID(branch.elementText("branchId"));

        result.setDisciplineMetadata(dmd);

        logger.info("Created discipline " + result.toString());

        return result;
    }

    private void ingestDiscipline(DisciplineContainerImpl discipline) throws RepositoryException {
        if (purgingBeforeIngest) {
            purge(discipline);
        }

        String msg = "Ingesting discipline " + discipline.getLabel() + " with sid " + discipline.getStoreId();
        logger.info(msg);
        Data.getEasyStore().ingest(discipline, msg);

        for (DmoContainerItem child : discipline.getChildren()) {
            ingestDiscipline((DisciplineContainerImpl) child);
        }
    }

    private void purge(DisciplineContainerImpl discipline) throws RepositoryException {
        if (objectExists(discipline.getStoreId())) {
            logger.info("Purging old discipline " + discipline.getStoreId());
            discipline.registerDeleted();
            Data.getEasyStore().purge(discipline, false, "purged old discipline");
        } else {
            logger.info("No object in store with storeId " + discipline.getStoreId());
        }
    }

    private boolean objectExists(String storeId) throws RepositoryException {
        try {
            Data.getEasyStore().retrieve(new DmoStoreId(storeId));
        }
        catch (ObjectNotInStoreException e) {
            return false;
        }
        return true;
    }
}
