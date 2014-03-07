package nl.knaw.dans.common.wicket.components.jumpoff;

import nl.knaw.dans.common.lang.repo.bean.JumpoffDmoMetadata;
import nl.knaw.dans.common.lang.repo.bean.MarkupMetadata;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.wicket.components.DateTimeLabel;
import nl.knaw.dans.common.wicket.model.DMOModel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public class JumpoffMetadataPanel extends Panel
{

    private static final long serialVersionUID = -5564464014865060234L;
    private static final String DATE_TIME_FORMAT = "DateAndTimeFormat";

    public JumpoffMetadataPanel(String id, DMOModel<JumpoffDmo> model)
    {
        super(id, model);
        JumpoffDmo joDmo = (JumpoffDmo) getDefaultModelObject();
        JumpoffDmoMetadata joMetadata = joDmo.getJumpoffDmoMetadata();
        MarkupMetadata markupMetadata = joMetadata.getDefaultMarkupMetadata();

        add(new Label("jumpoffId", joDmo.getStoreId()));
        add(new DateTimeLabel("lastEdited", markupMetadata.getLastEdited(), getString(DATE_TIME_FORMAT)));
        add(new Label("lastEditedBy", markupMetadata.getLastEditedBy()));
        add(new Label("editorMode", new ResourceModel("editor.mode." + markupMetadata.getMarkupVersionID().toString())));
    }

    @Override
    public boolean isVisible()
    {
        return ((JumpoffDmo) getDefaultModelObject()).getJumpoffDmoMetadata().getDefaultMarkupMetadata().getLastEdited() != null;
    }

}
