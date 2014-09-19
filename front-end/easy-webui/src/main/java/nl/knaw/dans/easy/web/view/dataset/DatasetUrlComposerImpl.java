package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.easy.servicelayer.DatasetNotification;
import nl.knaw.dans.easy.servicelayer.DatasetUrlComposer;
import nl.knaw.dans.easy.web.PageBookmark;
import nl.knaw.dans.easy.web.search.pages.MyDatasetsSearchResultPage;

import org.apache.wicket.IPageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.protocol.http.WebRequest;

public class DatasetUrlComposerImpl implements DatasetUrlComposer {

    private final IPageMap pageMap;
    private final String baseUrl;
    private static DatasetUrlComposer instance = null;

    private DatasetUrlComposerImpl(final IPageMap pageMap) {
        this.pageMap = pageMap;
        final WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();

        // TODO will this always be just host and port?
        baseUrl = webRequest.getHttpServletRequest().getRequestURL().toString();
    }

    /**
     * Gets an instance to inject into {@link DatasetNotification}.<br>
     * TODO rather at session or even application level, but called by onBeforeRender of pages that might send e-mails about datasets.<br>
     * We get indefinite recursion when called by EasyWicketApplication.newSession with argument: session.getDefaultPageMap()
     * 
     * @param pageMap
     *        we need a page map but don't care about which one
     * @return
     */
    public static DatasetUrlComposer getInstance(final IPageMap pageMap) {
        if (instance == null) {
            instance = new DatasetUrlComposerImpl(pageMap);
        }
        return instance;
    }

    public String getUrl(final String storeId) {
        return createUrl(createParameters(storeId));
    }

    public String getPermissionUrl(final String storeId) {
        return createUrl(createParameters(storeId, DatasetViewPage.RI_TAB_PERMISSIONS));
    }

    public String getFileExplorerUrl(final String storeId) {
        return createUrl(createParameters(storeId, DatasetViewPage.RI_TAB_FILEEXPLORER));
    }

    private PageParameters createParameters(final String storeId, final String string) {
        final PageParameters parameters = createParameters(storeId);
        parameters.add(DatasetViewPage.PM_TAB_INDEX, string);
        return parameters;
    }

    private PageParameters createParameters(final String storeId) {
        final PageParameters parameters = new PageParameters();
        parameters.add(DatasetViewPage.PM_DATASET_ID, storeId);
        return parameters;
    }

    private String createUrl(final PageParameters parameters) {
        final CharSequence url = RequestCycle.get().urlFor(pageMap, DatasetViewPage.class, parameters);
        return RequestUtils.toAbsolutePath(baseUrl, url.toString());
    }

    public String getMyDatasetsUrl(final String storeId) {
        final PageBookmark bookmark = PageBookmark.valueOf(MyDatasetsSearchResultPage.class);
        return RequestUtils.toAbsolutePath(baseUrl, bookmark.getAlias());
    }

}
