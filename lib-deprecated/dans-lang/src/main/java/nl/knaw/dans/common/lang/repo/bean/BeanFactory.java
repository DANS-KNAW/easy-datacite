package nl.knaw.dans.common.lang.repo.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private static BeanFactoryDelegator delegator;

    public BeanFactory(BeanFactoryDelegator delegator) {
        logger.debug("Created " + this + " with " + delegator);
        BeanFactory.delegator = delegator;
    }

    public static DublinCoreMetadata newDublinCoreMetadata() {
        return delegator.newDublinCoreMetadata();
    }

    public static JumpoffDmoMetadata newJumpoffDmoMetadata() {
        return delegator.newJumpoffDmoMetadata();
    }

}
