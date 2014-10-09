package nl.knaw.dans.easy.domain.dataset;

import java.net.URI;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.DatasetItemContainerMetadata;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public class ItemContainerMetadataImpl extends AbstractItemMetadataImpl<DatasetItemContainerMetadata> implements DatasetItemContainerMetadata {

    /**
     * The version - when newly instantiated. The actual version of an instance as read from an xml-stream might be obtained by {@link #getVersion()}.
     */
    public static final String VERSION = "0.1";

    private static final long serialVersionUID = -396869998619454854L;

    @SuppressWarnings("unused") // required for deserialisation by JiBX
    private int totalFileCount;
    @SuppressWarnings("unused") // required for deserialisation by JiBX
    private int childFileCount;

    @SuppressWarnings("unused") // required for deserialisation by JiBX
    private int totalFolderCount;
    @SuppressWarnings("unused") // required for deserialisation by JiBX
    private int childFolderCount;

    @SuppressWarnings("unused") // required for deserialisation by JiBX
    private int[] creatorRoleArray = null;

    @SuppressWarnings("unused") // required for deserialisation by JiBX
    private int[] visibleToArray = null;

    @SuppressWarnings("unused") // required for deserialisation by JiBX
    private int[] accessibleToArray = null;

    private String version;

    protected ItemContainerMetadataImpl() {
        super();
    }

    public ItemContainerMetadataImpl(DmoStoreId sid) {
        super(sid);
    }

    public String getVersion() {
        if (version == null) {
            version = VERSION;
        }
        return version;
    }

    // ///////////////////////////////////////////
    protected void setNop(List<?> list) {
        // needed for jiBx deserialization
    }

    @SuppressWarnings("unused") // required for deserialisation by JiBX
    private List<CreatorRole> getCreatorRoles() {
        return null;
    }

    @SuppressWarnings("unused") // required for deserialisation by JiBX
    private List<VisibleTo> getVisibleToList() {
        return null;
    }

    @SuppressWarnings("unused") // required for deserialisation by JiBX
    private List<AccessibleTo> getAccessibleToList() {
        return null;
    }

    // ////////////////////////////////////////////

    public String getUnitFormat() {
        return UNIT_FORMAT;
    }

    public URI getUnitFormatURI() {
        return UNIT_FORMAT_URI;
    }

    public String getUnitId() {
        return UNIT_ID;
    }

    public String getUnitLabel() {
        return UNIT_LABEL;
    }

}
