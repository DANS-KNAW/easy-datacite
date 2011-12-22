package nl.knaw.dans.easy.domain.dataset;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.DescriptiveMetadata;
import nl.knaw.dans.easy.domain.model.FileItemMetadata;
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
    
    @SuppressWarnings("unchecked")
    public List<KeyValuePair> getMetadataForAnonKnown() {
    	List<KeyValuePair> props = new ArrayList<KeyValuePair>();
    	
    	ArrayList<Element> metadata = getFileItemMetadataAsList();
    	
    	Element path = getElement(metadata, "path");
		if(path!=null) {
			path.setName("Path");
			props.add(new KeyValuePair(path.getName(), path.getText()));
		}
		Element size = getElement(metadata, "size");
		if(size!=null) {
			size.setName("Size");
			props.add(new KeyValuePair(size.getName(), size.getText()));
		}
		Element creator = getElement(metadata, "creatorRole");
		if(creator!=null) {
			creator.setName("Creator");
			props.add(new KeyValuePair(creator.getName(), creator.getText()));
		}
		BaseElement accessible = new BaseElement("Accessible");
		if(accessible!=null) {
			accessible.setName("Accessible");
			props.add(new KeyValuePair(accessible.getName(), accessible.getText()));
		}
    	
		// now add additional metadata
		AdditionalMetadata addMetadata = fileItemMetadata.getAdditionalMetadata();
        List<AdditionalContent> addContents = addMetadata.getAdditionalContentlist();
        for (AdditionalContent addContent : addContents)
        {
            Element content = addContent.getContent();
            // iterate through the list
            List<Element> elements = content.elements();
            for(Element e : elements) {
            	props.add(new KeyValuePair(e.getName(), e.getText()));
            }
        }
        
        // now we add the descriptive metadata
        props.addAll(descriptiveMetadata.getProperties());
		
    	return props;
    }
    
    @SuppressWarnings("unchecked")
    public List<KeyValuePair> getMetadataForArchDepo() {
    	List<KeyValuePair> props = new ArrayList<KeyValuePair>();
    	
    	ArrayList<Element> metadata = getFileItemMetadataAsList();
    	
    	Element path = getElement(metadata, "path");
		if(path!=null) {
			path.setName("Path");
			props.add(new KeyValuePair(path.getName(), path.getText()));
		}
		Element mimeType = getElement(metadata, "mimeType");
		if(mimeType!=null) {
			mimeType.setName("Mime type");
			props.add(new KeyValuePair(mimeType.getName(), mimeType.getText()));
		}
		Element size = getElement(metadata, "size");
		if(size!=null) {
			size.setName("Size");
			props.add(new KeyValuePair(size.getName(), size.getText()));
		}
		Element creator = getElement(metadata, "creatorRole");
		if(creator!=null) {
			creator.setName("Creator");
			props.add(new KeyValuePair(creator.getName(), creator.getText()));
		}
		Element visibleTo = getElement(metadata, "visibleTo");
		if(visibleTo!=null) {
			visibleTo.setName("Visible to");
			props.add(new KeyValuePair(visibleTo.getName(), visibleTo.getText()));
		}
		Element accessibleTo = getElement(metadata, "accessibleTo");
		if(accessibleTo!=null) {
			accessibleTo.setName("Accessible to");
			props.add(new KeyValuePair(accessibleTo.getName(), accessibleTo.getText()));
		}
		Element sid = getElement(metadata, "sid");
		if(sid!=null) {
			sid.setName("Sid");
			props.add(new KeyValuePair(sid.getName(), sid.getText()));
		}
		Element parentSid = getElement(metadata, "parentSid");
		if(parentSid!=null) {
			parentSid.setName("Parent sid");
			props.add(new KeyValuePair(parentSid.getName(), parentSid.getText()));
		}
		Element datasetSid = getElement(metadata, "datasetSid");
		if(datasetSid!=null) {
			datasetSid.setName("Dataset sid");
			props.add(new KeyValuePair(datasetSid.getName(), datasetSid.getText()));
		}
    	
		// now add additional metadata
		AdditionalMetadata addMetadata = fileItemMetadata.getAdditionalMetadata();
        List<AdditionalContent> addContents = addMetadata.getAdditionalContentlist();
        for (AdditionalContent addContent : addContents)
        {
            Element content = addContent.getContent();
            // iterate through the list
            List<Element> elements = content.elements();
            for(Element e : elements) {
            	props.add(new KeyValuePair(e.getName(), e.getText()));
            }
        }
        
        // now we add the descriptive metadata
        props.addAll(descriptiveMetadata.getProperties());
		
    	return props;
    }
    
    @SuppressWarnings("unchecked")
    private ArrayList<Element> getFileItemMetadataAsList()
    {
        try
        {
        	return new ArrayList<Element>(new SAXReader().read(fileItemMetadata.asXMLInputStream()).getRootElement().selectNodes("/*/*"));
        } catch (DocumentException e) {
        	logger.error("Error applying file rights", e);
		} catch (XMLSerializationException e) {
			logger.error("Error applying file rights", e);
		}
        return new ArrayList<Element>();
    }

    private Element getElement(ArrayList<Element> list, String key) {
		for(Element e: list) {
			if(e.getName().equals(key)) {
				return e;
			}
		}
		return null;
	}
}
