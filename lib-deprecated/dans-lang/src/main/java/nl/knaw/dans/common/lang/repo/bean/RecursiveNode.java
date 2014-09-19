package nl.knaw.dans.common.lang.repo.bean;

import java.io.Serializable;
import java.util.List;

public interface RecursiveNode extends Serializable {

    List<RecursiveEntry> getChildren();

    void setChildren(List<RecursiveEntry> children);

    RecursiveEntry get(String key);

    void add(RecursiveEntry entry);

}
