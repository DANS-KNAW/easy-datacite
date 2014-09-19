package nl.knaw.dans.common.wicket.components.pagebrowse;

import java.io.Serializable;

import nl.knaw.dans.common.wicket.components.pagebrowse.PageBrowsePanel.PageBrowseLink;

public interface PageBrowseLinkListener extends Serializable {
    void onClick(PageBrowseLink link);
}
