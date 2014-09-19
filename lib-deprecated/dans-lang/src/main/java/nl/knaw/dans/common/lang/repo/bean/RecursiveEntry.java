package nl.knaw.dans.common.lang.repo.bean;

public interface RecursiveEntry extends Comparable<RecursiveEntry>, RecursiveNode {

    String getKey();

    void setKey(String key);

    String getShortname();

    void setShortname(String shortname);

    String getName();

    void setName(String name);

    int getOrdinal();

    void setOrdinal(int ordinal);

    int compareTo(RecursiveEntry re);

}
