package nl.knaw.dans.easy.web.deposit;

import nl.knaw.dans.easy.web.deposit.repeater.AbstractCustomPanel;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static nl.knaw.dans.easy.web.deposit.repeasy.CMDIFormatChoiceWrapper.CMDI_MIME;

public class CmdiViewPanel extends AbstractCustomPanel {

    private static final long serialVersionUID = -9039579510082841556L;
    private final EasyMetadata easyMetadata;

    public CmdiViewPanel(final String id, final EasyMetadata easyMetadata) {
        this(id, new Model<EasyMetadata>(easyMetadata));
    }

    public CmdiViewPanel(final String id, final IModel<EasyMetadata> model) {
        super(id, model);
        easyMetadata = (EasyMetadata) model.getObject();
        setOutputMarkupId(true);
    }

    @Override
    protected Panel getCustomComponentPanel() {
        if (isInEditMode()) {
            throw new UnsupportedOperationException("EditMode not supported.");
        } else {
            return new ViewPanel();
        }
    }

    private boolean containsCMDI() {
        return easyMetadata.getEmdFormat().getDcFormat().contains(CMDI_MIME);
    }

    class ViewPanel extends Panel {
        private static final long serialVersionUID = -3441453142983333780L;

        public ViewPanel() {
            super(CUSTOM_PANEL_ID);
            setVisible(containsCMDI());
        }
    }
}
