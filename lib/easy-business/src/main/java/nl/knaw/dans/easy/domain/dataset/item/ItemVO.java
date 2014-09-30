package nl.knaw.dans.easy.domain.dataset.item;

import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.easy.domain.model.Dataset;

public interface ItemVO extends Cloneable {

    String getSid();

    void setSid(String pid);

    String getParentSid();

    void setParentSid(String parentSid);

    String getName();

    void setName(String name);

    String getDatasetSid();

    void setDatasetSid(String datasetSid);

    void setPath(String path);

    String getPath();

    boolean belongsTo(Dataset dataset);

    Object clone() throws CloneNotSupportedException;

    AuthzStrategy getAuthzStrategy();

    String getAutzStrategyName();
}
