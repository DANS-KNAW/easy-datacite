package nl.knaw.dans.common.jibx.bean;

import nl.knaw.dans.common.lang.repo.bean.BeanFactoryDelegator;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata;

public class JiBXBeanFactoryDelegator implements BeanFactoryDelegator {

    @Override
    public DublinCoreMetadata newDublinCoreMetadata() {
        return new JiBXDublinCoreMetadata();
    }

    @Override
    public JumpoffDmoMetadata newJumpoffDmoMetadata() {
        return new JiBXJumpoffDmoMetadata();
    }

}
