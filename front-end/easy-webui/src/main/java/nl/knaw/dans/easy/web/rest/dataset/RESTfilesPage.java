package nl.knaw.dans.easy.web.rest.dataset;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.rest.PageDescription;
import nl.knaw.dans.common.wicket.rest.RESTcascadePage;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;

public class RESTfilesPage extends RESTcascadePage {
    public static final String NAME = "files";
    public static final String RESOURCE_KEY = "rest.datasets.files";

    public static final String PM_FILE_ITEM = "fileItem";

    private static Map<String, PageDescription> CHILDREN;

    private static final int LEVEL = 4;

    public RESTfilesPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public Map<String, PageDescription> getChildren() {
        if (CHILDREN == null) {
            CHILDREN = new LinkedHashMap<String, PageDescription>();

            String name = RESTfilesContentPage.NAME;
            String resourceKey = RESTfilesContentPage.RESOURCE_KEY;
            PageDescription description = new PageDescription(name, resourceKey, RESTfilesContentPage.class);
            CHILDREN.put(name, description);

        }
        return CHILDREN;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getResourceKey() {
        return RESOURCE_KEY;
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    protected void contributeParameters(PageParameters parameters) {
        parameters.put(PM_FILE_ITEM, getFileItem());
    }

    protected FileItem getFileItem() {
        FileItem fileItem;
        Dataset dataset = (Dataset) getPageParameters().get(RESTdatasetsPage.PM_DATASET);
        try {
            fileItem = Services.getItemService().getFileItem(EasySession.getSessionUser(), dataset, new DmoStoreId(getFileItemId()));
        }
        catch (ObjectNotAvailableException e) {
            throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (CommonSecurityException e) {
            throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch (ServiceException e) {
            throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return fileItem;
    }

    protected String getFileItemId() {
        String storeId;
        String itemId = getUrlFragments()[LEVEL];
        if (itemId.startsWith(FileItem.NAMESPACE.getValue())) {
            storeId = itemId;
        } else {
            storeId = FileItem.NAMESPACE.getValue() + ":" + itemId;
        }
        return storeId;
    }

}
