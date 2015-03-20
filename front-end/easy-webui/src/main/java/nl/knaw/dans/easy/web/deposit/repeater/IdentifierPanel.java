package nl.knaw.dans.easy.web.deposit.repeater;

import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_AIP_ID;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_DMO_ID;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_DOI;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_OAI_ITEM_ID;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_PID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.CacheException;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListGetter;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.web.deposit.repeasy.IdentifierListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.IdentifierListWrapper.IdentifierModel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdentifierPanel extends AbstractChoicePanel {

    private static final long serialVersionUID = -822413494904086019L;
    private static final Logger logger = LoggerFactory.getLogger(IdentifierPanel.class);

    private static Map<String, String> SCHEMENAME_MAP;

    /**
     * Constructor that takes a model with a ListWrapper&lt;T> as model object.
     * 
     * @param wicketId
     *        id of this panel
     * @param model
     *        a model of sort IModel&lt;ListWrapper&lt;T>>
     * @param choices
     *        a list of choices
     */
    public IdentifierPanel(final String wicketId, final IModel<IdentifierListWrapper> model, final ChoiceList choiceList) {
        super(wicketId, model, choiceList);
    }

    @Override
    protected Panel getRepeatingComponentPanel(final ListItem item) {
        if (isInEditMode()) {
            return new RepeatingEditModePanel(item);
        } else {
            return new RepeatingViewModePanel(item);
        }
    }

    class RepeatingEditModePanel extends Panel {

        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingEditModePanel(final ListItem<IdentifierModel> item) {
            super(REPEATING_PANEL_ID);

            List<KeyValuePair> choices = getChoices();

            PropertyModel<KeyValuePair> dropDownPM = new PropertyModel<KeyValuePair>(item.getDefaultModelObject(), "scheme");
            PropertyModel<String> valuePM = new PropertyModel<String>(item.getDefaultModelObject(), "value");

            final DropDownChoice<KeyValuePair> schemeChoice = new DropDownChoice<KeyValuePair>("schemeChoice", dropDownPM, choices, getRenderer());
            schemeChoice.setVisible(choices.size() > 0);
            schemeChoice.setNullValid(isNullValid());
            add(schemeChoice);
            add(new TextField<String>("valueField", valuePM));
        }

    }

    class RepeatingViewModePanel extends Panel {

        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingViewModePanel(final ListItem<IdentifierModel> item) {
            super(REPEATING_PANEL_ID);
            IdentifierModel im = (IdentifierModel) item.getDefaultModelObject();
            PropertyModel<IdentifierModel> pm = new PropertyModel<IdentifierModel>(item.getDefaultModel(), "value");
            add(new Label("schemeName", lookup(im.getBasicIdentifier().getScheme())));
            add(new Label("noneditable", pm));
        }

    }

    private static String lookup(String scheme) {
        if (SCHEMENAME_MAP == null) {
            SCHEMENAME_MAP = new HashMap<String, String>();
            SCHEMENAME_MAP.put(null, null);
            SCHEMENAME_MAP.put(SCHEME_PID, "Persistent identifier: ");
            SCHEMENAME_MAP.put(SCHEME_DOI, "DOI: ");
            SCHEMENAME_MAP.put(SCHEME_DMO_ID, "Fedora Identifier: ");
            SCHEMENAME_MAP.put(SCHEME_AIP_ID, "AipId: ");
            SCHEMENAME_MAP.put(SCHEME_ARCHIS_ONDERZOEK_M_NR, "Archis onderzoeksmeldingsnr.");
            SCHEMENAME_MAP.put(SCHEME_OAI_ITEM_ID, "OAI item id: ");
            try {
                ChoiceList cl = ChoiceListGetter.getInstance().getChoiceList("archaeology.dc.identifier", null);
                for (KeyValuePair kvp : cl.getChoices()) {
                    SCHEMENAME_MAP.put(kvp.getKey(), kvp.getValue() + ": ");
                }
            }
            catch (CacheException e) {
                logger.error("could not load choice list. " + e.getMessage(), e);
            }
            catch (ResourceNotFoundException e) {
                logger.error("could not load choice list. " + e.getMessage(), e);
            }
            catch (DomainException e) {
                logger.error("could not load choice list. " + e.getMessage(), e);
            }
        }
        return SCHEMENAME_MAP.get(scheme);
    }
}
