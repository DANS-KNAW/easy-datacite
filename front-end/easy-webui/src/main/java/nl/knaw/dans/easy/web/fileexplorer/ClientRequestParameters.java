package nl.knaw.dans.easy.web.fileexplorer;

import java.net.MalformedURLException;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.model.*;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.web.EasySession;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequest;
import org.slf4j.*;

/**
 * @author Joke Pol
 */
class ClientRequestParameters {
    private static final Logger logger = LoggerFactory.getLogger(ClientRequestParameters.class);

    private static final String DATASET_SID = "datasetSid";

    // update
    static final String SIDS_TO_UPDATE = "dataToUpdate";

    static enum UpdateParameters {
        newAccessibleTo, newVisibleTo, newName, delete
    };

    static enum FilterParameters {
        accessibleToFilter, visibleToFilter, creatorFilter
    };

    // fetch
    static final String WANTED_SID = "sid";
    static final String FETCH = "data";
    static final String FILES_AND_FOLDERS = "directoriesAndFilesTable";
    static final String FOLDERS = "directoriesTree";

    private final Map<String, String[]> parameterMap;

    /**
     * @param parameterMap
     *        key-value pairs of the request arguments
     */
    ClientRequestParameters(final Map<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
    }

    /**
     * creates an instance from the request in the RequestCycle
     * 
     * @throws MalformedURLException
     *         should not happen as the url is extracted from the request
     */
    @SuppressWarnings("unchecked")
    ClientRequestParameters() {
        final WebRequest webRequest = ((WebRequest) RequestCycle.get().getRequest());
        try {
            new java.net.URL(webRequest.getHttpServletRequest().getHeader("Referer")).getPath();
        }
        catch (MalformedURLException e) {
            logger.error("Referer in HttpServletRequest header", e);
        }
        this.parameterMap = webRequest.getHttpServletRequest().getParameterMap();
    }

    public String getWantedSid() {
        return get(WANTED_SID);
    }

    public DmoStoreId getDatasetSid() {
        return new DmoStoreId(getOptional(DATASET_SID));
    }

    public boolean getWantFilesAndFolders() {
        return get(FETCH).equals(FILES_AND_FOLDERS);
    }

    @SuppressWarnings("unchecked")
    String getItemsToUpdate() {
        return get(SIDS_TO_UPDATE);
    }

    boolean deleteRequested() {
        return null != getOptional(UpdateParameters.delete.name());
    }

    VisibleTo getNewVisibleToValue() {
        String value = getOptional(UpdateParameters.newVisibleTo.name());
        return value == null ? null : VisibleTo.valueOf(value);
    }

    public AccessibleTo getNewAccessibleToValue() {
        String value = getOptional(UpdateParameters.newAccessibleTo.name());
        return value == null ? null : AccessibleTo.valueOf(value);
    }

    public String getNewName() {
        return getOptional(UpdateParameters.newName.name());
    }

    ItemFilters getItemFilters(final EasySession easySession) {
        final EasyUser user = easySession.getUser();
        Dataset dataset = null;
        try {
            dataset = (Dataset) easySession.getDataset(getDatasetSid());
        }
        catch (final ServiceException exception) {
            logger.error("dataset not found, filter won't recognize depositor", exception);
        }
        return ItemFilters.get(user, dataset, getDesiredFilters());
    }

    ItemFilters getDesiredFilters() {
        final String creators = getOptional(FilterParameters.creatorFilter.name());
        final String visibleTos = getOptional(FilterParameters.visibleToFilter.name());
        final String accessibleTos = getOptional(FilterParameters.accessibleToFilter.name());
        return new ItemFilters(visibleTos, creators, accessibleTos);
    }

    private String get(String key) {
        if (!parameterMap.containsKey(key))
            throw new IllegealRequestArgument("Missing argument " + key);
        if (parameterMap.get(key).length != 1)
            throw new IllegealRequestArgument("Duplicate argument " + key);
        return parameterMap.get(key)[0];
    }

    static class IllegealRequestArgument extends IllegalArgumentException {

        /**
         *
         */
        private static final long serialVersionUID = 1333745082690101989L;

        public IllegealRequestArgument(String string) {
            super(string);
        }

    }

    private String getOptional(String key) {
        if (!parameterMap.containsKey(key))
            return null;
        return get(key);
    }
}
