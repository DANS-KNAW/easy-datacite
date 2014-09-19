package nl.knaw.dans.easy.rest.util;

import java.util.List;

import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;

/**
 * A class to convert ItemVO's (files and folders) to XML strings.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
public class ItemConverter extends SimpleXmlWriter {

    /**
     * Returns a XML string that represents the items provided in the given list.
     * 
     * @param items
     *        The list of items to parse.
     * @return A String containing XML info about the given items.
     */
    public static String convert(List<ItemVO> items) {
        String xml = startNode("list");
        for (ItemVO item : items) {
            xml += convert(item);
        }
        return xml + endNode("list");
    }

    /**
     * Parses a single item to a XML string.
     * 
     * @param item
     *        The item to parse.
     * @return An XML string containing info about the given item.
     */
    public static String convert(ItemVO item) {
        return item instanceof FileItemVO ? convertFile((FileItemVO) item) : convertFolder((FolderItemVO) item);
    }

    private static String convertFile(FileItemVO file) {
        String xml = startNode("file");
        xml += addNode("name", file.getName());
        xml += addNode("sid", file.getSid());
        xml += addNode("path", file.getPath());
        xml += addNode("mediatype", file.getMimetype());
        return xml + endNode("file");
    }

    private static String convertFolder(FolderItemVO folder) {
        String xml = startNode("folder");
        xml += addNode("name", folder.getName());
        xml += addNode("sid", folder.getSid());
        xml += addNode("path", folder.getPath());
        return xml + endNode("folder");
    }

}
