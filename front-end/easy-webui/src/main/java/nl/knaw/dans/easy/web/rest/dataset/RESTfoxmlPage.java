package nl.knaw.dans.easy.web.rest.dataset;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.rest.RESTdisseminationPage;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AbortException;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTfoxmlPage extends RESTdisseminationPage {

    public static final String NAME = "foxml";
    public static final String RESOURCE_KEY = "rest.datasets.foxml";

    private static final Logger logger = LoggerFactory.getLogger(RESTfoxmlPage.class);

    public RESTfoxmlPage(PageParameters parameters) {
        super(parameters);
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
    protected void disseminate() {
        Dataset dataset = (Dataset) getPageParameters().get(RESTdatasetsPage.PM_DATASET);
        byte[] xml;
        try {
            xml = Services.getDatasetService().getObjectXml(EasySession.getSessionUser(), dataset);
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

        String filename = StringUtils.left(dataset.getLabel(), 10) + "_fo.xml";
        try {
            writeXml(filename, xml);
            throw new AbortException();
        }
        catch (IOException e) {
            logger.error("Unable to disseminate: ", e);
            throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

}
