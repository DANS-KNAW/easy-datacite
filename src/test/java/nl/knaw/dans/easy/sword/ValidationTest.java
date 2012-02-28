package nl.knaw.dans.easy.sword;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataImpl;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.purl.sword.base.SWORDErrorException;

@RunWith(Parameterized.class)
public class ValidationTest extends Tester
{
    private final String fileName;
    private final String messageContent;

    public ValidationTest(String fileName, final String messageContent)
    {
        this.fileName = fileName;
        this.messageContent = messageContent;
    }

    @Parameters
    public static Collection<String[]> createParameters() throws Exception
    {
        final List<String[]> constructortSignatureInstances = new ArrayList<String[]>();
        constructortSignatureInstances.add(new String[] {"missingMetadata.xml", "[deposit.field_required]"});
        constructortSignatureInstances.add(new String[] {"disciplineWithWhiteSpace.xml", "[]"});
        return constructortSignatureInstances;
    }

    @Ignore
    @Test
    public void executeFailingValidation() throws Exception
    {
        final byte[] fileContent = FileUtil.readFile(new File("src/test/resources/input/" + fileName));
        final EasyMetadata metadata = (EasyMetadata) JiBXObjectFactory.unmarshal(EasyMetadataImpl.class, fileContent);
        try
        {
            EasyBusinessFacade.validateSemantics(metadata);
        }
        catch (final SWORDErrorException se)
        {
            if (!se.getMessage().contains(messageContent))
                throw new Exception("\n" + fileName + " expected a message containing " + messageContent + "\nbut got " + se.getMessage());
        }
        catch (final Exception se)
        {
            throw new Exception("\n" + fileName + " expected " + SWORDErrorException.class.getName() + " with a message containing " + messageContent
                    + "\nbut got " + se.toString());
        }
        throw new Exception("\n" + fileName + " expected " + SWORDErrorException.class.getName() + " with a message containing " + messageContent
                + "\nbut got no exception");
    }
}
