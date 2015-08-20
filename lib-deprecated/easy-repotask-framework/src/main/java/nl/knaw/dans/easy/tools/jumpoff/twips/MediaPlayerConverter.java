package nl.knaw.dans.easy.tools.jumpoff.twips;

public class MediaPlayerConverter {

    public static final String[][] PATH_MAP = { {"/mediaplayer/playlists", "/mediaplayer/evdo/playlists"},
            {"mediaplayer/playlists", "/mediaplayer/evdo/playlists"},
            {"/mediaplayer/logo_erfgoedvandeoorlog.gif", "/mediaplayer/evdo/logo_erfgoedvandeoorlog.gif"},
            {"mediaplayer/logo_erfgoedvandeoorlog.gif", "/mediaplayer/evdo/logo_erfgoedvandeoorlog.gif"},
            {"/mediaplayer/nav_buttons.jpg", "/mediaplayer/evdo/nav_buttons.jpg"}, {"mediaplayer/nav_buttons.jpg", "/mediaplayer/evdo/nav_buttons.jpg"},
            {"/mediaplayer/player.swf", "/mediaplayer/evdo/player.swf"}, {"mediaplayer/player.swf", "/mediaplayer/evdo/player.swf"},
            {"/mediaplayer/player-config.xml", "/mediaplayer/evdo/player-config.xml"},
            {"mediaplayer/player-config.xml", "/mediaplayer/evdo/player-config.xml"},
            {"/mediaplayer/player-config-vetin.xml", "/mediaplayer/evdo/player-config-vetin.xml"},
            {"mediaplayer/player-config-vetin.xml", "/mediaplayer/evdo/player-config-vetin.xml"},
            {"/mediaplayer/swfobject.js", "/mediaplayer/evdo/swfobject.js"}, {"mediaplayer/swfobject.js", "/mediaplayer/evdo/swfobject.js"},
            {"/mediaplayer/yt.swf", "/mediaplayer/evdo/yt.swf"}, {"mediaplayer/yt.swf", "/mediaplayer/evdo/yt.swf"}};

    public static final String NOT_SELF_CLOSING_DIV = "<div align=\"center\" id=\"thePlayer\"></div>";

    public static final String[] SELF_CLOSING_DIV_LIST = {"<div align=\"center\" id=\"thePlayer\"/>", "<div align=\"center\" id=\"thePlayer\" />",
            "<div id=\"thePlayer\" align=\"center\"/>", "<div id=\"thePlayer\" align=\"center\" />",

            "<div align='center' id='thePlayer'/>", "<div align='center' id='thePlayer' />", "<div id='thePlayer' align='center'/>",
            "<div id='thePlayer' align='center' />"};

    public static final String NOT_SELF_CLOSING_SCRIPT = "<script src=\"/mediaplayer/evdo/swfobject.js\" type=\"text/javascript\"></script>";

    public static final String[] SELF_CLOSING_SCRIPT = {"<script src=\"/mediaplayer/evdo/swfobject.js\" type=\"text/javascript\"/>",
            "<script src=\"/mediaplayer/evdo/swfobject.js\" type=\"text/javascript\" />",
            "<script type=\"text/javascript\" src=\"/mediaplayer/evdo/swfobject.js\"/>",
            "<script type=\"text/javascript\" src=\"/mediaplayer/evdo/swfobject.js\"/ >",

            "<script src='/mediaplayer/evdo/swfobject.js' type='text/javascript'/>", "<script src='/mediaplayer/evdo/swfobject.js' type='text/javascript' />",
            "<script type='text/javascript' src='/mediaplayer/evdo/swfobject.js'/>", "<script type='text/javascript' src='/mediaplayer/evdo/swfobject.js'/ >",};

    public String convertPaths(String content) {
        String newContent = content;
        for (String[] entry : PATH_MAP) {
            newContent = newContent.replaceAll(entry[0], entry[1]);
        }
        return newContent;
    }

    public String convertDivs(String content) {
        String newContent = content;
        for (String entry : SELF_CLOSING_DIV_LIST) {
            newContent = newContent.replaceAll(entry, NOT_SELF_CLOSING_DIV);
        }
        return newContent;
    }

    public String convertScripts(String content) {
        String newContent = content;
        for (String entry : SELF_CLOSING_SCRIPT) {
            newContent = newContent.replaceAll(entry, NOT_SELF_CLOSING_SCRIPT);
        }
        return newContent;
    }

}
