package nl.knaw.dans.easy.web.view.dataset;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class UploadFileMetadataProcess extends UploadSingelFilePostProcess
{

    private static final String NO_FILE_NAME = "expecting <file name='...'> but got: ";

    @Override
    public boolean needsProcessing(final List<File> files)
    {
        return needsProcessing(files, ".xml");
    }

    @Override
    void processUploadedFile(final File file, final Dataset dataset) throws UploadPostProcessException
    {
        final Map<String, Element> fileMap = buildFileMap(file);
        final ItemService itemService = Services.getItemService();
        try
        {
            itemService.saveDescriptiveMetadata(EasySession.get().getUser(), dataset, fileMap);
        }
        catch (final ServiceException e)
        {
            throw new UploadPostProcessException(e);
        }
    }
    
    private static UploadPostProcessException exception(final String string, final Element descriptiveMeatadata)
    {
        final String s = descriptiveMeatadata.asXML();
        final String msg = string + s.substring(0, Math.min(300, s.length()));
        logger.error(msg);
        return new UploadPostProcessException(msg);
    }

    @SuppressWarnings("unchecked")
    static Map<String, Element> buildFileMap(final File file) throws UploadPostProcessException
    {
        logger.debug("importing " + file.getName());
        final Element rootElement = parse(file).getRootElement();
        final Map<String, Element> filesWithMetadata = new HashMap<String, Element>();
        for (final Element fileMetadata : (List<Element>) rootElement.elements())
        {
            final String name = getName(fileMetadata);
            validate(fileMetadata);
            if (filesWithMetadata.containsKey(name)) {
                throw exception("duplicate file name ", fileMetadata);
            }
            filesWithMetadata.put(name, fileMetadata);
        }
        return filesWithMetadata;
    }

    @SuppressWarnings("unchecked")
    private static void assertTextNodesHaveWhiteSpaceOnly(final Element rootElement) throws UploadPostProcessException
    {
        if (rootElement.hasMixedContent())
        {
            for (final Node node : (List<Node>) rootElement.content())
            {
                if (node.getNodeType() == Node.TEXT_NODE)
                {
                    final String trimmed = node.getText().trim();
                    if (!trimmed.equals(""))
                        throw exception("mixed content [" + trimmed + "] in descriptive file metadata: ", rootElement);
                }
                else if (node.getNodeType() != Node.ELEMENT_NODE)
                    throw exception("mixed content in descriptive file metadata: ", rootElement);
            }
        }
    }

    private static String getName(final Element fileMetadata) throws UploadPostProcessException
    {
        final Attribute attribute = fileMetadata.attribute("name");
        if (attribute == null)
            throw exception(NO_FILE_NAME, fileMetadata);
        final String name = attribute.getValue();
        if (name == null || name.equals("") || !fileMetadata.getName().equals("file"))
            throw exception(NO_FILE_NAME, fileMetadata);
        return name;
    }

    @SuppressWarnings("unchecked")
    private static void validate(final Element descriptiveMetadata) throws UploadPostProcessException
    {
        if (!descriptiveMetadata.hasContent())
            throw new UploadPostProcessException("empty descriptive file metadata: " + descriptiveMetadata.asXML());
        assertTextNodesHaveWhiteSpaceOnly(descriptiveMetadata);
        for (final Element element : (List<Element>) descriptiveMetadata.elements())
        {
            if (element.attributeCount() > 0)
                throw exception("no attributes allowed: ", element);
            if (!element.isTextOnly())
                throw exception("only text allowed: ", element);
        }
    }

    private static Document parse(final File file) throws UploadPostProcessException
    {
        final SAXReader reader = new SAXReader();
        Document document;
        try
        {
            document = reader.read(file);
        }
        catch (final DocumentException e)
        {
            throw new UploadPostProcessException("could not parse " + file + ": " + e.getMessage());
        }
        return document;
    }
}
