package nl.knaw.dans.easy.web.deposit.repeater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.CacheException;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListGetter;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.emd.types.BasicIdentifier;
import nl.knaw.dans.easy.domain.model.emd.types.EmdConstants;
import nl.knaw.dans.easy.web.deposit.repeasy.IdentifierListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.IdentifierListWrapper.IdentifierModel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class IdentifierPanel extends AbstractChoicePanel
{

    private static final long        serialVersionUID = -822413494904086019L;

    private boolean					 dropdownVisible = true;
    
    // part of quick fix
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
    public IdentifierPanel(final String wicketId, final IModel model, final ChoiceList choiceList)
    {
       super(wicketId, model, choiceList);
    }

    public void setDropdownVisible(boolean dropdownVisible) {
		this.dropdownVisible = dropdownVisible;
	}
    
    @Override
    protected Panel getRepeatingComponentPanel(final ListItem item)
    {
    	if (isInEditMode())
    	{
    		return new RepeatingEditModePanel(item);
    	}
    	else
    	{
    		return new RepeatingViewModePanel(item);
    	}
    }

    
    class RepeatingEditModePanel extends Panel
    {
        
        private static final long serialVersionUID = -1064600333931796440L;
        
        private final BasicIdentifier identifier;

        RepeatingEditModePanel(final ListItem<IdentifierModel> item)
        {
            super(REPEATING_PANEL_ID);
            identifier = item.getModelObject().getBasicIdentifier();
            
            List<KeyValuePair> choices = getChoices();
            
            final DropDownChoice schemeChoice = new DropDownChoice("schemeChoice", new PropertyModel(item.getDefaultModelObject(),
            "scheme"), choices, getRenderer());
            
            schemeChoice.setVisible(dropdownVisible);
            schemeChoice.setNullValid(isNullValid());
            final TextField valueField = new TextField("valueField", new PropertyModel(item.getDefaultModelObject(), "value"));
            add(schemeChoice);
            add(valueField);
        }
        
    }
    
    class RepeatingViewModePanel extends Panel
    {
        
        private static final long serialVersionUID = -1064600333931796440L;

        RepeatingViewModePanel(final ListItem item)
        {
            super(REPEATING_PANEL_ID);
            // quick fix!!
            IdentifierListWrapper.IdentifierModel im = (IdentifierModel) item.getDefaultModelObject();
            String scheme = im.getBasicIdentifier().getScheme();
            String schemeName = null;
            if (scheme != null)
            {
                schemeName = lookup(scheme);
            }
            Label schemeLabel = new Label("schemeName", schemeName);
            add(schemeLabel);
            // end quick fix
            PropertyModel pm = new PropertyModel(item.getDefaultModel(), "value");
    		Label label = new Label("noneditable", pm);
            add(label);
        }
        
    }
    
    // part of quick fix: identifiers not editable or not editable on this panel
    private static String lookup(String scheme)
    {
        if (SCHEMENAME_MAP == null)
        {
            SCHEMENAME_MAP = new HashMap<String, String>();
            SCHEMENAME_MAP.put("PID", "Persistent identifier: ");
            SCHEMENAME_MAP.put("DMO_ID", "Fedora Identifier: ");
            SCHEMENAME_MAP.put("OAI_ITEM_ID", "OAI item id: ");
            SCHEMENAME_MAP.put("AIP_ID", "AipId: ");
            SCHEMENAME_MAP.put("Archis_onderzoek_m_nr", "Archis onderzoeksmeldingsnr.");
            try
            {
                ChoiceList cl = ChoiceListGetter.getInstance().getChoiceList("archaeology.dc.identifier", null);
                for (KeyValuePair kvp : cl.getChoices())
                {
                    SCHEMENAME_MAP.put(kvp.getKey(), kvp.getValue() + ": ");
                }
            }
            catch (ObjectNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (CacheException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (ResourceNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (DomainException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return SCHEMENAME_MAP.get(scheme);
    }
}
