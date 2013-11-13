package nl.knaw.dans.easy.web.deposit;

import static nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.PakbonStatus.IMPORTED;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;
import nl.knaw.dans.easy.web.template.emd.atomic.DepositUploadPakbonPanel;
import nl.knaw.dans.pf.language.emd.EmdOther;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class UploadPakbonPanel extends AbstractCustomPanel
{
    private static final Logger log = LoggerFactory.getLogger(UploadPakbonPanel.class);

    private final DatasetModel datasetModel;

    public UploadPakbonPanel(String id, DatasetModel datasetModel)
    {
        super(id);
        assert datasetModel != null : "DatasetModel argument may not be null";
        this.datasetModel = datasetModel;
        setOutputMarkupId(true);
    }

    @Override
    protected Panel getCustomComponentPanel()
    {
        return isPakbonImported() ? new ViewModePanel() : new UploadModePanel();
    }

    private boolean isPakbonImported()
    {
        assert getDataset() != null : "Null datatset in UploadPakbonPanel";
        assert getDataset().getEasyMetadata() != null : "Null EMD in UploadPakbonPanel";
        EmdOther emdOther = getDataset().getEasyMetadata().getEmdOther();
        if (emdOther == null)
        {
            return false;
        }
        ApplicationSpecific eas = emdOther.getEasApplicationSpecific();
        if (eas == null)
        {
            return false;
        }
        return IMPORTED.equals(eas.getPakbonStatus());
    }

    private Dataset getDataset()
    {
        return datasetModel.getObject();
    }

    private class UploadModePanel extends Panel
    {
        public UploadModePanel()
        {
            super(CUSTOM_PANEL_ID);
            log.debug("Creating UploadPakbonPanel.UploadModePanel");
            this.add(new DepositUploadPakbonPanel("uploadPanel", datasetModel));
        }

    }

    private static class ViewModePanel extends Panel
    {
        public ViewModePanel()
        {
            super(CUSTOM_PANEL_ID);
            log.debug("Creating UploadPakbonPanel.ViewModePanel");
            Label label = new Label("noneditable", "PAKBON IMPORTED");
            add(label);
        }
    }
}
