package nl.knaw.dans.easy.domain.dataset.item;

public enum ItemOrderField {
    NAME(new Class[] {FileItemVO.class, FolderItemVO.class}, "name"),

    SIZE(new Class[] {FileItemVO.class}, "size"),

    CREATORROLE(new Class[] {FileItemVO.class}, "creatorRole"),

    VISIBLETO(new Class[] {FileItemVO.class}, "visibleTo"),

    ACCESSIBLETO(new Class[] {FileItemVO.class}, "accessibleTo");

    @SuppressWarnings("unchecked")
    public Class[] voTypes;

    public String propertyName;

    @SuppressWarnings("unchecked")
    ItemOrderField(Class[] voTypes, String propertyName) {
        this.voTypes = voTypes;
        this.propertyName = propertyName;
    }

}
