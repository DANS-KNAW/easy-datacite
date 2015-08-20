package nl.knaw.dans.easy.tools.task.am.dataset;

import static org.junit.Assert.*;

import org.junit.Test;

public class RelationLinkCorrectorTakTest {

    private static RelationLinkCorrectorTask RLT = new RelationLinkCorrectorTask();

    private static String[][] TESTSTRINGS = {
            {
                    "http://easy.dans.knaw.nl/dms?command=AIP_info&amp;aipId=twips.dans.knaw.nl--8513817369253725916-1270566839781&amp;windowStyle=default&amp;windowContext=default",
                    "twips.dans.knaw.nl--8513817369253725916-1270566839781"},
            {"http://easy.dans.knaw.nl/dms?command=AIP_info&amp;aipId=twips.dans.knaw.nl--8513817369253725916-1270566839781",
                    "twips.dans.knaw.nl--8513817369253725916-1270566839781"}};

    @Test
    public void findAipId() {
        for (String[] testString : TESTSTRINGS) {
            assertEquals(testString[1], RLT.findAipId(testString[0]));
        }
    }

}
