package nl.knaw.dans.easy.domain.dataset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.BinaryUnit;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Constants;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.DatasetItemMetadata;
import nl.knaw.dans.easy.domain.model.DescriptiveMetadata;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileItemMetadata;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.xml.AdditionalMetadata;

import org.dom4j.Element;

public class FileItemImpl extends AbstractDatasetItemImpl implements FileItem
{

    private static final long                serialVersionUID = -1213485923457997519L;
    
    private EasyFile easyFile;
    private FileItemMetadataImpl fileItemMetadata;
    private DescriptiveMetadata descriptiveMetadata;
    
	private DublinCoreMetadata dc;
        
    public FileItemImpl(String storeId)
    {
        super(storeId);
    }
    
    public DmoNamespace getDmoNamespace()
    {
        return NAMESPACE;
    }

    @Override
    public String getLabel()
    {
        String label = super.getLabel();
        if (label == null && easyFile != null)
        {
            label = easyFile.getUnitLabel();
            super.setLabel(label);
        }
        return label;
    }
    
    public void setLabel(String label)
    {
        //evaluateDirty(label, getLabel());
        super.setLabel(label);
        getFileItemMetadataImpl().setName(label);
    }

    public String getMimeType()
    {
        return getFileItemMetadata().getMimeType();
    }
    
    public void setMimeType(String mimeType)
    {
        getFileItemMetadataImpl().setMimeType(mimeType);
    }

    public long getSize()
    {
        return getFileItemMetadata().getSize();
    }
    
    public void setSize(long size)
    {
        getFileItemMetadataImpl().setSize(size);
    }

    public File getFile()
    {
        File file = null;
        if (easyFile != null)
        {
            file = easyFile.getFile();
        }
        return file;
    }

    public void setFile(File file) throws IOException
    {
        if (file == null)
        {
            this.easyFile = null;
            setMimeType(null);
            setSize(0);
        }
        else
        {
            easyFile = new EasyFile();
            easyFile.setFile(file);
            setMimeType(easyFile.getMimeType());
            setSize(easyFile.getFileSize());
            super.setLabel(easyFile.getUnitLabel());
            getFileItemMetadataImpl().setName(easyFile.getUnitLabel());
        }
    }

    public DublinCoreMetadata getDublinCoreMetadata()
    {
        if (dc == null)
        {
        	dc = new JiBXDublinCoreMetadata();
        }
        List<String> label = new ArrayList<String>(1);
        label.add(getLabel());
        dc.setTitle(label);
        List<String> mimeType = new ArrayList<String>(1);
        mimeType.add(getMimeType());
        dc.setType(mimeType);
        dc.setDirty(this.isDirty());

        return dc;
    }
    
    public DatasetItemMetadata getDatasetItemMetadata()
    {
        return getFileItemMetadataImpl();
    }
    
    public FileItemMetadata getFileItemMetadata()
    {
        return getFileItemMetadataImpl();
    }
    
    @Override
    public AdditionalMetadata getAdditionalMetadata()
    {
        return getFileItemMetadata().getAdditionalMetadata();
    }
    
    @Override
    public void setAdditionalMetadata(AdditionalMetadata additionalMetadata)
    {
        getFileItemMetadata().setAdditionalMetadata(additionalMetadata);
    }
    
    private FileItemMetadataImpl getFileItemMetadataImpl()
    {
        if (fileItemMetadata == null)
        {
            fileItemMetadata = new FileItemMetadataImpl(getStoreId());
        }
        fileItemMetadata.setSid(getStoreId());
        return fileItemMetadata;
    }
    
    /**
     * DO NOT USE. Needed for deserialization in Store.
     * @param fileItemMetadata FileItemMetadataImpl
     */
    public void setFileItemMetadata(FileItemMetadataImpl fileItemMetadata)
    {
        this.fileItemMetadata = fileItemMetadata;
        setLabel(fileItemMetadata.getName());
        setDirty(false);
    }
    
    @Override
    public List<BinaryUnit> getBinaryUnits()
    {
        List<BinaryUnit> binaryUnits = super.getBinaryUnits();
        if (easyFile != null)
        {
            binaryUnits.add(easyFile);
        }
        return binaryUnits;
    }
    
    public List<MetadataUnit> getMetadataUnits()
    {
        List<MetadataUnit> metadataUnits = super.getMetadataUnits();
        
        metadataUnits.add(getDublinCoreMetadata());
        metadataUnits.add(getFileItemMetadata());
        if (hasDescriptiveMetadata())
        {
            metadataUnits.add(getDescriptiveMetadata());
        }     
        return metadataUnits;
    }

    public boolean isDeletable()
    {
        return true;
    }
        
    public CreatorRole getCreatorRole()
    {
        return getFileItemMetadata().getCreatorRole();
    }
    
    public void setCreatorRole(CreatorRole creatorRole)
    {
        CreatorRole previous = getFileItemMetadata().getCreatorRole();
        boolean changed = getFileItemMetadata().setCreatorRole(creatorRole);
        if (changed)
        {
            DatasetItemContainer parent = (DatasetItemContainer) getParent();
            if (parent != null)
            {
                parent.onDescendantStateChange(previous, creatorRole);
            }
        }
    }
    
    public boolean isCreatedByArchivist()
    {
        return CreatorRole.ARCHIVIST.equals(getFileItemMetadata().getCreatorRole());
    }
    
    @Override
    public boolean isCreatedByDepositor()
    {
        return CreatorRole.DEPOSITOR.equals(getFileItemMetadata().getCreatorRole());
    }
    
    public VisibleTo getVisibleTo()
    {
        return getFileItemMetadata().getVisibleTo();
    }
    
    public void setVisibleTo(VisibleTo visibleTo)
    {
        VisibleTo previous = getFileItemMetadata().getVisibleTo();
        boolean changed = getFileItemMetadata().setVisibleTo(visibleTo);
        if (changed)
        {
            DatasetItemContainer parent = (DatasetItemContainer) getParent();
            if (parent != null)
            {
                parent.onDescendantStateChange(previous, visibleTo);
            }
        }
    }
    
    public AccessibleTo getAccessibleTo()
    {
        return getFileItemMetadata().getAccessibleTo();
    }
    
    public void setAccessibleTo(AccessibleTo accessibleTo)
    {
        AccessibleTo previous = getFileItemMetadata().getAccessibleTo();
        boolean changed = getFileItemMetadata().setAccessibleTo(accessibleTo);
        if (changed)
        {
            DatasetItemContainer parent = (DatasetItemContainer) getParent();
            if (parent != null)
            {
                parent.onDescendantStateChange(previous, accessibleTo);
            }
        }      
    }
    
    public boolean isAccessibleFor(int userProfile)
    {
        int mask = AccessCategory.UTIL.getBitMask(getReadAccessCategory());
        return ((mask & userProfile) > 0);
    }
    
    public int getAccessProfile(int userProfile)
    {
        int mask = AccessCategory.UTIL.getBitMask(getReadAccessCategory());
        return mask & userProfile;
    }
    
    /**
     * Hack needed because there is no unification of key abstractions in the DANS software development process. 
     * @return the AccessCategory of the file item in respect to this 'AccessibleTo'.
     */
    public AccessCategory getReadAccessCategory()
    {
        return AccessibleTo.translate(getAccessibleTo());
    }
    
    @Override
    public AccessCategory getViewAccessCategory()
    {
        return VisibleTo.translate(getVisibleTo());
    }
    
	@Override
	public Set<String> getContentModels()
	{
		 Set<String> contentModels = super.getContentModels();
		 contentModels.add(Constants.CM_FILE_ITEM_1);
		 return contentModels;
	}

    @Override
    public void setDescriptiveMetadata(Element content)
    {
        descriptiveMetadata = new DescriptiveMetadataImpl(content);
    }
    
    public boolean hasDescriptiveMetadata()
    {
        return descriptiveMetadata != null;
    }
    
    public DescriptiveMetadata getDescriptiveMetadata()
    {
        return descriptiveMetadata;
    }
    
    @Override
    public String getAutzStrategyName()
    {
        return "nl.knaw.dans.easy.security.authz.EasyFileItemAuthzStrategy";
    }

}
