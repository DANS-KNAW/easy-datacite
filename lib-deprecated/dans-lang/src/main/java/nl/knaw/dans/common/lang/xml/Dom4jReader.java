package nl.knaw.dans.common.lang.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Dom4jReader {

    private final Document document;

    public Dom4jReader(String filename) throws DocumentException {
        this(new File(filename));
    }

    public Dom4jReader(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        document = reader.read(file);
    }

    public String getString(String xpath) throws DocumentException {
        Node node = getNode(xpath);
        return node.getText();
    }

    @SuppressWarnings("unchecked")
    public List<String> getList(String xpath) {
        List<String> textList = new ArrayList<String>();
        List<Node> nodes = document.selectNodes(xpath);
        for (Node n : nodes) {
            textList.add(n.getText());
        }
        return textList;
    }

    public Node getNode(String xpath) throws DocumentException {
        Node node = document.selectSingleNode(xpath);
        if (node == null) {
            throw new DocumentException("The xpath expression '" + xpath + "' did not yield a result.");
        }
        return node;
    }

    @SuppressWarnings("unchecked")
    public List<Node> getNodes(String xpath) {
        List<Node> nodes = document.selectNodes(xpath);
        return nodes;
    }

}
