package nl.knaw.dans.easy.domain.dataset.item.filter;

import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;

public enum ItemFilterField
{

    VISIBLETO(new Class[] {FileItemVO.class}, VisibleToFieldFilter.class, "visibleTo", "visibleToList"),

    ACCESSBLETO(new Class[] {FileItemVO.class}, AccessibleToFieldFilter.class, "accessibleTo", "accessibleToList"),

    CREATORROLE(new Class[] {FileItemVO.class}, CreatorRoleFieldFilter.class, "creatorRole", "creatorRoles");

    @SuppressWarnings("unchecked")
    public Class[] voTypes;

    @SuppressWarnings("unchecked")
    public Class<? extends ItemFilter> fieldFilter;

    /**
     * The name of this property in the FileItemVO 
     */
    public String filePropertyName;

    /**
     * The name of set of this property in the FolderItemVO
     */
    public String folderSetPropertyName;

    @SuppressWarnings("unchecked")
    ItemFilterField(Class[] voTypes, Class<? extends ItemFieldFilter> fieldFilter, String propertyName, String folderSetPropertyName)
    {
        this.voTypes = voTypes;
        this.fieldFilter = fieldFilter;
        this.filePropertyName = propertyName;
        this.folderSetPropertyName = folderSetPropertyName;
    }

}
