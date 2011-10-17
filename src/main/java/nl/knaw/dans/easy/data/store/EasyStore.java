package nl.knaw.dans.easy.data.store;

import java.net.URL;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStore;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;

import org.joda.time.DateTime;

public interface EasyStore extends DmoStore
{

    public enum RepositoryState
    {
        /**
         * Indicates a Digital Object is <i>Active</i>.
         */
        Active("A"),
        /**
         * Indicates a Digital Object is <i>Inactive</i>.
         */
        Inactive("I"),
        /**
         * Indicates a Digital Object is <i>Deleted</i>.
         */
        Deleted("D");

        public final String code;

        private RepositoryState(String code)
        {
            this.code = code;
        }
    }
    
    EasyMetadata getEasyMetaData(String sid, DateTime asOfDateTime) throws RepositoryException;

    DownloadHistory findDownloadHistoryFor(DataModelObject objectDmo, String period) throws RepositoryException;

    DownloadHistory findDownloadHistoryFor(String objectStoreId, String period) throws RepositoryException;

    List<DownloadHistory> findDownloadHistoryFor(DataModelObject dmo) throws RepositoryException;
    
    URL getFileURL(String sid);
    
    URL getDescriptiveMetadataURL(String sid);

    URL getStreamURL(String storeId, String streamId);

}
