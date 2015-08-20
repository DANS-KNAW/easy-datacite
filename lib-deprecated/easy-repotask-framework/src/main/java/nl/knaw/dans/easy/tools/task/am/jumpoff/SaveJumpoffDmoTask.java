package nl.knaw.dans.easy.tools.task.am.jumpoff;

import org.dom4j.Document;
import org.joda.time.DateTime;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.bean.MarkupMetadata;
import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata.MarkupVersionID;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmoRelations;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

public class SaveJumpoffDmoTask extends AbstractTask {

    private int saveCounter;
    private EasyUser user;
    private String datasetId;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        if (joint.isCycleSubjectDirty() && joint.isFitForSave()) {
            saveCounter++;
            saveJumpoff(joint);
        }
    }

    private void saveJumpoff(JointMap joint) throws FatalTaskException {
        JumpoffDmo jumpoff = joint.getJumpoffDmo();

        JumpoffDmoRelations relations = (JumpoffDmoRelations) jumpoff.getRelations();
        String datasetId = relations.getObjectId();

        if (MarkupVersionID.TEXT_MU.equals(jumpoff.getJumpoffDmoMetadata().getDefaultMarkupVersionID())) {
            saveTextMarkup(joint);
            RL.info(new Event(getTaskName(), "Saved text", jumpoff.getStoreId(), datasetId));
        } else {
            saveHtmlMarkup(joint);

        }
    }

    private void saveTextMarkup(JointMap joint) throws FatalTaskException {
        JumpoffDmo jumpoff = joint.getJumpoffDmo();

        save(jumpoff);
    }

    private void saveHtmlMarkup(JointMap joint) throws FatalTaskException {
        JumpoffDmo jumpoff = joint.getJumpoffDmo();
        Document markupDoc = joint.getJumpoffDom4jDocument();
        if (markupDoc == null) {
            RL.error(new Event(getTaskName(), "No markup", jumpoff.getStoreId(), datasetId));
            return;
        }

        jumpoff.getHtmlMarkup().setHtml(markupDoc.getRootElement().asXML());

        save(jumpoff);
        RL.info(new Event(getTaskName(), "Saved html", jumpoff.getStoreId(), datasetId));
    }

    protected void save(JumpoffDmo jumpoff) throws FatalTaskException {
        EasyUnitOfWork uow = new EasyUnitOfWork(getUser());
        try {
            uow.attach(jumpoff);
            uow.commit();
            RL.info(new Event(getTaskName(), "Saved " + saveCounter, jumpoff.getStoreId()));
        }
        catch (RepositoryException e) {
            RL.error(new Event(getTaskName(), e, "Could not save", jumpoff.getStoreId()));
            throw new FatalTaskException(jumpoff.getStoreId(), e, this);
        }
        catch (UnitOfWorkInterruptException e) {
            RL.error(new Event(getTaskName(), e, "Could not save", jumpoff.getStoreId()));
            throw new FatalTaskException(jumpoff.getStoreId(), e, this);
        }
    }

    private EasyUser getUser() {
        if (user == null) {
            user = new EasyUserImpl() {

                private static final long serialVersionUID = -66924895541046244L;

                @Override
                public String getId() {
                    return "tools-admin";
                }

                @Override
                public String getDisplayName() {
                    return "Admin of Tools";
                }

            };
        }
        return user;
    }

}
