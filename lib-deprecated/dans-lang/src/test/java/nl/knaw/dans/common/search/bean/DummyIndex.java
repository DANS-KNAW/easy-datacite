package nl.knaw.dans.common.search.bean;

import nl.knaw.dans.common.lang.search.Index;

public class DummyIndex implements Index {
    private static final long serialVersionUID = 8694020343426156561L;

    public String getName() {
        return "dummyIndex";
    }

    public String getPrimaryKey() {
        return DummySB.ID_NAME;
    }

}
