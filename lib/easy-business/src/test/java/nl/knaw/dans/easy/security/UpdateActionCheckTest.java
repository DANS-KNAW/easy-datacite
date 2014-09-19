package nl.knaw.dans.easy.security;

import static org.junit.Assert.assertFalse;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo.Action;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.SecurityOfficer;
import nl.knaw.dans.easy.security.UpdateActionCheck;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateActionCheckTest {

    private static final Logger logger = LoggerFactory.getLogger(UpdateActionCheckTest.class);

    @Test
    public void explain() {
        SecurityOfficer officer = new UpdateActionCheck(Action.DELETE, Action.RENAME);
        UpdateInfo info = new UpdateInfo();
        info.updateAccessibleTo(AccessibleTo.KNOWN);
        info.registerDeleted(true);
        ContextParameters ctxParameters = new ContextParameters(info);
        if (Tester.isVerbose())
            logger.debug(officer.explainComponentVisible(ctxParameters));
        assertFalse(officer.isEnableAllowed(ctxParameters));
    }

}
