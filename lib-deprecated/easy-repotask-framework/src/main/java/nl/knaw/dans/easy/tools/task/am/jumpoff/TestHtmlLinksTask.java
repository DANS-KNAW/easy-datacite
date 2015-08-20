package nl.knaw.dans.easy.tools.task.am.jumpoff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmoRelations;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class TestHtmlLinksTask extends AbstractTask {

    private static final String charset = "UTF-8";
    private static final String HREF = "href";
    private static final String ONCLICK = "onClick";

    private String datasetId;
    private JumpoffDmo currentJumpoff;
    private int maxRecursionDepth;
    private String host;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        currentJumpoff = joint.getJumpoffDmo();

        datasetId = ((JumpoffDmoRelations) currentJumpoff.getRelations()).getObjectId();

        Document doc = getDocument(joint);

        HttpURLConnection.setFollowRedirects(false);

        for (Object el : doc.selectNodes("//a")) {
            Element node = (Element) el;

            Attribute href = node.attribute(HREF);
            Attribute onclick = node.attribute(ONCLICK);

            boolean onclickOK = false;
            if (onclick != null) {
                String URL = onclick.getStringValue();
                if (URL.contains("window.open")) {
                    int startIndex = URL.indexOf("'") + 1;
                    int endIndex = URL.indexOf("'", startIndex);
                    try {
                        URL = URL.substring(startIndex, endIndex);
                        if (URL.contains("?")) {
                            URL = encodeUrl(URL);
                        }
                        onclickOK = checkLink(URL, doc, ONCLICK);
                    }
                    catch (UnsupportedEncodingException e) {
                        RL.error(new Event(getTaskName(), e.getMessage()));
                    }
                }
            }

            if (!onclickOK) {
                if (href != null) {
                    checkLink(href.getStringValue(), doc, HREF);
                }
            }
        }

    }

    private boolean checkLink(String URL, Document doc, String type) throws TaskException {
        boolean result = true;

        if (URL.startsWith("/")) {
            URL = host + URL;
        }

        if (URL.equals("#")) {
            result = true;
        } else if (URL.startsWith("#")) {
            if (!checkAnchor(URL, doc)) {
                RL.error(new Event(getTaskName(), "SID: " + datasetId + "     (" + type + ") URL: " + URL, currentJumpoff.getStoreId(), datasetId));
                result = false;
            }
        } else if (!checkURL(URL, type, maxRecursionDepth)) {
            RL.error(new Event(getTaskName(), "SID: " + datasetId + "     (" + type + ") URL: " + URL, currentJumpoff.getStoreId(), datasetId));
            result = false;
        }

        return result;
    }

    private boolean checkAnchor(String URL, Document doc) {
        boolean foundAnchor = false;
        String anchor = URL.substring(1);
        for (Object ell : doc.selectNodes("//a")) {
            Attribute attr = ((Element) ell).attribute("name");
            if (attr != null) {
                String name = attr.getStringValue();
                if (name.equals(anchor)) {
                    foundAnchor = true;
                    break;
                }
            }
        }
        return foundAnchor;
    }

    private boolean checkURL(String URL, String type, int maxRecDepth) throws TaskException {
        try {
            URL u = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            RL.info(new Event(getTaskName(), "Response: " + connection.getResponseCode() + "     (" + type + ") URL: " + URL, currentJumpoff.getStoreId(),
                    datasetId));

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || connection.getResponseCode() == HttpURLConnection.HTTP_SEE_OTHER
                    || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM)
            {
                String redirectLocation = connection.getHeaderField("Location");
                return redirectLocation == null || maxRecDepth <= 0 ? false : checkURL(redirectLocation, type, --maxRecDepth);
            } else {
                return false;
            }
        }
        catch (IOException e) {
            return false;
        }
    }

    private String encodeUrl(String URL) throws UnsupportedEncodingException {
        String[] urlParts = URL.split("\\?");
        String[] queryParts = urlParts[1].split("&");
        String query = "";
        for (int i = 0; i < queryParts.length; i++) {
            String queryPart = queryParts[i];
            int splitIndex = queryPart.indexOf('=');
            String param = URLEncoder.encode(queryPart.substring(0, splitIndex), charset);
            String value = URLEncoder.encode(queryPart.substring(splitIndex + 1), charset);
            query = query + param + "=" + value + "&";
        }
        query = query.substring(0, query.length() - 1).replace("dans-jumpoff%3A", "dans-jumpoff:");
        return urlParts[0] + "?" + query;
    }

    private Document getDocument(JointMap joint) throws TaskException {
        Document markupDoc;
        SAXReader reader = new SAXReader();
        try {
            String html = "<div>" + currentJumpoff.getHtmlMarkup().getHtml() + "</div>";
            markupDoc = reader.read(new ByteArrayInputStream(html.getBytes()));
        }
        catch (DocumentException e) {
            RL.error(new Event(getTaskName(), e, "Could not read markup", currentJumpoff.getStoreId(), datasetId, "\n"
                    + currentJumpoff.getHtmlMarkup().getHtml()));
            throw new TaskException("Could not read markup", e, this);
        }
        return markupDoc;
    }

    public void setMaxRecursionDepth(int maxRecursionDepth) {
        this.maxRecursionDepth = maxRecursionDepth;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
