package nl.knaw.dans.easy.sword;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.sword.util.Fixture;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.purl.sword.base.SWORDErrorException;

@RunWith(Parameterized.class)
public class TestValidation extends Fixture
{
    private final String metadataFileName;
    private final String messageContent;

    public TestValidation(String metadataFileName, final String messageContent)
    {
        this.metadataFileName = metadataFileName;
        this.messageContent = messageContent;
    }

    /**
     * Documents expected content of &lt;atom:summary type="text"> in the HTTP response 400 Bad Request.
     * The metadata files won't cause a draft dataset when submitted. Input errors not detected by the
     * validation (such as trailing or leading white space) will pass a NoOp submission but might cause a
     * draft dataset when submitted for real.
     */
    @Parameters
    public static Collection<String[]> createParameters() throws Exception
    {
        final List<String[]> constructorSignatureInstances = new ArrayList<String[]>();

        constructorSignatureInstances.add(new String[] {"invalidAccessRights.xml", " is not a valid key in the list "});

        // no longer causes a draft dataset after refactoring nl.knaw.dans.easy.business.dataset.MetadataValidator
        // TODO no complaint about neither dc.creator nor eas.creator being specified, but wait for the external XML
        constructorSignatureInstances.add(new String[] {"missingMetadata.xml", "Missing required field dc.title"});

        // used to cause a draft dataset because the notification message could not be created
        constructorSignatureInstances.add(new String[] {"disciplineWithWhiteSpace.xml", null});

        // in this example a schema id is added manually after download of the xml from a test dataset
        // TODO will fail after 2014-10-18
        constructorSignatureInstances.add(new String[] {"SpatialPoint.xml", null});

        // just as downloaded from a test dataset
        constructorSignatureInstances.add(new String[] {"SpatialPointWithoutSchemaId.xml", "Expected is 'archaeology.eas.spatial'"});

        // TODO make error message mores specific, needs architectural solution see also EBIU workaround
        constructorSignatureInstances.add(new String[] {"SpatialPointWithoutX.xml", "invalid"});
        constructorSignatureInstances.add(new String[] {"SpatialPointWithoutY.xml", "invalid"});

        constructorSignatureInstances.add(new String[] {"InvalidDiscipline.xml", "Value 'nonsense' is not facet-valid"});
        constructorSignatureInstances.add(new String[] {"SaxError.xml", "must be terminated by the matching end-tag"});

        // TODO mock the system date for more precise boundary checks
        constructorSignatureInstances.add(new String[] {"embargoPast.xml", "in the past"});
        constructorSignatureInstances.add(new String[] {"embargoFuture.xml", "more than two years"});

        return constructorSignatureInstances;
    }

    @Test
    public void executeValidation() throws Exception
    {
        final byte[] fileContent = FileUtil.readFile(new File("src/test/resources/input/" + metadataFileName));
        try
        {
            EasyMetadataFacade.validate(fileContent);
        }
        catch (final SWORDErrorException se)
        {
            if (messageContent == null)
                throw new Exception("\n" + metadataFileName + " no error expected but got " + se.toString(), se);
            if (!se.getMessage().contains(messageContent))
                throw new Exception("\n" + metadataFileName + " expected a message containing " + messageContent + "\nbut got " + se.getMessage(), se);
            return;
        }
        catch (final Exception se)
        {
            if (messageContent == null)
                throw new Exception("\n" + metadataFileName + " no error expected but got " + se.toString());
            throw new Exception("\n" + metadataFileName + " expected " + SWORDErrorException.class.getName() + " with a message containing: " + messageContent
                    + "\nbut got " + se.toString(), se);
        }
        if (messageContent != null)
            throw new Exception("\n" + metadataFileName + " expected " + SWORDErrorException.class.getName() + " with a message containing: " + messageContent
                    + "\nbut got no exception");
    }
}
