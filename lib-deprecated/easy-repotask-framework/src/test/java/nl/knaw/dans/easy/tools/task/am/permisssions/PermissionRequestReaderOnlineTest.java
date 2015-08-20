package nl.knaw.dans.easy.tools.task.am.permisssions;

import java.util.Map;

import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.tools.task.am.permissions.PermissionRequestReader;

import org.junit.Ignore;
import org.junit.Test;

public class PermissionRequestReaderOnlineTest {
    private String dmsaaLocation = "/mnt/hgfs/ecco/Public/AIPstore/xmldata/dmsaa.xml";

    @Test
    @Ignore("Contains local path")
    public void testReading() throws Exception {
        PermissionRequestReader reader = new PermissionRequestReader(dmsaaLocation);
        Map<String, PermissionSequenceList> sequenceList = reader.getSequenceListMap();
        System.out.println(sequenceList.size());
        for (String aipId : sequenceList.keySet()) {
            // System.out.println(aipId);
            // System.out.println(sequenceList.get(aipId).asXMLString());
        }
    }

}
