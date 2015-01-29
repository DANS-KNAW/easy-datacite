package nl.knaw.dans.easy.domain.dataset.item;

import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.easy.domain.model.Dataset;

public abstract class AbstractItemVO implements ItemVO {

    private static final long serialVersionUID = 4141978905361852147L;
    private String sid;
    private String parentSid;
    private String datasetSid;
    private String name;
    private String path;

    private AuthzStrategy authzStrategy;

    public AbstractItemVO() {}

    public AbstractItemVO(String pid, String parentSid, String datasetSid, String name) {
        this.sid = pid;
        this.parentSid = parentSid;
        this.datasetSid = datasetSid;
        this.name = name;
    }

    public String getSid() {
        return this.sid;
    }

    public void setSid(String pid) {
        this.sid = pid;
    }

    public String getParentSid() {
        return this.parentSid;
    }

    public void setParentSid(String parentSid) {
        this.parentSid = parentSid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatasetSid() {
        return datasetSid;
    }

    public void setDatasetSid(String datasetSid) {
        this.datasetSid = datasetSid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean belongsTo(Dataset dataset) {
        boolean belongsTo = false;
        if (dataset != null && getDatasetSid() != null) {
            belongsTo = getDatasetSid().equals(dataset.getStoreId());
        }
        return belongsTo;
    }

    @Override
    public AuthzStrategy getAuthzStrategy() {
        if (authzStrategy == null) {
            throw new IllegalStateException("No AuthzStrategy set on this ItemVO.");
        }
        return authzStrategy;
    }

    public void setAuthzStrategy(AuthzStrategy authzStrategy) {
        this.authzStrategy = authzStrategy;
    }

    @Override
    public int hashCode() {
        return parentSid.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractItemVO) {
            AbstractItemVO a = (AbstractItemVO) o;
            //@formatter:off
            return sid.equals(a.sid) 
                && path.equals(a.path) 
                && parentSid.equals(a.parentSid) 
                && name.equals(a.name) 
                && datasetSid.equals(a.datasetSid);
            //@formatter:on
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("[class=%s, sid=%s, path=%s]", getClass().getSimpleName(), sid, path);
    }
}
