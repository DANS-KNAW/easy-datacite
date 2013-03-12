package nl.knaw.dans.pf.language.xml.vocabulary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapFromXSD
{
    private final Map<String, String> enum2appInfo = new HashMap<String, String>();
    private final Map<String, String> appInfo2doc = new HashMap<String, String>();
    private final Map<String, String> enum2doc = new HashMap<String, String>();

    private static final Logger logger = LoggerFactory.getLogger(MapFromXSD.class);

    public MapFromXSD(final String xsd) throws DocumentException
    {
        for (final Node enumerationNode : readEnumerationNodes(xsd))
        {
            final String key = enumerationNode.valueOf("@value").trim();
            final Node appinfoNode = (Node) enumerationNode.selectNodes(".//xs:appinfo").iterator().next();
            final String appInfoText = appinfoNode.getText().trim();
            final Node docNode = (Node) enumerationNode.selectNodes(".//xs:documentation").iterator().next();
            final String docText = docNode.getText().trim();
            getEnum2appInfo().put(key, appInfoText);
            getAppInfo2doc().put(appInfoText, docText);
            getEnum2doc().put(key, docText);
        }
        logger.debug("keys: " + Arrays.toString(getEnum2appInfo().keySet().toArray()));
        logger.debug("values: " + Arrays.toString(getEnum2appInfo().values().toArray()));
    }

    private Node[] readEnumerationNodes(final String xsd) throws DocumentException
    {
        @SuppressWarnings("unchecked")
        final List<Object> nodes = new SAXReader().read(xsd).selectNodes("//xs:enumeration");
        return (Node[]) nodes.toArray(new Node[nodes.size()]);
    }

    public Map<String, String> getEnum2appInfo()
    {
        return enum2appInfo;
    }

    public Map<String, String> getAppInfo2doc()
    {
        return appInfo2doc;
    }

    public Map<String, String> getEnum2doc()
    {
        return enum2doc;
    }
}
