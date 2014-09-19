package nl.knaw.dans.easy.web.rest.dataset;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.rest.RESTdisseminationPage;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;

import org.apache.wicket.AbortException;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTfilesContentPage extends RESTdisseminationPage {

    public static final String NAME = "content";
    public static final String RESOURCE_KEY = "rest.datasets.files.content";

    private static final Logger logger = LoggerFactory.getLogger(RESTfilesContentPage.class);

    public RESTfilesContentPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void disseminate() {
        Dataset dataset = (Dataset) getPageParameters().get(RESTdatasetsPage.PM_DATASET);
        FileItem fileItem = (FileItem) getPageParameters().get(RESTfilesPage.PM_FILE_ITEM);
        URL url;
        try {
            url = Services.getItemService().getFileContentURL(EasySession.getSessionUser(), dataset, fileItem);
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

        try {
            write(fileItem, url.openStream());
            throw new AbortException();
        }
        catch (IOException e) {
            logger.error("Unable to disseminate: ", e);
            throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getResourceKey() {
        return RESOURCE_KEY;
    }

}
