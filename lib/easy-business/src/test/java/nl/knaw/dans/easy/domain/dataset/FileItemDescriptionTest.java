package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.*;

import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;

import org.junit.Test;

public class FileItemDescriptionTest {

    @Test
    public void testConstructor() {
        FileItemDescription fid = new FileItemDescription(new FileItemMetadataImpl());

        List<KeyValuePair> props = fid.getAllProperties();
        assertNotNull(props);

        List<KeyValuePair> descProps = fid.getDescriptiveMetadata().getProperties();
        assertNotNull(descProps);
    }

}
