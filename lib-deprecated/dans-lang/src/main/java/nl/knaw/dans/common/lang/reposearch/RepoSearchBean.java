package nl.knaw.dans.common.lang.reposearch;

import java.io.Serializable;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.exception.NoStoreAttachedException;
import nl.knaw.dans.common.lang.search.bean.annotation.SearchField;

/**
 * Search beans that refer to data model object entities can use this base class for uniform unique identification.
 * 
 * @author lobo
 */
public abstract class RepoSearchBean implements Serializable {
    private static final long serialVersionUID = 8454438648941755394L;

    public static final String SID_FIELD = "sid";
    @SearchField(name = SID_FIELD, required = true)
    private String storeId;

    public static final String STORE_NAME_FIELD = "repository_id";
    @SearchField(name = STORE_NAME_FIELD, required = true)
    private String storeName;

    public void setPropertiesByDmo(DataModelObject dmo) {
        setStoreId(dmo.getStoreId());
        try {
            setStoreName(dmo.getStoreName());
        }
        catch (NoStoreAttachedException e) {
            setStoreName(null);
        }
    }

    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String sid) {
        this.storeId = sid;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public void setStoreName(String repository) {
        this.storeName = repository;
    }

    @Override
    public String toString() {
        return super.toString() + "[sid=" + storeId + ", repositoryId=" + storeName + "]";
    }
}
