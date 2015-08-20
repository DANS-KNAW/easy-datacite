package nl.knaw.dans.easy.tools.dmo;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisciplineIteratorTask extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(DisciplineIteratorTask.class);

    private final DmoIterator<DisciplineContainer> iterator;
    private final DmoProcessor<DataModelObject> processor;

    public DisciplineIteratorTask(final DmoProcessor<DataModelObject> processor) {
        iterator = new DmoIterator<DisciplineContainer>(DisciplineContainer.NAMESPACE);
        this.processor = processor;
    }

    public DisciplineIteratorTask(final DmoProcessor<DataModelObject> processor, final DmoFilter<DisciplineContainer> dmoFilter) {
        iterator = new DmoIterator<DisciplineContainer>(DisciplineContainer.NAMESPACE, dmoFilter);
        this.processor = processor;
    }

    @Override
    public void run(JointMap joint) throws FatalTaskException {
        try {
            while (iterator.hasNext()) {
                DisciplineContainer disciplineContainer = iterator.next();
                logger.info("Now processing " + disciplineContainer.getStoreId());
                RL.info(new Event("start", disciplineContainer.getStoreId()));
                process(disciplineContainer);
            }
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

    }

    private void process(DataModelObject dmo) {
        try {
            processor.process(dmo);
        }
        catch (TaskExecutionException e) {
            logger.error("Exception while processing " + dmo.getStoreId(), e);

            RL.error(new Event(dmo.getStoreId(), e));
        }

    }

}
