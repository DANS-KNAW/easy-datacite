package nl.knaw.dans.easy.rest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;

/**
 * A utility class for retrieving information about image thumbnails from datasets.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
public class ThumbnailUtil extends SimpleXmlWriter {
    /**
     * A immutable string for the folder name and XML root node.
     */
    public static final String THUMBNAILS = "thumbnails";

    /**
     * A function to check whether a FileItem is an image thumbnail.
     * 
     * @param user
     *        The EasyUser.
     * @param d
     *        The Dataset.
     * @param file
     *        The FileItem that you want to check.
     * @return True iff file is within a thumbnails folder.
     * @throws ServiceException
     *         Thrown if something goes wrong internally.
     */
    public static boolean isThumbnail(EasyUser user, Dataset d, FileItemVO file) throws ServiceException {
        List<ItemVO> items = Services.getItemService().getFilesAndFolders(user, d, new DmoStoreId(file.getParentSid()));
        return items.size() == 1 ? items.get(0).getName().equals(THUMBNAILS) : false;
    }

    /**
     * Returns a list of ID's of thumbnails.
     * 
     * @param user
     *        the EasyUser.
     * @param datasetSid
     *        Store ID of the dataset.
     * @return A list of ID's of the thumbnails.
     * @throws ServiceException
     *         If something goes wrong.
     */
    public static String getThumbnailIdsXml(EasyUser user, String datasetSid) throws ServiceException {
        List<String> ids = getThumbnailIds(user, datasetSid);
        String xml = startNode(THUMBNAILS);
        for (String id : ids) {
            xml += addNode("sid", id);
        }
        return xml + endNode(THUMBNAILS);
    }

    private static List<String> getThumbnailIds(EasyUser user, String datasetSid) throws ServiceException {
        Dataset d = Services.getDatasetService().getDataset(user, new DmoStoreId(datasetSid));
        List<ItemVO> rootItems = Services.getItemService().getFilesAndFolders(user, d, d.getDmoStoreId());
        List<String> ids = new ArrayList<String>();
        getThumbnailIds(d, user, rootItems, ids);
        return ids;
    }

    private static void getThumbnailIds(Dataset d, EasyUser user, List<ItemVO> items, List<String> ids) throws ServiceException {
        for (ItemVO item : items) {
            if (item instanceof FolderItemVO) {
                FolderItemVO folder = (FolderItemVO) item;
                List<ItemVO> children = Services.getItemService().getFilesAndFolders(user, d, new DmoStoreId(folder.getSid()));
                if (folder.getName().equals(THUMBNAILS)) {
                    for (ItemVO child : children) {
                        if (child instanceof FileItemVO) {
                            ids.add(child.getSid());
                        }
                    }
                } else {
                    getThumbnailIds(d, user, children, ids);
                }
            }
        }
    }

}
