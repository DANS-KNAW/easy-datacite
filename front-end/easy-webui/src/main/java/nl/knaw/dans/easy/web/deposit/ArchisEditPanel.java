package nl.knaw.dans.easy.web.deposit;

import nl.knaw.dans.easy.web.deposit.repeasy.ArchisListWrapper;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractRepeaterPanel;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.validator.PatternValidator;

public class ArchisEditPanel extends AbstractRepeaterPanel<ArchisListWrapper.ArchisItemModel> {

    private static final long serialVersionUID = -8019251676702448058L;

    public ArchisEditPanel(String wicketId, IModel<ArchisListWrapper> model) {
        super(wicketId, model);
    }

    protected EasyMetadata getEasyMetadata() {
        ArchisListWrapper archisListWrapper = (ArchisListWrapper) getModelObject();
        return archisListWrapper.getEasyMetadata();
    }

    @Override
    protected Panel getRepeatingComponentPanel(ListItem<ArchisListWrapper.ArchisItemModel> item) {
        if (isInEditMode()) {
            return new RepeatingEditModePanel(item);
        } else {
            throw new UnsupportedOperationException("Only edit panel");
        }
    }

    class RepeatingEditModePanel extends Panel {

        private static final long serialVersionUID = -5374680337042765664L;

        RepeatingEditModePanel(final ListItem<ArchisListWrapper.ArchisItemModel> item) {
            super(REPEATING_PANEL_ID);
            final TextField<String> textField = new TextField<String>("omg_nr", new PropertyModel<String>(item.getModelObject(), "value"));
            textField.setLabel(new StringResourceModel("label.archis.info", null));
            add(textField);
        }
    }

}
