package nl.knaw.dans.easy.business.md.amd;

import java.util.Iterator;

import nl.knaw.dans.easy.xml.AdditionalContent;
import nl.knaw.dans.easy.xml.AdditionalMetadata;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

/**
 * Replace or add {@link AdditionalContent} with a certain id.
 * Implementation of {@link AdditionalMetadataUpdateStrategy} that adds or replaces additional metadata contained in
 * a flat list of name-value pairs where element-name is name. This type of additional FileItem metadata is used
 * by archaeologists.
 * 
 *
 */
public class ElementnameUpdateStrategy implements AdditionalMetadataUpdateStrategy
{
    private final String additionalId;

    public ElementnameUpdateStrategy(String additionalId)
    {
        this.additionalId = additionalId;
    }

    @Override
    public void update(AdditionalMetadataOwner owner, AdditionalMetadata additionalMetadata)
    {
        AdditionalContent newAdditionalContent = additionalMetadata.getAdditionalContent(additionalId);
        if (newAdditionalContent == null)
        {
            return; // nothing to add or replace
        }

        AdditionalMetadata originalAddMd = owner.getAdditionalMetadata();
        AdditionalContent oldAdditionalContent = originalAddMd.getAdditionalContent(additionalId);
        if (oldAdditionalContent == null)
        {
            originalAddMd.addAdditionalContent(newAdditionalContent); // add everything, nothing to replace
        }
        else
        {
            replaceOrAdd(oldAdditionalContent, newAdditionalContent, originalAddMd);
        }
    }

    @SuppressWarnings("unchecked")
    private void replaceOrAdd(AdditionalContent oldAdditionalContent, AdditionalContent newAdditionalContent, AdditionalMetadata originalAddMd)
    {
        Element oldContent = oldAdditionalContent.getContent();
        Element newContent = newAdditionalContent.getContent();

        Iterator<Element> iter = newContent.elementIterator();
        while (iter.hasNext())
        {
            Element newElement = iter.next();
            String elementName = newElement.getName();

            Element oldElement = oldContent.element(elementName);
            if (oldElement == null)
            {
                Element el = new DefaultElement(newElement.getName());
                el.setText(newElement.getText());
                oldContent.add(el);
            }
            else
            {
                oldElement.setText(newElement.getText());
            }
        }
    }

}
