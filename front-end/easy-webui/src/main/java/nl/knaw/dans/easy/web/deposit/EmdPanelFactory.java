package nl.knaw.dans.easy.web.deposit;

import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.deposit.repeasy.ArchisListWrapper;
import nl.knaw.dans.easy.web.deposit.repeater.FixedTextPanel;
import nl.knaw.dans.easy.web.deposit.repeater.RepeaterPanelFactory;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class EmdPanelFactory extends RepeaterPanelFactory {

    private static final long serialVersionUID = 420491451388909660L;

    private DatasetModel datasetModel;

    public EmdPanelFactory(String panelWicketId, Component parent, DatasetModel datasetModel) {
        super(new EmdModelFactory(datasetModel), panelWicketId, parent);
        this.datasetModel = datasetModel;
    }

    public Panel createArchisEditPanel(StandardPanelDefinition spDef, IModel<ArchisListWrapper> model) {
        ArchisEditPanel panel = new ArchisEditPanel(getPanelWicketId(), model);
        panel.setPanelDefinition(spDef);
        return panel;
    }

    public Panel createArchisViewPanel(StandardPanelDefinition spDef, IModel<EasyMetadata> model) {
        ArchisViewPanel panel = new ArchisViewPanel(getPanelWicketId(), model);
        panel.setPanelDefinition(spDef);
        return panel;
    }

    public Panel createUploadPanel(StandardPanelDefinition spDef, IModel<?> model) {
        UploadPanel panel = new UploadPanel(getPanelWicketId(), datasetModel);
        panel.setPanelDefinition(spDef);
        return panel;
    }

    public Panel createUploadPakbonPanel(StandardPanelDefinition spDef, IModel<?> model) {
        UploadPakbonPanel panel = new UploadPakbonPanel(getPanelWicketId(), datasetModel);
        panel.setPanelDefinition(spDef);
        return panel;
    }

    public Panel createLicensePanel(StandardPanelDefinition spDef, IModel<EasyMetadata> model) {
        LicensePanel panel = new LicensePanel(getPanelWicketId(), model);
        panel.setPanelDefinition(spDef);
        return panel;
    }

    public Panel createPersistentIdentifierPanel(StandardPanelDefinition spDef, IModel<EasyMetadata> model) {
        PersistentIdentifierPanel panel = new PersistentIdentifierPanel(getPanelWicketId(), model);
        panel.setPanelDefinition(spDef);
        return panel;
    }

    public Panel createRelationViewPanel(StandardPanelDefinition spDef, IModel<EasyMetadata> model) {
        RelationViewPanel panel = new RelationViewPanel(getPanelWicketId(), model);
        panel.setPanelDefinition(spDef);
        return panel;
    }

    public Panel createFixedTextPanel(StandardPanelDefinition spDef, IModel<EasyMetadata> model) {
        FixedTextPanel fixedTextPanel = new FixedTextPanel(getPanelWicketId(), model);
        fixedTextPanel.setPanelDefinition(spDef);
        return fixedTextPanel;
    }
}
