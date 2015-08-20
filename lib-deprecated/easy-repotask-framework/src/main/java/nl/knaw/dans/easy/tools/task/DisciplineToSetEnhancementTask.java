package nl.knaw.dans.easy.tools.task;

import java.util.List;

import nl.knaw.dans.common.fedora.RelationshipManager;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.Constants;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollectionImpl;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.JointMap;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisciplineToSetEnhancementTask extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(DisciplineToSetEnhancementTask.class);

    @Override
    public void run(JointMap taskMap) {
        // the resource index may be slow...
        convertDisciplines();
    }

    private void convertDisciplines() {
        try {
            int count = 0;
            int subdisciplines = 0;
            DisciplineContainer root = null;
            RelationshipManager relMan = Application.getFedora().getRelationshipManager();
            while (count < 10 && subdisciplines == 0) {
                count++;
                root = (DisciplineContainer) Data.getEasyStore().retrieve(DisciplineCollectionImpl.EASY_DISCIPLINE_ROOT_DMO_STORE_ID);

                logger.info("Found root of disciplines. storeId=" + root.getStoreId());
                subdisciplines = root.getSubDisciplines().size();
                if (subdisciplines == 0) {
                    logger.info("Tripple store slow. Found " + subdisciplines + " subdisciplines of root.");
                    try {
                        Thread.sleep(10000);
                    }
                    catch (InterruptedException e) {
                        //
                    }
                }
            }
            if (subdisciplines == 0) {
                String msg = "After trying " + count + " times, found no discipline hierarchie in tripple store.";
                logger.error(msg);
                RL.error(new Event("Found no disciplines", msg));
                return;
            }

            convertDisciplines(root.getSubDisciplines(), null, relMan);
        }
        catch (DomainException e) {
            String msg = "Could not enhance easy-disciplines: ";
            logger.error(msg, e);
            RL.error(new Event("Convert disciplines", e, msg));
            return;
        }
        catch (RepositoryException e) {
            String msg = "Could not enhance easy-disciplines: ";
            logger.error(msg, e);
            RL.error(new Event("Convert disciplines", e, msg));
            return;
        }
        catch (DocumentException e) {
            String msg = "Could not enhance easy-disciplines: ";
            logger.error(msg, e);
            RL.error(new Event("Convert disciplines", e, msg));
            return;
        }
    }

    private void convertDisciplines(List<DisciplineContainer> subDisciplines, String setHierarchy, RelationshipManager relMan) throws RepositoryException,
            DocumentException, DomainException
    {
        for (DisciplineContainer dc : subDisciplines) {
            String setSpec;
            if (setHierarchy == null) {
                setSpec = dc.getDisciplineMetadata().getOICode();
            } else {
                setSpec = setHierarchy + ":" + dc.getDisciplineMetadata().getOICode();
            }
            String setName = dc.getName();
            String storeId = dc.getStoreId();

            String object = RelsConstants.getObjectURI(Constants.CM_OAI_SET_1);
            relMan.addRelationship(storeId, RelsConstants.FM_HAS_MODEL, object, false, null);
            relMan.addRelationship(storeId, RelsConstants.OAI_SET_SPEC, setSpec, true, null);
            relMan.addRelationship(storeId, RelsConstants.OAI_SET_NAME, setName, true, null);

            logger.debug("Added relations. storeId=" + storeId + " setSpec=" + setSpec + " setName=" + setName);

            convertDisciplines(dc.getSubDisciplines(), setSpec, relMan);

        }

    }

}
