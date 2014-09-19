package nl.knaw.dans.easy.web.permission;

import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;

public class PermissionReplyEditPanel extends AbstractEasyPanel {
    private static final long serialVersionUID = 7165279397951054593L;

    public PermissionReplyEditPanel(String wicketId, final AbstractEasyPage fromPage, final DatasetModel datasetModel, final PermissionSequence sequence) {
        super(wicketId, datasetModel);

        // add the form
        add(new PermissionReplyForm("form", fromPage, datasetModel, sequence));
    }
}
