package nl.knaw.dans.easy.tools.task.am.jumpoff;

import static org.junit.Assert.*;
import nl.knaw.dans.common.jibx.bean.JiBXBeanFactoryDelegator;
import nl.knaw.dans.common.lang.repo.bean.BeanFactory;
import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata.MarkupVersionID;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.easy.tools.JointMap;

import org.junit.BeforeClass;
import org.junit.Test;

public class MediaPlayerTextCorrectorTaskTest {
    String markup = "<div align=\"center\" id=\"thePlayer\"/><script src=\"/mediaplayer/swfobject.js\" type=\"text/javascript\"/><script type=\"text/javascript\">var swf = new SWFObject('/mediaplayer/evdo/player.swf','player','512','288','9'); swf.addParam('allowfullscreen','true'); swf.addParam('allowscriptaccess','always'); swf.addParam('wmode','opaque'); swf.addVariable('config','/mediaplayer/player-config.xml'); swf.addVariable('file','GV_GAR_bombardement_01.mp4'); swf.write('thePlayer');</script><p>Sommige interviews bestaan uit meerdere delen. De delen staan onder elkaar. Door middel van de <img border=\"0\" src=\"/mediaplayer/evdo/nav_buttons.jpg\"/> pijltjes knoppen in de videonavigatiebalk is het mogelijk naar het volgende of vorige deel te gaan.</p><p>De interviews zijn letterlijk getranscribeerd, deze transcripties zijn te vinden onder de tab Data Files. Het downloaden van de transcripties is alleen mogelijk door geregistreerde EASY gebruikers.</p>";

    @BeforeClass
    public static void beforeClass() {
        new BeanFactory(new JiBXBeanFactoryDelegator());
    }

    @Test
    public void conversion() throws Exception {
        JumpoffDmo jd = new JumpoffDmo("foo", "easy-dataset:1");
        jd.getTextMarkup().setHtml(markup);
        jd.getJumpoffDmoMetadata().setDefaultMarkupVersionID(MarkupVersionID.TEXT_MU);

        MediaplayerTextCorrectorTask task = new MediaplayerTextCorrectorTask();
        JointMap joint = new JointMap();
        joint.setJumpoffDmo(jd);

        task.run(joint);

        assertTrue(joint.isCycleSubjectDirty());
    }

}
