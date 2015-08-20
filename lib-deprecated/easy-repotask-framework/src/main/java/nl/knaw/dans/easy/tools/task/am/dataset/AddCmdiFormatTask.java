package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.pf.language.emd.EmdFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

public class AddCmdiFormatTask extends AbstractTask {

    private List<String> pids;
    private String mediaType;

    @Override
    public void run(JointMap joint) throws TaskException, FatalTaskException {
        Dataset dataset = joint.getDataset();
        String pid = dataset.getPersistentIdentifier();
        if (pids.contains(pid)) {
            try {
                UnitOfWork uow = new EasyUnitOfWork(dataset.getDepositor());
                uow.attach(dataset);
                EmdFormat format = dataset.getEasyMetadata().getEmdFormat();
                List<BasicString> formatList = format.getDcFormat();
                if (!hasFormat(formatList)) {
                    formatList.add(new BasicString(mediaType));
                    format.setDcFormat(formatList);
                    uow.commit();
                    RL.info(new Event(getTaskName(), "Added format to dataset: " + pid, dataset.getStoreId()));
                } else {
                    RL.info(new Event(getTaskName(), "Dataset " + pid + " already has '" + mediaType + "' in format list.", dataset.getStoreId()));
                }
            }
            catch (RepositoryException e) {
                throw new FatalTaskException(e, this);
            }
            catch (UnitOfWorkInterruptException e) {
                throw new TaskException(e, this);
            }
        }
    }

    private boolean hasFormat(List<BasicString> formatList) {
        for (BasicString bs : formatList) {
            if (bs.getValue().equals(mediaType)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getPids() {
        return pids;
    }

    public void setPids(List<String> pids) {
        this.pids = pids;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

}
