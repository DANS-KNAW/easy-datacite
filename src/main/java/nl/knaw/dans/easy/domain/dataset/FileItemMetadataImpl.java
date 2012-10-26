package nl.knaw.dans.easy.domain.dataset;

import java.net.URI;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.FileItemMetadata;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.xml.AdditionalMetadata;

public class FileItemMetadataImpl extends AbstractItemMetadataImpl<FileItemMetadata> implements FileItemMetadata
{

    /**
     * The version - when newly instantiated. The actual version of an instance as read from an xml-stream might be
     * obtained by {@link #getVersion()}.
     */
    public static final String VERSION = "0.1";

    private static final long serialVersionUID = -6117686733532197916L;

    private String version;

    private CreatorRole creatorRole;
    private VisibleTo visibleTo;
    private AccessibleTo accessibleTo;
    private String mimeType = MIMETYPE_UNDEFINED;
    private long size;

    private AdditionalMetadata additionalMetadata;

    protected FileItemMetadataImpl()
    {
        super();
    }

    public FileItemMetadataImpl(DmoStoreId sid)
    {
        super(sid);
    }

    public String getVersion()
    {
        if (version == null)
        {
            version = VERSION;
        }
        return version;
    }

    public CreatorRole getCreatorRole()
    {
        return creatorRole;
    }

    public boolean setCreatorRole(CreatorRole creatorRole)
    {
        boolean dirty = evaluateDirty(creatorRole, this.creatorRole);
        this.creatorRole = creatorRole;
        return dirty;
    }

    public VisibleTo getVisibleTo()
    {
        return visibleTo;
    }

    public boolean setVisibleTo(VisibleTo visibleTo)
    {
        boolean dirty = evaluateDirty(visibleTo, this.visibleTo);
        this.visibleTo = visibleTo;
        return dirty;
    }

    public AccessibleTo getAccessibleTo()
    {
        return accessibleTo;
    }

    public boolean setAccessibleTo(AccessibleTo accessibleTo)
    {
        boolean dirty = evaluateDirty(accessibleTo, this.accessibleTo);
        this.accessibleTo = accessibleTo;
        return dirty;
    }

    public String getMimeType()
    {
        if (mimeType == null)
        {
            mimeType = MIMETYPE_UNDEFINED;
        }
        return mimeType;
    }

    protected void setMimeType(String mimeType)
    {
        evaluateDirty(mimeType, this.mimeType);
        this.mimeType = mimeType;
    }

    public long getSize()
    {
        return size;
    }

    protected void setSize(long size)
    {
        evaluateDirty(size, this.size);
        this.size = size;
    }

    public String getUnitFormat()
    {
        return UNIT_FORMAT;
    }

    public URI getUnitFormatURI()
    {
        return UNIT_FORMAT_URI;
    }

    public String getUnitId()
    {
        return UNIT_ID;
    }

    public String getUnitLabel()
    {
        return UNIT_LABEL;
    }

    public AdditionalMetadata getAdditionalMetadata()
    {
        if (additionalMetadata == null)
        {
            additionalMetadata = new AdditionalMetadata();
        }
        return additionalMetadata;
    }

    public void setAdditionalMetadata(AdditionalMetadata additionalMetadata)
    {
        this.additionalMetadata = additionalMetadata;
    }

}
