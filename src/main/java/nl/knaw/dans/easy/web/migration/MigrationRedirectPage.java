package nl.knaw.dans.easy.web.migration;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Driven by urls starting with '/dms', redirects to pages similar to the ones served by Easy I.
 * <p/>
 * Redirects:
 * <pre>
 *   /dms?command=home
 *   /dms?command=loginForm
 *   /dms?command=AIP_info&aipId=twips.dans.knaw.nl--5253144779629921964-1225444224296
 *   
 *   /dms?command=show&query=Analecta
 *   /dms?command=myContents
 * </pre>
 *
 */
public class MigrationRedirectPage extends AbstractEasyPage
{

    public static final String PM_COMMAND = "command";
    public static final String PM_AIP_ID = "aipId";
    public static final String PM_SEARCH_QUERY = "query";

    public static final String URL_PERSISTENT_IDENTIFIER = "http://www.persistent-identifier.nl/?identifier=";

    public static final String URL_HOME = "/home";
    public static final String URL_LOGIN = "/login";
    public static final String URL_DATASET_VIEW = "/view/datasetId/";
    public static final String URL_PUBLIC_SEARCH = "/?wicket:bookmarkablePage=:nl.knaw.dans.easy.web.search.pages.PublicSearchResultPage&q=";
    public static final String URL_MY_DATSETS = "/mydatasets";

    private static final Logger logger = LoggerFactory.getLogger(MigrationRedirectPage.class);

    public MigrationRedirectPage(final PageParameters parameters)
    {
        super(parameters);
        String command = parameters.getString(PM_COMMAND);

        if ("home".equals(command))
        {
            throw new RestartResponseException(new RedirectPage(URL_HOME));
        }
        else if ("loginForm".equals(command))
        {
            throw new RestartResponseException(new RedirectPage(URL_LOGIN));
        }
        else if ("AIP_info".equals(command))
        {
            redirectToDatasetViewPage(parameters);
        }
        else if ("show".equals(command))
        {
            redirectToSearchPage(parameters);
        }
        else if ("myContents".equals(command))
        {
            throw new RestartResponseException(new RedirectPage(URL_MY_DATSETS));
        }
        else
        {
            errorMessage(EasyResources.NOT_FOUND, parameters.toString());
            throw new InternalWebError();
        }

    }

    private void redirectToDatasetViewPage(PageParameters parameters)
    {
        String aipId = parameters.getString(PM_AIP_ID);
        if (StringUtils.isBlank(aipId))
        {
            errorMessage(EasyResources.NOT_FOUND, "aipId = [null]");
            throw new InternalWebError();
        }
        try
        {
            IdMap idMap = Services.getMigrationService().getMostRecentByAipId(aipId);
            if (idMap == null)
            {
                errorMessage(EasyResources.NOT_FOUND, "aipId = " + aipId);
                throw new InternalWebError();
            }
            String href = URL_PERSISTENT_IDENTIFIER + idMap.getPersistentIdentifier();
            DatasetViewPage datasetViewPage = new DatasetViewPage(idMap.getStoreId(), null);
            warningMessage(EasyResources.USE_PERSITENT_IDENTIFIER, href);
            logger.debug("Redirecting to " + DatasetViewPage.class.getName());
            setResponsePage(datasetViewPage);
        }
        catch (ServiceException e)
        {
            errorMessage(EasyResources.INTERNAL_ERROR);
            logger.error("Unable to redirect: ", e);
            throw new InternalWebError();
        }

    }

    private void redirectToSearchPage(PageParameters parameters)
    {
        String query = parameters.getString(PM_SEARCH_QUERY, "");
        String url = URL_PUBLIC_SEARCH + query;
        throw new RestartResponseException(new RedirectPage(url));
    }

}
