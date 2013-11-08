package nl.knaw.dans.easy.web.deposit;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;
import nl.knaw.dans.easy.web.template.emd.atomic.DepositUploadPakbonPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.PakbonStatus.*;
public class UploadPakbonPanel extends AbstractCustomPanel
{
    private static final long serialVersionUID = -9132574510082841750L;
    private static Logger log = LoggerFactory.getLogger(UploadPakbonPanel.class);

    private final DatasetModel datasetModel;

    public UploadPakbonPanel(String id, DatasetModel datasetModel)
    {
        super(id);
        this.datasetModel = datasetModel;
        setOutputMarkupId(true);
    }

    @Override
    protected Panel getCustomComponentPanel()
    {
        Dataset dataset = datasetModel.getObject();
        if (IMPORTED.equals(dataset.getEasyMetadata().getEmdOther().getEasApplicationSpecific().getPakbonStatus()))
        {
            return new ViewModePanel();
        }
        else
        {
            return new UploadModePanel();
        }
    }

    class UploadModePanel extends Panel
    {

        private static final long serialVersionUID = -1141097831590702485L;

        public UploadModePanel()
        {
            super(CUSTOM_PANEL_ID);
            this.add(new DepositUploadPakbonPanel("uploadPanel", datasetModel));

            AjaxSubmitLink refreshPageLink = new AjaxSubmitLink("refreshPageLink")
            {

                private static final long serialVersionUID = 4529821897686007980L;

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    ((DepositPanel) form.getParent()).setInitiated(false);
                    target.addComponent(form.getParent());
                }

                @Override
                public boolean isVisible()
                {
                    return true;
                }

                @Override
                public boolean isEnabled()
                {
                    return true;
                }
            };
            add(refreshPageLink);
        }

    }

    class ViewModePanel extends Panel
    {

        private static final long serialVersionUID = -1141097831590702485L;

        public ViewModePanel()
        {
            super(CUSTOM_PANEL_ID);
            Label label = new Label("noneditable", "PAKBON IMPORTED");
            add(label);
        }
    }
}
