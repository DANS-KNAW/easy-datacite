package nl.knaw.dans.easy.security;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.And;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.DatasetStateCheck;
import nl.knaw.dans.easy.security.HasRoleCheck;
import nl.knaw.dans.easy.security.IsDepositorOfDatasetCheck;
import nl.knaw.dans.easy.security.IsSelfCheck;
import nl.knaw.dans.easy.security.Not;
import nl.knaw.dans.easy.security.Or;
import nl.knaw.dans.easy.security.SecurityOfficer;
import nl.knaw.dans.easy.security.SplitAnswer;
import nl.knaw.dans.easy.security.WorkflowCheck;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplainTest
{

    private static final Logger logger = LoggerFactory.getLogger(ExplainTest.class);

    @Test
    public void explain()
    {
        Object[] args = null;
        ContextParameters ctxParameters = new ContextParameters(args);

        SecurityOfficer datasetStateCheck = new DatasetStateCheck(DatasetState.DRAFT, DatasetState.MAINTENANCE);
        // logger.debug(datasetStateCheck.explainAllowed(ctxParameters));
        // logger.debug(datasetStateCheck.explainVisible(ctxParameters));

        SecurityOfficer hasRoleCheck = new HasRoleCheck(Role.ADMIN, Role.ARCHIVIST);
        // logger.debug(hasRoleCheck.explainAllowed(ctxParameters));
        // logger.debug(hasRoleCheck.explainVisible(ctxParameters));

        SecurityOfficer isDepositorCheck = new IsDepositorOfDatasetCheck();
        // logger.debug(isDepositorCheck.explainAllowed(ctxParameters));
        // logger.debug(isDepositorCheck.explainVisible(ctxParameters));

        SecurityOfficer isSelfCheck = new IsSelfCheck();
        // logger.debug(isSelfCheck.explainAllowed(ctxParameters));
        // logger.debug(isSelfCheck.explainVisible(ctxParameters));

        SecurityOfficer workFlowCheck = new WorkflowCheck();
        // logger.debug(workFlowCheck.explainAllowed(ctxParameters));
        // logger.debug(workFlowCheck.explainVisible(ctxParameters));

        SecurityOfficer a = new And(datasetStateCheck, hasRoleCheck);
        // logger.debug(a.explainAllowed(ctxParameters));
        // logger.debug(a.explainVisible(ctxParameters));

        SecurityOfficer b = new And(isDepositorCheck, isSelfCheck);
        // logger.debug(b.explainAllowed(ctxParameters));
        // logger.debug(b.explainVisible(ctxParameters));

        SecurityOfficer c = new Or(a, b, hasRoleCheck);
        // logger.debug(c.explainEnableAllowed(ctxParameters));
        // logger.debug(c.explainVisible(ctxParameters));

        SecurityOfficer d = new Not(workFlowCheck);
        // logger.debug(d.explainAllowed(ctxParameters));
        // logger.debug(d.explainVisible(ctxParameters));

        SecurityOfficer e = new SplitAnswer(a, b);
        // logger.debug(e.explainAllowed(ctxParameters));
        // logger.debug(e.explainVisible(ctxParameters));

        SecurityOfficer f = new And(e, new Or(c, d));
        if (Tester.isVerbose())
            logger.debug(f.explainEnableAllowed(ctxParameters));
        // logger.debug(f.explainVisible(ctxParameters));

    }
}
