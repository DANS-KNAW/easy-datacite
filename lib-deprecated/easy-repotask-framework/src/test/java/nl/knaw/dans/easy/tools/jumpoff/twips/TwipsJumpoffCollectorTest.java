package nl.knaw.dans.easy.tools.jumpoff.twips;

import java.io.File;

import nl.knaw.dans.common.lang.util.FileUtil;

import org.junit.Ignore;
import org.junit.Test;

public class TwipsJumpoffCollectorTest {

    public static final String AIP_WITH_JUMPOFF = "/mnt/hgfs/ecco/Public/AIPstore/data/twips.dans.knaw.nl--6615931242394568689-1263459389976";
    public static final String START_ELEMENT = "<appHTML>";
    public static final String END_ELEMENT = "</appHTML>";

    @Ignore("local path")
    @Test
    public void testParser() throws Exception {
        File file = new File(AIP_WITH_JUMPOFF + "/mgmdata/mgmdata.xml");
        byte[] bytes = FileUtil.readFile(file);
        String mgmdata = new String(bytes);
        int start = mgmdata.indexOf(START_ELEMENT);
        int end = mgmdata.indexOf(END_ELEMENT);

        String content = mgmdata.substring(start + 9, end).trim();
        System.err.println(content);
    }
}
