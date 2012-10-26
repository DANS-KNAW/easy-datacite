package nl.knaw.dans.common.wicket.components.jumpoff;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.UnitMetadata;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffFile;
import nl.knaw.dans.common.wicket.components.CommonPanel;
import nl.knaw.dans.common.wicket.model.DMOModel;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

public abstract class JumpoffPanel extends CommonPanel
{
    private static final long serialVersionUID = 1899985393302979412L;
    private static final String WI_CONTENTPANEL = "viewEditJumpoffPanel";

    private final DataModelObject targetDmo;
    private Component viewEditJumpoffPanel;
    private boolean initiated;
    private boolean jumpoffExists;
    private boolean inEditMode;

    private final String resourceAlias;

    public JumpoffPanel(String id, DataModelObject targetDmo, String resourceAlias)
    {
        super(id);
        this.targetDmo = targetDmo;
        this.resourceAlias = resourceAlias;
    }

    public String getResourceAlias()
    {
        return resourceAlias;
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init()
    {
        JumpoffDmo jumpoffDmo = getJumpoffDmoFor(targetDmo);

        jumpoffExists = jumpoffDmo != null;
        if (jumpoffExists)
        {
            setDefaultModel(createModel(jumpoffDmo));
            viewEditJumpoffPanel = new JumpoffViewPanel(WI_CONTENTPANEL, getJumpoffDmoModel());
        }
        else
        {
            setDefaultModel(createModel(new JumpoffDmo(targetDmo)));
            viewEditJumpoffPanel = new Label(WI_CONTENTPANEL, "");
        }

        viewEditJumpoffPanel.setOutputMarkupId(true);
        add(viewEditJumpoffPanel);

        add(new Link("addButton")
        {

            private static final long serialVersionUID = 8599836125938720374L;

            @Override
            public void onClick()
            {
                switchEditMode();
            }

            @Override
            public boolean isVisible()
            {
                return !jumpoffExists && !inEditMode;
            }

        });
        add(new Link("editButton")
        {
            private static final long serialVersionUID = 5444183424272535973L;

            @Override
            public void onClick()
            {
                switchEditMode();
            }

            @Override
            public boolean isVisible()
            {
                return !inEditMode && jumpoffExists;
            }

        });

        Link deleteButton = new Link("deleteButton")
        {
            @Override
            public void onClick()
            {
                deleteJumpoff();
            }

            @Override
            public boolean isVisible()
            {
                return jumpoffExists;
            }
        };
        deleteButton.add(new SimpleAttributeModifier("onclick", "return confirm('Are you sure you want to delete " + "this jumpoff page?');"));
        add(deleteButton);

        Link toggleEditorButton = new Link("toggleEditorButton")
        {

            @Override
            public void onClick()
            {
                toggleEditorMode(getJumpoffDmo());
                refreshPanel();
            }

            @Override
            public boolean isVisible()
            {
                return jumpoffExists || inEditMode;
            }

        };
        add(toggleEditorButton);
    }

    /**
     * Get the JumpoffDmo for the given targetDmo. If no JumpoffDmo for the given targetDmo exists <code>null</code> must be returned.
     * 
     * @param targetDmo
     *        the DMO that is the object of the jump off page
     * @return JumpoffDmo for the given targetDmo or <code>null</code>
     */
    public abstract JumpoffDmo getJumpoffDmoFor(DataModelObject targetDmo);

    public abstract void saveJumpoffDmo(DataModelObject targetDmo, JumpoffDmo jumpoffDmo);

    public abstract void deleteJumpoffDmo(DataModelObject targetDmo, JumpoffDmo jumpoffDmo);

    public abstract List<UnitMetadata> getUnitMetadata(JumpoffDmo jumpoffDmo);

    public abstract void deleteResource(DataModelObject targetDmo, ResourceRef resourceRef);

    public abstract void toggleEditorMode(JumpoffDmo jumpoffDmo);

    @Override
    public boolean isVisible()
    {
        return true;
    }

    private DMOModel<JumpoffDmo> createModel(JumpoffDmo jumpoffDmo)
    {
        return new DMOModel<JumpoffDmo>(jumpoffDmo)
        {

            private static final long serialVersionUID = 1L;

            @Override
            protected JumpoffDmo loadDmo()
            {
                JumpoffDmo jumpoffDmo = getJumpoffDmoFor(targetDmo);
                if (jumpoffDmo == null)
                {
                    jumpoffDmo = new JumpoffDmo(targetDmo);
                }
                return jumpoffDmo;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private DMOModel<JumpoffDmo> getJumpoffDmoModel()
    {
        DMOModel<JumpoffDmo> model = (DMOModel<JumpoffDmo>) getDefaultModel();
        return model;
    }

    private JumpoffDmo getJumpoffDmo()
    {
        return getJumpoffDmoModel().getObject();
    }

    private void switchEditMode()
    {
        inEditMode = true;
        viewEditJumpoffPanel = new JumpoffEditPanel(WI_CONTENTPANEL, getJumpoffDmoModel())
        {

            private static final long serialVersionUID = 45871822412975505L;

            @Override
            public void onCancelButtonClicked()
            {
                switchViewMode();
            }

            @Override
            public void onSaveButtonClicked()
            {
                saveContents();
            }

            @Override
            public void onViewButtonClicked()
            {
                saveContents();
                switchViewMode();
            }

            @Override
            public void onFileUpload()
            {
                saveContents();
            }

            @Override
            public List<ResourceRef> getUploadedResources()
            {
                return JumpoffPanel.this.getUploadedResources();
            }

            @Override
            public void onDelete(ResourceRef resourceRef)
            {
                deleteResource(resourceRef);
            }

        };
        addOrReplace(viewEditJumpoffPanel);
    }

    private void switchViewMode()
    {
        inEditMode = false;
        viewEditJumpoffPanel = new JumpoffViewPanel(WI_CONTENTPANEL, getJumpoffDmoModel());
        addOrReplace(viewEditJumpoffPanel);
    }

    private void refreshPanel()
    {
        if (inEditMode)
        {
            switchEditMode();
        }
        else
        {
            switchViewMode();
        }
    }

    private void saveContents()
    {
        saveJumpoffDmo(targetDmo, getJumpoffDmo());
        jumpoffExists = true;
    }

    private List<ResourceRef> getUploadedResources()
    {
        List<ResourceRef> resourceRefs = new ArrayList<ResourceRef>();
        JumpoffDmo jumpoffDmo = getJumpoffDmo();
        String containerId = jumpoffDmo.getStoreId();
        if (containerId != null)
        {
            String markup = getJumpoffDmo().getHtmlMarkup().getHtml();
            List<UnitMetadata> umdList = getUnitMetadata(jumpoffDmo);
            for (UnitMetadata umd : umdList)
            {
                if (umd.getId().startsWith(JumpoffFile.UNIT_ID_PREFIX))
                {
                    ResourceRef rr = new ResourceRef(containerId, umd, resourceAlias);
                    rr.setReferenced(markup.contains(rr.getHref().replaceAll("&", "&amp;")));
                    resourceRefs.add(rr);
                }
            }
        }
        return resourceRefs;
    }

    private void deleteResource(ResourceRef resourceRef)
    {
        deleteResource(targetDmo, resourceRef);
        info("Removed file: " + resourceRef.getFilename());
    }

    private void deleteJumpoff()
    {
        deleteJumpoffDmo(targetDmo, getJumpoffDmo());
        info("Removed jumpoff page from " + targetDmo.getStoreId());
        jumpoffExists = false;
        setDefaultModel(createModel(new JumpoffDmo(targetDmo)));
        switchViewMode();
    }

}
