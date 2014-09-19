package nl.knaw.dans.easy.xml;

import org.dom4j.Element;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;

public class AdditionalContent extends AbstractJiBXObject<AdditionalContent> {

    private static final long serialVersionUID = -2201150097479313915L;

    private String id;
    private String label;
    private Element content;

    public AdditionalContent() {
        this(null, null, null);
    }

    public AdditionalContent(String id, Element content) {
        this(id, null, content);
    }

    public AdditionalContent(String id, String label, Element content) {
        this.id = id;
        this.label = label;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Element getContent() {
        return content;
    }

    public void setContent(Element content) {
        this.content = content;
    }

}
