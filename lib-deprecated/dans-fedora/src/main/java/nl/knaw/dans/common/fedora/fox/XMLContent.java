package nl.knaw.dans.common.fedora.fox;

import org.dom4j.Element;

public class XMLContent {

    private Element element;

    public XMLContent() {

    }

    public XMLContent(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

}
