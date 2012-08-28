package nl.knaw.dans.easy.sword;


import static org.purl.sword.base.SwordValidationInfoType.INFO;
import static org.purl.sword.base.SwordValidationInfoType.WARNING;
import nl.knaw.dans.easy.sword.util.MockUtil;
import nl.knaw.dans.easy.sword.util.SubmitFixture;

import org.junit.Before;
import org.junit.Test;
import org.purl.sword.base.SwordValidationInfo;

/** Integration test for the configuration. */
public class TestSubmitResponseCompliancy extends SubmitFixture
{
    @Before
    public void setupMocking() throws Exception {
        MockUtil.mockAll();
    }

    @Test
    public void submit() throws Exception
    {
        SwordValidationInfo info = execute(false, false, PROPER_ZIP);
        // TODO upgrade compliancy level to VALID
        assertCompliant(WARNING, info);
    }

    @Test
    public void submitVerboseNoOp() throws Exception
    {
        SwordValidationInfo info = execute(true, true, PROPER_ZIP);
        // TODO upgrade compliancy level to VALID
        assertCompliant(INFO, info);
    }

    @Test
    public void submitNoOp() throws Exception
    {
        SwordValidationInfo info = execute(false, true, PROPER_ZIP);
        // TODO upgrade compliancy level to VALID
        assertCompliant(INFO, info);
    }

    @Test 
    public void spatialMetadata() throws Throwable
    {
        SwordValidationInfo info = execute(false, true, getZip("data-plus-spatial-metadata"));
        // TODO upgrade compliancy level to VALID
        assertCompliant(INFO, info);
    }

    @Test 
    public void whiteSpace() throws Throwable
    {
        SwordValidationInfo info = execute(false, true, getZip("disciplineWithWhiteSpace"));
        // TODO upgrade compliancy level to VALID
        assertCompliant(INFO, info);
    }
}
