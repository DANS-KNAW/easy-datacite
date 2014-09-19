package nl.knaw.dans.c.dmo.collections.xml;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

public class JiBXCollection extends AbstractJiBXObject<JiBXCollection> {

    private static final long serialVersionUID = -1753385302725866794L;

    private String namespace;
    private String id;
    private String label = "[no label]";
    private String shortName = "[short-name]";
    private boolean publishedAsOAISet;
    private JiBXDublinCoreMetadata dcMetadata;
    private List<JiBXCollection> children;

    public JiBXCollection() {

    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isPublishedAsOAISet() {
        return publishedAsOAISet;
    }

    public void setPublishedAsOAISet(boolean publishedAsOAISet) {
        this.publishedAsOAISet = publishedAsOAISet;
    }

    public void setDcMetadata(JiBXDublinCoreMetadata dcMetadata) {
        this.dcMetadata = dcMetadata;
    }

    public JiBXDublinCoreMetadata getDcMetadata() {
        if (dcMetadata == null) {
            dcMetadata = new JiBXDublinCoreMetadata();
        }
        return dcMetadata;
    }

    public void setDcMetadataElement(Element dcMetadataElement) throws XMLDeserializationException {
        if (dcMetadataElement != null) {
            dcMetadata = (JiBXDublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, dcMetadataElement);
        }
    }

    public Element getDcMetadataElement() throws XMLSerializationException {
        if (dcMetadata == null) {
            return null;
        } else {
            return dcMetadata.asElement();
        }
    }

    public List<JiBXCollection> getChildren() {
        if (children == null) {
            children = new ArrayList<JiBXCollection>();
        }
        return children;
    }

    public void setChildren(List<JiBXCollection> children) {
        this.children = children;
    }

    public void addChild(JiBXCollection child) {
        getChildren().add(child);
    }

}
