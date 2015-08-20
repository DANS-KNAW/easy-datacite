package nl.knaw.dans.easy.tools.task.am.jumpoff;

import static org.junit.Assert.*;

import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;

import org.dom4j.Attribute;
import org.dom4j.tree.FlyweightAttribute;
import org.junit.Test;

public class MissingLinkCorrectorTaskOnlineTest {

    public String[] GOOD_STRINGS = {"/customAIP/twips.dans.knaw.nl--8513817369253725916-1270566839781/logowoon.jpg",
            "../customAIP/twips.dans.knaw.nl--8513817369253725916-1270566839781/logowoon.jpg",
            "customAIP/twips.dans.knaw.nl-807139707405450874-1226310466720/a00604_ore.rdf",
            "http://easy.dans.knaw.nl/customAIP/twips.dans.knaw.nl--8513817369253725916-1270566839781/logowoon.jpg",
            "https://easy.dans.knaw.nl/customAIP/twips.dans.knaw.nl-807139707405450874-1226310466720/a00604_ore.rdf"

    };

    public String[] BAD_STRINGS = {"blabla/twips.dans.knaw.nl-807139707405450874-1226310466720/a00604_ore.rdf", "", "customAAP/123",
            "https://easy.dans.knaw.nl/dms/../customAIP/twips.dans.knaw.nl-807139707405450874-1226310466720/a00604_ore.rdf"};

    @Test
    public void locateResource() throws Exception {
        MissingLinkCorrectorTask ml = new MissingLinkCorrectorTask("src/test/resources");

        for (String text : GOOD_STRINGS) {
            String path = ml.locateResource(text);
            assertTrue(path.startsWith("/customAIP"));
        }

        for (String text : BAD_STRINGS) {
            String path = ml.locateResource(text);
            assertNull(path);
        }

    }

    @Test
    public void processScript() throws Exception {
        MissingLinkCorrectorTask ml = new MissingLinkCorrectorTask("src/test/resources") {

            @Override
            protected String uploadFile(String elementName, String originalText, String oldPath) throws FatalTaskException {
                assertEquals("/customAIP/twips.dans.knaw.nl-6802027138963377881-1234195166246/bestandwijzerTOP10vector.jpg", oldPath);
                return "/resources/easy/content/bla&did=dad";
            }
        };

        String text = "window.open('/customAIP/twips.dans.knaw.nl-6802027138963377881-1234195166246/bestandwijzerTOP10vector.jpg',null,'status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes'); return false";
        String newText = ml.processOnclickText(text);

        String expected = "window.open('/resources/easy/content/bla&did=dad',null,'status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes'); return false";

        assertEquals(expected, newText);
    }

}
