package nl.knaw.dans.easy.domain.dataset.item;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;

public class RequestedItem {
    private final String storeId;
    private final boolean filesOnly;

    /**
     * @param item
     *        storeId collected by the client, id's for folders may be extended with "/*". The extension indicates just the files of the folder are desired.
     *        without this extensions both files and folders are desired.
     */
    public RequestedItem(final String item) {
        String[] itemParts = item.split("/");
        this.storeId = itemParts[0];
        filesOnly = itemParts.length > 1 && itemParts[1].equals("*");
    }

    public String getStoreId() {
        return storeId;
    }

    public boolean isDataset() {
        return hasNameSpace(Dataset.NAMESPACE);
    }

    public boolean isFolder() {
        return hasNameSpace(FolderItem.NAMESPACE);
    }

    public boolean isFile() {
        return hasNameSpace(FileItem.NAMESPACE);
    }

    private boolean hasNameSpace(DmoNamespace namespace) {
        // not including the ":" in the test, allows to extend the name space with Dummy for testing
        return storeId.startsWith(namespace.getValue());
    }

    public boolean filesOnly() {
        return filesOnly;
    }

}
