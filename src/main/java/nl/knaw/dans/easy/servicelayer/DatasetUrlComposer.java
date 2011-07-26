/**
 * 
 */
package nl.knaw.dans.easy.servicelayer;

public interface DatasetUrlComposer
{
    String getUrl(final String storeId);
    String getPermissionUrl(final String storeId);
    String getFileExplorerUrl(final String storeId);
    String getMyDatasetsUrl(final String storeId);
}