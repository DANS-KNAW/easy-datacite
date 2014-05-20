package nl.knaw.dans.easy.domain.dataset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.DescriptiveMetadata;
import nl.knaw.dans.easy.domain.model.FileItemMetadata;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.xml.AdditionalContent;
import nl.knaw.dans.easy.xml.AdditionalMetadata;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.BaseElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileItemDescription implements Serializable
{
    private static final long serialVersionUID = -9088727185399518709L;

    private static final Logger logger = LoggerFactory.getLogger(FileItemDescription.class);

    private final FileItemMetadata fileItemMetadata;

    private final DescriptiveMetadata descriptiveMetadata;

    public FileItemDescription(FileItemMetadata fileItemMetadata)
    {
        this(fileItemMetadata, null);
    }

    public FileItemDescription(FileItemMetadata fileItemMetadata, DescriptiveMetadata descriptiveMetadata)
    {
        this.fileItemMetadata = fileItemMetadata;
        if (descriptiveMetadata == null)
        {
            this.descriptiveMetadata = new DescriptiveMetadataImpl(new BaseElement("content"));
        }
        else
        {
            this.descriptiveMetadata = descriptiveMetadata;
        }
    }

    public FileItemMetadata getFileItemMetadata()
    {
        return fileItemMetadata;
    }

    public DescriptiveMetadata getDescriptiveMetadata()
    {
        return descriptiveMetadata;
    }

    public List<KeyValuePair> getAllProperties()
    {
        return getMetadataForArchDepo();
    }

    public List<KeyValuePair> getMetadataForAnonKnown()
    {
        List<KeyValuePair> props = new ArrayList<KeyValuePair>();

        ArrayList<Element> metadata = getFileItemMetadataAsList();

        addElement(props, metadata, "path", "Path");
        addElement(props, metadata, "size", "Size");
        addElement(props, metadata, "creatorRole", "Creator");
        addBaseElement(props, "Accessible", "Accessible");
        addStreamingURL(props, metadata);
        addAdditionalMetadata(props);
        props.addAll(descriptiveMetadata.getProperties());

        return props;
    }

    public List<KeyValuePair> getMetadataForArchDepo()
    {
        List<KeyValuePair> props = new ArrayList<KeyValuePair>();

        ArrayList<Element> metadata = getFileItemMetadataAsList();

        addElement(props, metadata, "path", "Path");
        addElement(props, metadata, "mimeType", "Mime type");
        addElement(props, metadata, "size", "Size");
        addElement(props, metadata, "creatorRole", "Creator");
        addElement(props, metadata, "visibleTo", "Visible to");
        addElement(props, metadata, "accessibleTo", "Accessible to");
        addElement(props, metadata, "sid", "Sid");
        addElement(props, metadata, "parentSid", "Parent sid");
        addElement(props, metadata, "datasetSid", "Dataset sid");
        addStreamingURL(props, metadata);
        addAdditionalMetadata(props);
        props.addAll(descriptiveMetadata.getProperties());

        return props;
    }

    private void addAdditionalMetadata(List<KeyValuePair> props)
    {
        AdditionalMetadata addMetadata = fileItemMetadata.getAdditionalMetadata();
        List<AdditionalContent> addContents = addMetadata.getAdditionalContentlist();
        for (AdditionalContent addContent : addContents)
        {
            Element content = addContent.getContent();
            // iterate through the list
            @SuppressWarnings("unchecked")
            List<Element> elements = content.elements();
            for (Element e : elements)
            {
                props.add(new KeyValuePair(e.getName(), e.getText()));
            }
        }
    }

    private void addStreamingURL(List<KeyValuePair> props, ArrayList<Element> metadata)
    {
        Element streamingPath = getElement(metadata, "streamingPath");
        if (streamingPath != null)
        {
            streamingPath.setName("Streaming url");
            props.add(new KeyValuePair(streamingPath.getName(), Services.getItemService().getStreamingHost() + "/" + streamingPath.getText()));
        }
    }

    private void addBaseElement(List<KeyValuePair> props, String key, String name)
    {
        BaseElement accessible = new BaseElement(key);
        if (accessible != null)
        {
            accessible.setName(name);
            props.add(new KeyValuePair(accessible.getName(), accessible.getText()));
        }
    }

    private void addElement(List<KeyValuePair> props, ArrayList<Element> metadata, String key, String name)
    {
        Element path = getElement(metadata, key);
        if (path != null && path.getText().trim().length() != 0)
        {
            path.setName(name);
            props.add(new KeyValuePair(path.getName(), path.getText()));
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Element> getFileItemMetadataAsList()
    {
        try
        {
            return new ArrayList<Element>(new SAXReader().read(fileItemMetadata.asXMLInputStream()).getRootElement().selectNodes("/*/*"));
        }
        catch (DocumentException e)
        {
            logger.error("Error applying file rights", e);
        }
        catch (XMLSerializationException e)
        {
            logger.error("Error applying file rights", e);
        }
        return new ArrayList<Element>();
    }

    private Element getElement(ArrayList<Element> list, String key)
    {
        for (Element e : list)
        {
            if (e.getName().equals(key))
            {
                return e;
            }
        }
        return null;
    }
}
