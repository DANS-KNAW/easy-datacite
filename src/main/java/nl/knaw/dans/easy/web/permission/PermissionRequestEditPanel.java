package nl.knaw.dans.easy.web.permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;

//Note maybe a AbstractDatasetModelPanel is better?
//
public class PermissionRequestEditPanel extends AbstractEasyPanel
{
    private static final long serialVersionUID = 4065214891614131014L;
    private static final Logger logger = LoggerFactory.getLogger(PermissionRequestEditPanel.class);
    
    public PermissionRequestEditPanel(String wicketId,  final AbstractEasyPage fromPage,
            final DatasetModel datasetModel)
    {
        super(wicketId, datasetModel);

        add(new PermissionRequestForm("form", fromPage, datasetModel));
    }
}
