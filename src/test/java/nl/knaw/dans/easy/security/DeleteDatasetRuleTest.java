package nl.knaw.dans.easy.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.SecurityOfficer;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteDatasetRuleTest
{

    private static final Logger logger = LoggerFactory.getLogger(DeleteDatasetRuleTest.class);

    private static SecurityOfficer rule;
    private static Dataset dataset;
    private static EasyUser user;
    private static ContextParameters ctx;

    private boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforeClass()
    {
        rule = new CodedAuthz().getDeleteDatasetRule();
        dataset = EasyMock.createMock(Dataset.class);
        user = EasyMock.createMock(EasyUser.class);
        ctx = new ContextParameters(user, dataset);
    }

    @Test
    public void testProposition()
    {
        String proposition = "(([SessionUser is depositor of dataset] AND [Dataset state is DRAFT]) OR ([SessionUser has role ARCHIVIST] AND [Dataset state is DRAFT or SUBMITTED or PUBLISHED or MAINTENANCE]))";

        assertEquals(proposition, rule.getProposition());
    }

    // @formatter:off
    /*
     * Because of efficient equation have to test following table: 0x0x --> false, false 100x --> false,
     * false 11xx --> true, true 0x10 --> false, false 0x11 --> true, true where 0: partial proposition
     * is 'false', 1: partial proposition is 'true' and x: partial proposition 'does not matter'.
     */
    // @formatter:on

    @Test
    public void test0x0x()
    {
        EasyMock.reset(dataset, user);
        EasyMock.expect(user.isActive()).andReturn(true).times(4);
        EasyMock.expect(dataset.hasDepositor(user)).andReturn(false).times(2);
        EasyMock.expect(user.hasRole(Role.ARCHIVIST)).andReturn(false).times(2);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();

        EasyMock.replay(dataset, user);
        assertFalse(rule.isEnableAllowed(ctx));
        assertFalse(rule.isComponentVisible(ctx));
        EasyMock.verify(dataset, user);
    }

    @Test
    public void test100x()
    {
        EasyMock.reset(dataset, user);
        EasyMock.expect(user.isActive()).andReturn(true).times(4);
        EasyMock.expect(dataset.hasDepositor(user)).andReturn(true).times(2);
        EasyMock.expect(dataset.getAdministrativeState()).andReturn(DatasetState.PUBLISHED).times(2);
        EasyMock.expect(user.hasRole(Role.ARCHIVIST)).andReturn(false).times(2);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();

        EasyMock.replay(dataset, user);
        assertFalse(rule.isEnableAllowed(ctx));
        assertFalse(rule.isComponentVisible(ctx));
        EasyMock.verify(dataset, user);
    }

    @Test
    public void test11xx()
    {
        EasyMock.reset(dataset, user);
        EasyMock.expect(user.isActive()).andReturn(true).times(2);
        EasyMock.expect(dataset.hasDepositor(user)).andReturn(true).times(2);
        EasyMock.expect(dataset.getAdministrativeState()).andReturn(DatasetState.DRAFT).times(2);

        EasyMock.replay(dataset, user);
        assertTrue(rule.isEnableAllowed(ctx));
        assertTrue(rule.isComponentVisible(ctx));
        EasyMock.verify(dataset, user);
    }

    @Test
    public void test0x10()
    {
        EasyMock.reset(dataset, user);
        EasyMock.expect(user.isActive()).andReturn(true).times(4);
        EasyMock.expect(dataset.hasDepositor(user)).andReturn(false).times(2);

        EasyMock.expect(user.hasRole(Role.ARCHIVIST)).andReturn(true).times(2);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();
        EasyMock.expect(dataset.getAdministrativeState()).andReturn(DatasetState.MAINTENANCE).times(8);

        EasyMock.replay(dataset, user);
        assertTrue(rule.isEnableAllowed(ctx));
        assertTrue(rule.isComponentVisible(ctx));
        EasyMock.verify(dataset, user);
    }

    @Test
    public void test0x11()
    {
        EasyMock.reset(dataset, user);
        EasyMock.expect(user.isActive()).andReturn(true).times(4);
        EasyMock.expect(dataset.hasDepositor(user)).andReturn(false).times(2);

        EasyMock.expect(user.hasRole(Role.ARCHIVIST)).andReturn(true).times(2);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();
        EasyMock.expect(dataset.getAdministrativeState()).andReturn(DatasetState.SUBMITTED).times(4);

        EasyMock.replay(dataset, user);
        assertTrue(rule.isEnableAllowed(ctx));
        assertTrue(rule.isComponentVisible(ctx));
        EasyMock.verify(dataset, user);
    }

    @Test
    public void testNull()
    {
        Object[] args = null;
        ContextParameters ctxParameters = new ContextParameters(args);
        assertFalse(rule.isEnableAllowed(ctxParameters));
        assertFalse(rule.isComponentVisible(ctxParameters));
    }

    @Test
    public void testExplain()
    {
        Object[] args = null;
        ContextParameters ctxParameters = new ContextParameters(args);
        if (verbose)
            logger.debug(rule.explainEnableAllowed(ctxParameters));
    }

}
