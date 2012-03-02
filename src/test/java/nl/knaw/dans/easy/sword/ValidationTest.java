package nl.knaw.dans.easy.sword;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.knaw.dans.common.lang.util.FileUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.purl.sword.base.SWORDErrorException;

@RunWith(Parameterized.class)
public class ValidationTest extends Tester
{
    private final String metadataFileName;
    private final String messageContent;

    public ValidationTest(String metadataFileName, final String messageContent)
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
        final List<String[]> constructortSignatureInstances = new ArrayList<String[]>();
        
        // no longer causes a draft dataset after refactoring nl.knaw.dans.easy.business.dataset.MetadataValidator
        constructortSignatureInstances.add(new String[] {"missingMetadata.xml", "[deposit.field_required]"});
        
        // used to cause a draft dataset because the notification message could not be created
        constructortSignatureInstances.add(new String[] {"disciplineWithWhiteSpace.xml", null});

        // in this example a schema id is added manually after download of the xml from a test dataset
        constructortSignatureInstances.add(new String[] {"SpatialPoint.xml", null});
        
        // just as downloaded from a test dataset
        constructortSignatureInstances.add(new String[] {"SpatialPointWithoutSchemaId.xml", "Expected is 'archaeology.eas.spatial'"});
        
        // TODO make error message mores specific, needs architectural solution see also EBIU workaround
        constructortSignatureInstances.add(new String[] {"SpatialPointWithoutX.xml", "invalid"});
        constructortSignatureInstances.add(new String[] {"SpatialPointWithoutY.xml", "invalid"});
        return constructortSignatureInstances;
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
                throw new Exception("\n" + metadataFileName + " no error expected but got " + se.toString(),se);
            if (!se.getMessage().contains(messageContent))
                throw new Exception("\n" + metadataFileName + " expected a message containing " + messageContent + "\nbut got " + se.getMessage(),se);
            return;
        }
        catch (final Exception se)
        {
            if (messageContent == null)
                throw new Exception("\n" + metadataFileName + " no error expected but got " + se.toString());
            throw new Exception("\n" + metadataFileName + " expected " + SWORDErrorException.class.getName() + " with a message containing: " + messageContent
                    + "\nbut got " + se.toString(),se);
        }
        if (messageContent != null)
            throw new Exception("\n" + metadataFileName + " expected " + SWORDErrorException.class.getName() + " with a message containing: " + messageContent
                    + "\nbut got no exception");
    }
}
