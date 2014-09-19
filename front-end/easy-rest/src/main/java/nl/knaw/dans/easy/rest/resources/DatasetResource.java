package nl.knaw.dans.easy.rest.resources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.RequestedItem;
import nl.knaw.dans.easy.domain.download.FileContentWrapper;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileItemMetadata;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.rest.util.ItemConverter;
import nl.knaw.dans.easy.rest.util.ThumbnailUtil;
import nl.knaw.dans.easy.rest.util.UrlConverter;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;

/**
 * This class provides methods to access resources/representations regarding datasets.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
@Path("/dataset")
public class DatasetResource extends AuthenticatedResource {

    /**
     * Returns metadata of the dataset if available.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return Response containing the metadata in the format that was requested in the Accept header.
     */
    @GET
    @Path("/{sid}/metadata")
    public Response getMetadata(@PathParam("sid") String sid) {
        try {
            Dataset d = Services.getDatasetService().getDataset(authenticate(), new DmoStoreId(sid));
            EasyMetadata emd = d.getEasyMetadata();
            return responseXmlOrJson(new EmdMarshaller(emd).getXmlString());
        }
        catch (ObjectNotAvailableException e) {
            return notFound("Resource not available: " + sid);
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (IllegalArgumentException e) {
            return notFound("Not a valid id: " + sid);
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
        catch (XMLSerializationException e) {
            return internalServerError(e);
        }
    }

    /**
     * Checks whether metadata can be addressed and returns the applicable HTTP methods.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return Response containing the HTTP methods that are applicable in the Allow header.
     */
    @OPTIONS
    @Path("/{sid}/metadata")
    public Response optionsMetadata(@PathParam("sid") String sid) {
        try {
            Services.getDatasetService().getDataset(authenticate(), new DmoStoreId(sid));
            return optionsResponse();
        }
        catch (ObjectNotAvailableException e) {
            return notFound("Resource not available: " + sid);
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns Dublin-Core metadata of the dataset if available.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return Response containing the metadata in the format that was requested in the Accept header.
     */
    @GET
    @Path("/{sid}/dc-metadata")
    public Response getDcMetadata(@PathParam("sid") String sid) {
        try {
            Dataset d = Services.getDatasetService().getDataset(authenticate(), new DmoStoreId(sid));
            return responseXmlOrJson(d.getEasyMetadata().getDublinCoreMetadata().asXMLString());
        }
        catch (ObjectNotAvailableException e) {
            return notFound("Resource not available: " + sid);
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (IllegalArgumentException e) {
            return notFound("Not a valid id: " + sid);
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
        catch (nl.knaw.dans.common.lang.xml.XMLSerializationException e) {
            return internalServerError(e);
        }
    }

    /**
     * Checks whether Dublin-Core metadata can be addressed and returns the applicable HTTP methods.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return Response containing the HTTP methods that are applicable in the Allow header.
     */
    @OPTIONS
    @Path("/{sid}/dc-metadata")
    public Response optionsDcMetadata(@PathParam("sid") String sid) {
        try {
            Services.getDatasetService().getDataset(authenticate(), new DmoStoreId(sid));
            return optionsResponse();
        }
        catch (ObjectNotAvailableException e) {
            return notFound("Resource not available: " + sid);
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns the date on which the given dataset was modified for the last time.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return Last date modified.
     */
    @GET
    @Path("/{sid}/date-modified")
    public Response getDateModified(@PathParam("sid") String sid) {
        try {
            AdministrativeMetadata amd = Services.getDatasetService().getDataset(authenticate(), new DmoStoreId(sid)).getAdministrativeMetadata();
            return simpleResponse(amd.getDateOfLastChangeTo(amd.getAdministrativeState()).toString());
        }
        catch (ObjectNotAvailableException e) {
            return notFound("Resource not available: " + sid);
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns the jumpoff page (HTML) of the dataset.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return HTML jumpoff page.
     */
    @GET
    @Path("/{sid}/jumpoff")
    @Produces("text/html")
    public Response getJumpoff(@PathParam("sid") String sid) {
        try {
            JumpoffDmo j = Services.getJumpoffService().getJumpoffDmoFor(authenticate(), new DmoStoreId(sid));
            if (j != null) {
                return Response.ok(j.getHtmlMarkup().getHtml()).build();
            } else {
                return notFound("Jumpoff not available.");
            }
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * Checks whether the jumpoff page of the given dataset is addressable.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return Response containing the HTTP methods that are applicable in the Allow header.
     */
    @OPTIONS
    @Path("/{sid}/jumpoff")
    public Response optionsJumpoff(@PathParam("sid") String sid) {
        try {
            JumpoffDmo j = Services.getJumpoffService().getJumpoffDmoFor(authenticate(), new DmoStoreId(sid));
            if (j != null) {
                return optionsResponse();
            } else {
                return notFound("Jumpoff not available.");
            }
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns jumpoff metadata of the dataset if available.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return Jumpoff page metadata.
     */
    @GET
    @Path("/{sid}/jumpoff/metadata")
    public Response getJumpoffMetadata(@PathParam("sid") String sid) {
        try {
            JumpoffDmo j = Services.getJumpoffService().getJumpoffDmoFor(authenticate(), new DmoStoreId(sid));
            if (j != null) {
                return responseXmlOrJson(j.getJumpoffDmoMetadata().asXMLString());
            } else {
                return notFound("Jumpoff metadata not available.");
            }
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
        catch (nl.knaw.dans.common.lang.xml.XMLSerializationException e) {
            return internalServerError(e);
        }
    }

    /**
     * Checks whether jumpoff metadata can be addressed and returns the applicable HTTP methods.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return Metadata of the jumpoff page.
     */
    @OPTIONS
    @Path("/{sid}/jumpoff/metadata")
    public Response optionsJumpoffMetadata(@PathParam("sid") String sid) {
        try {
            JumpoffDmo j = Services.getJumpoffService().getJumpoffDmoFor(authenticate(), new DmoStoreId(sid));
            if (j != null) {
                return optionsResponse();
            } else {
                return notFound("Jumpoff metadata not available.");
            }
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * Tries to fetch the metadata of the given file.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @param path
     *        Path to the file.
     * @return Response containing the file metadata.
     */
    @GET
    @Path("/{sid}/file-metadata/{path:[a-zA-Z0-9/\\.-]*}")
    public Response getFileItemMetadataWithPath(@PathParam("sid") String sid, @PathParam("path") String path) {
        return getFileItemMetadata(sid, path, FileItemMetadata.UNIT_ID);
    }

    /**
     * Tries to fetch the Dublin-Core metadata of the given file.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @param path
     *        Path to the file.
     * @return Response containing the DC file metadata.
     */
    @GET
    @Path("/{sid}/dc-file-metadata/{path:[a-zA-Z0-9/\\.-]*}")
    public Response getDcFileItemMetadataWithPath(@PathParam("sid") String sid, @PathParam("path") String path) {
        return getFileItemMetadata(sid, path, DublinCoreMetadata.UNIT_ID);
    }

    private Response getFileItemMetadata(String sid, String path, String unitId) {
        try {
            EasyUser user = authenticate();
            Dataset d = Services.getDatasetService().getDataset(user, new DmoStoreId(sid));

            FileItem fileItem = Services.getItemService().getFileItemByPath(user, d, path);
            for (MetadataUnit metadata : fileItem.getMetadataUnits()) {
                if (metadata.getUnitId().equals(unitId)) {
                    return responseXmlOrJson(metadata.asObjectXML());
                }
            }
            return notFound("File item metadata not available for: " + path);
        }
        catch (ObjectNotAvailableException ex) {
            return notFound("Resource not available: " + path);
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (nl.knaw.dans.common.lang.xml.XMLSerializationException e) {
            return internalServerError(e);
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * A resource to address the data item roots of the given dataset.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return An response containing the root items of the given dataset.
     */
    @GET
    @Path("/{sid}/filetree")
    public Response getFileTreeRoots(@PathParam("sid") String sid) {
        try {
            EasyUser user = authenticate();
            Dataset d = Services.getDatasetService().getDataset(user, new DmoStoreId(sid));
            List<ItemVO> items = Services.getItemService().getFilesAndFolders(user, d, d.getDmoStoreId(), -1, -1, null, null);
            return responseXmlOrJson(ItemConverter.convert(items));
        }
        catch (ObjectNotAvailableException e) {
            return notFound();
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns the contents of the given folder (storeId).
     * 
     * @param sid
     *        Store ID of the dataset.
     * @param folderSid
     *        Store ID of the folder.
     * @return An response containing the contents of the given folder.
     */
    @GET
    @Path("/{sid}/filetree/" + FOLDER_SID_PREFIX + "{folderSid:[0-9]*}")
    public Response getFolderSubTree(@PathParam("sid") String sid, @PathParam("folderSid") String folderSid) {
        try {
            EasyUser user = authenticate();
            Dataset d = Services.getDatasetService().getDataset(user, new DmoStoreId(sid));
            List<ItemVO> items = Services.getItemService().getFilesAndFolders(user, d, new DmoStoreId(FOLDER_SID_PREFIX + folderSid), -1, -1, null, null);
            return responseXmlOrJson(ItemConverter.convert(items));
        }
        catch (ObjectNotAvailableException e) {
            return notFound();
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns the contents of the given folder (path).
     * 
     * @param sid
     *        Store ID of the dataset.
     * @param path
     *        Path of the folder.
     * @return An response containing the contents of the given folder.
     */
    @GET
    @Path("/{sid}/filetree/{path:[a-zA-Z0-9/\\.-]*}")
    public Response getFolderSubTreeWithPath(@PathParam("sid") String sid, @PathParam("path") String path) {
        try {
            EasyUser user = authenticate();
            Dataset d = Services.getDatasetService().getDataset(user, new DmoStoreId(sid));
            FolderItem folder = Services.getItemService().getFolderItemByPath(user, d, path);
            List<ItemVO> items = Services.getItemService().getFilesAndFolders(user, d, new DmoStoreId(folder.getStoreId()), -1, -1, null, null);
            return responseXmlOrJson(ItemConverter.convert(items));
        }
        catch (ObjectNotAvailableException e) {
            return notFound();
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns a list of store id's of thumbnails that are in the given dataset.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return A list containing id's of the thumbnails.
     */
    @GET
    @Path("/{sid}/thumbnails")
    public Response getThumbnailIds(@PathParam("sid") String sid) {
        try {
            String xml = ThumbnailUtil.getThumbnailIdsXml(authenticate(), sid);
            return responseXmlOrJson(xml);
        }
        catch (ObjectNotAvailableException e) {
            return notFound();
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns the requested thumbnail.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @param thumbnailSid
     *        Store ID of the thumbnail file.
     * @return Returns the requested thumbnail iff it actually is a thumbnail.
     */
    @GET
    @Path("/{sid}/thumbnails/{thumbnailSid}")
    public Response getThumbnail(@PathParam("sid") String sid, @PathParam("thumbnailSid") String thumbnailSid) {
        try {
            EasyUser user = authenticate();
            Dataset d = Services.getDatasetService().getDataset(user, new DmoStoreId(sid));
            FileContentWrapper fcw = Services.getItemService().getContent(user, d, new DmoStoreId(thumbnailSid));
            if (ThumbnailUtil.isThumbnail(user, d, fcw.getFileItemVO())) {
                byte[] bytes = UrlConverter.toByteArray(fcw.getURL(), fcw.getFileItemVO().getSize());
                return Response.ok(bytes, fcw.getFileItemVO().getMimetype()).build();
            } else {
                return notAuthorized();
            }
        }
        catch (ObjectNotAvailableException e) {
            return notFound();
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
        catch (IOException e) {
            return internalServerError(e);
        }
    }

    /**
     * Tries to zip the complete data of the dataset and return it.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @return Response containing the zipped content of the dataset.
     */
    @GET
    @Path("/{sid}/data")
    @Produces("application/zip")
    public Response getData(@PathParam("sid") String sid) {
        try {
            EasyUser user = authenticate();
            Dataset d = Services.getDatasetService().getDataset(user, new DmoStoreId(sid));
            List<ItemVO> rootItems = Services.getItemService().getFilesAndFolders(user, d, d.getDmoStoreId(), -1, -1, null, null);
            List<RequestedItem> requestedItems = new ArrayList<RequestedItem>();
            for (ItemVO item : rootItems) {
                requestedItems.add(new RequestedItem(item.getSid()));
            }
            File zip = Services.getItemService().getZippedContent(user, d, requestedItems).getZipFile();
            return Response.ok(zip).build();
        }
        catch (ObjectNotAvailableException e) {
            return notFound("Resource not available: " + sid);
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (IllegalArgumentException e) {
            return notFound("Not a valid id: " + sid);
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns a specific folder or file from the given dataset. Folders will be zipped.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @param path
     *        Path to the folder/file.
     * @return Response containing the requested data.
     */
    @GET
    @Path("/{sid}/data/{path:[a-zA-Z0-9/\\.-]*}")
    public Response getSpecificDataWithPath(@PathParam("sid") String sid, @PathParam("path") String path) {
        try {
            EasyUser user = authenticate();
            Dataset d = Services.getDatasetService().getDataset(user, new DmoStoreId(sid));
            try {
                return Response.ok(tryToGetAsFolder(user, d, path), "application/zip").build();
            }
            catch (ServiceException e) {
                try {
                    FileItem fileItem = Services.getItemService().getFileItemByPath(user, d, path);
                    FileContentWrapper fcw = Services.getItemService().getContent(user, d, fileItem.getDmoStoreId());
                    byte[] bytes = UrlConverter.toByteArray(fcw.getURL(), fileItem.getSize());
                    return Response.ok(bytes, fileItem.getMimeType()).build();
                }
                catch (ObjectNotAvailableException ex) {
                    return notFound("Resource not available: " + path);
                }
                catch (IOException ex) {
                    return internalServerError(e);
                }
            }
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

    private File tryToGetAsFolder(EasyUser user, Dataset d, String path) throws ServiceException {
        FolderItem folderItem = Services.getItemService().getFolderItemByPath(user, d, path);
        List<RequestedItem> requestedItems = new ArrayList<RequestedItem>();
        requestedItems.add(new RequestedItem(folderItem.getStoreId()));
        return Services.getItemService().getZippedContent(user, d, requestedItems).getZipFile();
    }

    /**
     * Returns a specific file from the dataset.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @param fileSid
     *        Store ID of the file.
     * @return Response containing the requested file.
     */
    @GET
    @Path("/{sid}/data/" + FILE_SID_PREFIX + "{fileSid:[0-9]*}")
    public Response getSpecificFileWithId(@PathParam("sid") String sid, @PathParam("fileSid") String fileSid) {
        try {
            EasyUser user = authenticate();
            Dataset d = Services.getDatasetService().getDataset(user, new DmoStoreId(sid));
            final FileContentWrapper fcw = Services.getItemService().getContent(user, d, new DmoStoreId(FILE_SID_PREFIX + fileSid));
            byte[] bytes = UrlConverter.toByteArray(fcw.getURL(), fcw.getFileItemVO().getSize());

            return Response.ok(bytes, fcw.getFileItemVO().getMimetype()).build();
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
        catch (IOException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns a specific (zipped) folder from the dataset.
     * 
     * @param sid
     *        Store ID of the dataset.
     * @param folderSid
     *        Store ID of the folder.
     * @return Response containing the requested folder.
     */
    @GET
    @Path("/{sid}/data/" + FOLDER_SID_PREFIX + "{folderSid:[0-9]*}")
    @Produces("application/zip")
    public Response getSpecificFolderWithId(@PathParam("sid") String sid, @PathParam("folderSid") String folderSid) {
        try {
            EasyUser user = authenticate();
            Dataset d = Services.getDatasetService().getDataset(user, new DmoStoreId(sid));

            ArrayList<RequestedItem> requestedItems = new ArrayList<RequestedItem>();
            requestedItems.add(new RequestedItem(FOLDER_SID_PREFIX + folderSid));
            File zip = Services.getItemService().getZippedContent(user, d, requestedItems).getZipFile();
            return Response.ok(zip).build();
        }
        catch (CommonSecurityException e) {
            return notAuthorized();
        }
        catch (ServiceException e) {
            return internalServerError(e);
        }
    }

}
