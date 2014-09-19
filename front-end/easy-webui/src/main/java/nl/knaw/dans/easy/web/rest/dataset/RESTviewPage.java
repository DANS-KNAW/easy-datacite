package nl.knaw.dans.easy.web.rest.dataset;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.common.wicket.rest.RESTdisseminationPage;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;

public class RESTviewPage extends RESTdisseminationPage {

    public static final String NAME = "view";
    public static final String RESOURCE_KEY = "rest.datasets.view";

    public RESTviewPage(PageParameters parameters) {
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
        PageParameters parameters = getPageParameters();
        Dataset dataset = (Dataset) parameters.get(RESTdatasetsPage.PM_DATASET);
        if (dataset == null) {
            throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }
        String targetUrl = DatasetViewPage.urlFor(dataset.getStoreId(), 0, false, this);
        throw new RestartResponseException(new RedirectPage(targetUrl));
    }

}
