package nl.knaw.dans.easy.fedora.store;

import static org.junit.Assert.*;
import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.fedora.store.JumpoffDmoConverter;
import nl.knaw.dans.common.jibx.bean.JiBXBeanFactoryDelegator;
import nl.knaw.dans.common.lang.repo.bean.BeanFactory;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.MarkupUnit;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JumpoffDmoConverterTest {

    private boolean verbose = Tester.isVerbose();
    private JumpoffDmoConverter converter = new JumpoffDmoConverter(null);
    private String html = "<div align=\"center\" id=\"thePlayer\"></div>\n"
            + "<!--\\\\\n"
            + "   This is comment\n"
            + "\\\\-->\n"
            + "<script src=\"/mediaplayer/swfobject.js\" type=\"text/javascript\"></script><script type=\"text/javascript\">var swf = new SWFObject('/mediaplayer/player.swf','player','512','288','9'); swf.addParam('allowfullscreen','true'); swf.addParam('allowscriptaccess','always'); swf.addParam('wmode','opaque'); swf.addVariable('config','/mediaplayer/player-config.xml'); swf.addVariable('file','GV_GAR_bombardement_01.mp4'); swf.write('thePlayer');</script>";

    private static final Logger logger = LoggerFactory.getLogger(JumpoffDmoConverterTest.class);

    @BeforeClass
    public static void beforeClass() {
        new BeanFactory(new JiBXBeanFactoryDelegator());
    }

    @Test
    public void convertWithFileContent() throws Exception {
        JumpoffDmo joDmo = new JumpoffDmo("dans-jumpoff:joDmo-test", null);
        MarkupUnit markup = joDmo.getHtmlMarkup();
        markup.setHtml(html);
        markup.setLocation("foo:bar");

        DigitalObject dob = converter.serialize(joDmo);

        if (verbose)
            logger.debug("\n" + dob.asXMLString(4) + "\n");

        String xml = dob.asXMLString();
        assertTrue(xml.contains("foxml:datastreamVersion ID=\"HTML_MU.0\""));

        JumpoffDmo rJoDmo = new JumpoffDmo();
        converter.deserialize(dob, rJoDmo);
    }

    @Test
    public void convertWithBinaryContent() throws Exception {
        JumpoffDmo joDmo = new JumpoffDmo("dans-jumpoff:joDmo-test", null);
        MarkupUnit markup = joDmo.getHtmlMarkup();
        markup.setHtml(html);
        markup.setLocation("foo:bar");

        DigitalObject dob = converter.serialize(joDmo);

        if (verbose)
            logger.debug("\n" + dob.asXMLString(4) + "\n");

        JumpoffDmo rJoDmo = new JumpoffDmo();
        converter.deserialize(dob, rJoDmo);
        MarkupUnit rMarkup = rJoDmo.getHtmlMarkup();
        System.err.println(rMarkup.getHtml());
    }

}
