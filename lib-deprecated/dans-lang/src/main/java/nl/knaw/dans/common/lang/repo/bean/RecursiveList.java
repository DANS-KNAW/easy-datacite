package nl.knaw.dans.common.lang.repo.bean;

import nl.knaw.dans.common.lang.xml.XMLBean;

public interface RecursiveList extends XMLBean, RecursiveNode
{

    String getListId();

    void setListId(String listId);

}
