package nl.knaw.dans.easy.tools.dmo;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JumpoffDmoIteratorTask extends AbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(JumpoffDmoIteratorTask.class);

    private final DmoIterator<JumpoffDmo> iterator;
    private final DmoProcessor<JumpoffDmo> processor;

    public JumpoffDmoIteratorTask(final DmoProcessor<JumpoffDmo> processor) {
        iterator = new DmoIterator<JumpoffDmo>(JumpoffDmo.NAMESPACE);
        this.processor = processor;
    }

    public JumpoffDmoIteratorTask(final DmoProcessor<JumpoffDmo> processor, final DmoFilter<JumpoffDmo> dmoFilter) {
        iterator = new DmoIterator<JumpoffDmo>(JumpoffDmo.NAMESPACE, dmoFilter);
        this.processor = processor;
    }

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        try {
            while (iterator.hasNext()) {
                JumpoffDmo jumpoffDmo = iterator.next();
                logger.info("Now processing " + jumpoffDmo.getStoreId());
                process(jumpoffDmo);
            }
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

    }

    private void process(JumpoffDmo jumpoffDmo) {
        try {
            processor.process(jumpoffDmo);
        }
        catch (TaskExecutionException e) {
            logger.error("Exception while processing " + jumpoffDmo.getStoreId(), e);
            RL.error(new Event(jumpoffDmo.getStoreId(), e));
        }

    }

}
