package nl.knaw.dans.easy.web.deposit;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.behavior.FormModificationDetectorBehavior;
import nl.knaw.dans.easy.domain.deposit.discipline.ArchisCollector;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.deposit.repeasy.Archis2ListWrapper;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractRepeaterPanel;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class Archis2EditPanel extends AbstractRepeaterPanel<Archis2ListWrapper.ArchisItemModel> {

    private static final long serialVersionUID = -8019251676702448058L;

    private static final String FILL_IN_OMG_NR = "deposit.omg_fill_in";

    public Archis2EditPanel(String wicketId, IModel<Archis2ListWrapper> model) {
        super(wicketId, model);
    }

    protected EasyMetadata getEasyMetadata() {
        Archis2ListWrapper archis2ListWrapper = (Archis2ListWrapper) getModelObject();
        return archis2ListWrapper.getEasyMetadata();
    }

    @Override
    protected Panel getRepeatingComponentPanel(ListItem<Archis2ListWrapper.ArchisItemModel> item) {
        if (isInEditMode()) {
            return new RepeatingEditModePanel(item);
        } else {
            throw new UnsupportedOperationException("Only edit panel");
        }
    }

    class RepeatingEditModePanel extends Panel {

        private static final long serialVersionUID = -5374680337042765664L;

        RepeatingEditModePanel(final ListItem<Archis2ListWrapper.ArchisItemModel> item) {
            super(REPEATING_PANEL_ID);
            final BasicIdentifier archisId = item.getModelObject().getIdentifier();

            final TextField<String> textField = new TextField<String>("omg_nr", new PropertyModel<String>(item.getModelObject(), "value")) {

                private static final long serialVersionUID = -236794326207188963L;

                @Override
                public boolean isEnabled() {
                    return archisId.getIdentificationSystem() == null;
                }

            };
            add(textField);

            AjaxSubmitLink archisSubmitLink = new AjaxSubmitLink("archisSubmitLink") {

                private static final long serialVersionUID = 4529821897686007980L;

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    String nummer = ArchisCollector.getDigits(archisId.getValue());
                    if (StringUtils.isBlank(nummer)) {
                        Archis2EditPanel.this.error(item.getIndex(), getString(FILL_IN_OMG_NR));
                    } else {
                        try {
                            Services.getDepositService().getArchisInfo(archisId, getEasyMetadata());
                            if (archisId.getIdentificationSystem() == null) {
                                Archis2EditPanel.this.error(item.getIndex(), "No data found for Archis omg_nr. '" + nummer + "'");
                            } else {
                                ((DepositPanel) form.getParent()).setInitiated(false);// set initated
                                                                                      // variable of
                                                                                      // DepositPanel to
                                                                                      // false to run
                                                                                      // init() method.
                                EasyMetadata emd = getEasyMetadata();
                                if (emd.getEmdIdentifier() != null) {
                                    emd.getEmdIdentifier().removeIdentifier(archisId.getScheme());
                                }
                                Archis2EditPanel.this.info(item.getIndex(), "Imported data for Archis omg_nr. '" + nummer + "'");
                            }
                        }
                        catch (ServiceException e) {
                            textField.setEnabled(false);
                            setEnabled(false);
                            target.appendJavascript(FormModificationDetectorBehavior.DISABLE_DETECTOR_JS);
                            Archis2EditPanel.this.error(e.getMessage());
                        }
                    }

                    target.addComponent(form.getParent());
                }

                @Override
                public boolean isVisible() {
                    return archisId.getIdentificationSystem() == null;
                }

                @Override
                public boolean isEnabled() {
                    return archisId.getIdentificationSystem() == null;
                }
            };
            add(archisSubmitLink);

        }
    }

}
