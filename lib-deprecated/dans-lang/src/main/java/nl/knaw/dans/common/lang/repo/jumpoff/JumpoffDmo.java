package nl.knaw.dans.common.lang.repo.jumpoff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDataModelObject;
import nl.knaw.dans.common.lang.repo.BinaryUnit;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.bean.BeanFactory;
import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata;
import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata.MarkupVersionID;
import nl.knaw.dans.common.lang.repo.relations.Relations;

public class JumpoffDmo extends AbstractDataModelObject
{

    public static final DmoNamespace NAMESPACE = new DmoNamespace("dans-jumpoff");
    public static final String UNIT_ID_PAGE = "JUMPOFF_PAGE";

    public static final String UNIT_ID_HTML = "HTML_MU";
    public static final String UNIT_ID_TEXT = "TXT_MU";

    private static final String LABEL_HTML = "Validated markup";
    private static final String LABEL_TEXT = "Unvalidated markup";

    private static final long serialVersionUID = -1289187269891600767L;

    private MarkupUnit htmlMarkup;
    private MarkupUnit textMarkup;
    private String objectId;
    private List<JumpoffFile> joFiles = new ArrayList<JumpoffFile>();
    private JumpoffDmoMetadata jumpoffDmoMetadata;

    /**
     * Only use for deserialization.
     */
    public JumpoffDmo()
    {
        super();
    }

    public JumpoffDmo(String storeId)
    {
        super(storeId);
    }

    public JumpoffDmo(String storeId, String objectId)
    {
        super(storeId);
        this.objectId = objectId;
    }

    public JumpoffDmo(DataModelObject targetDmo)
    {
        super();
        this.objectId = targetDmo.getStoreId();
    }

    public JumpoffDmoMetadata getJumpoffDmoMetadata()
    {
        if (jumpoffDmoMetadata == null)
        {
            jumpoffDmoMetadata = BeanFactory.newJumpoffDmoMetadata();
        }
        return jumpoffDmoMetadata;
    }

    public void setJumpoffDmoMetadata(JumpoffDmoMetadata jumpoffDmoMetadata)
    {
        this.jumpoffDmoMetadata = jumpoffDmoMetadata;
    }

    public void toggleEditorMode()
    {
        getJumpoffDmoMetadata().toggleEditorMode();
    }

    public boolean isInHtmlMode()
    {
        return getJumpoffDmoMetadata().isInHtmlMode();
    }

    public DmoNamespace getDmoNamespace()
    {
        return NAMESPACE;
    }

    public boolean isDeletable()
    {
        return true;
    }

    public void addFile(File file)
    {
        JumpoffFile joFile = new JumpoffFile(file.getName().replaceAll(" ", "_"));
        try
        {
            joFile.setFile(file);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        joFiles.add(joFile);
    }

    @Override
    public List<MetadataUnit> getMetadataUnits()
    {
        List<MetadataUnit> metadataUnits = super.getMetadataUnits();
        metadataUnits.add(getJumpoffDmoMetadata());
        return metadataUnits;
    }

    @Override
    public List<BinaryUnit> getBinaryUnits()
    {
        List<BinaryUnit> binaryUnits = super.getBinaryUnits();
        MarkupUnit htmlMU = getHtmlMarkup();
        if (htmlMU.hasFile())
        {
            binaryUnits.add(htmlMU);
        }
        MarkupUnit textMU = getTextMarkup();
        if (textMU.hasFile())
        {
            binaryUnits.add(textMU);
        }
        binaryUnits.addAll(joFiles);
        return binaryUnits;
    }

    @Override
    protected Relations newRelationsObject()
    {
        if (objectId == null)
        {
            return new JumpoffDmoRelations(this);
        }
        else
        {
            return new JumpoffDmoRelations(this, objectId);
        }
    }

    public String getObjectId()
    {
        if (objectId == null)
        {
            JumpoffDmoRelations relations = (JumpoffDmoRelations) getRelations();
            return relations.getObjectId();
        }
        else
        {
            return objectId;
        }
    }

    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }

    public MarkupUnit getMarkupUnit()
    {
        if (MarkupVersionID.HTML_MU.equals(getJumpoffDmoMetadata().getDefaultMarkupVersionID()))
        {
            return getHtmlMarkup();
        }
        else
        {
            return getTextMarkup();
        }
    }

    public MarkupUnit getHtmlMarkup()
    {
        if (htmlMarkup == null)
        {
            htmlMarkup = new MarkupUnit(UNIT_ID_HTML, LABEL_HTML);
        }
        return htmlMarkup;
    }

    public MarkupUnit getTextMarkup()
    {
        if (textMarkup == null)
        {
            textMarkup = new MarkupUnit(UNIT_ID_TEXT, LABEL_TEXT);
        }
        return textMarkup;
    }

    @Override
    public boolean isInvalidated() throws RepositoryException
    {
        return false;
    }

}
