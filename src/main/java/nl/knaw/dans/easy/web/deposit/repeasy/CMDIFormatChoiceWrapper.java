package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractListWrapper;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class CMDIFormatChoiceWrapper extends AbstractListWrapper<KeyValuePair>
{
    private static final long serialVersionUID = 1L;

    private static final BasicString CMDI_MIME = new BasicString("application/x-cmdi+xml");

    private EasyMetadata easyMetadata;

    public CMDIFormatChoiceWrapper(EasyMetadata easyMetadata)
    {
        this.easyMetadata = easyMetadata;
    }

    /**
     * This method is called when the panel is displayed.
     * When it is displayed we need to decide which of the radio options should be selected
     * If the EASY Metadata's DC Format contains the CMDI MIME-type, the YES option should be selected.
     * In all other cases the NO option should be selected.
     */
    @Override
    public List<KeyValuePair> getInitialItems()
    {
        List<KeyValuePair> listItems = new ArrayList<KeyValuePair>();
        if (containsCMDI())
        {
            listItems.add(new KeyValuePair(CMDI_MIME.getValue(), null));
        }
        else
        {
            listItems.add(getEmptyValue());
        }
        return listItems;
    }

    /**
     * This method is called when the model needs to be updated to the latest version.
     * E.g. when a page is saved, or when you move to another page.
     * If the panel was in view and was editted it will also be saved.
     * 
     * When the YES option was selected we add the CMDI MIME-type to the DC Format field
     * in the EASY Metadata.
     * 
     * When the NO option (default) was selected we remove the CMDI MIME-type from the 
     * DC Format field in the EASY Metadata.
     */
    @Override
    public int synchronize(List<KeyValuePair> radioSelections)
    {
        String radioSelection = radioSelections.get(0).getKey();
        boolean selectionIsCmdi = CMDI_MIME.getValue().equals(radioSelection);
        if (selectionIsCmdi)
        {
            if (!containsCMDI())
            {
                getDcFormat().add(CMDI_MIME);
            }
        }
        else
        {
            if (containsCMDI())
                ;
            {
                getDcFormat().remove(CMDI_MIME);
            }
        }
        return 0;
    }

    private boolean containsCMDI()
    {
        return getDcFormat().contains(CMDI_MIME);
    }

    private List<BasicString> getDcFormat()
    {
        return easyMetadata.getEmdFormat().getDcFormat();
    }

    @Override
    public int size()
    {
        int size = 0;
        for (BasicString bs : getDcFormat())
        {
            size += (CMDI_MIME.equals(bs) ? 1 : 0);
        }
        return size;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ChoiceRenderer getChoiceRenderer()
    {
        return new KvpChoiceRenderer();
    }

    @Override
    public KeyValuePair getEmptyValue()
    {
        return new KeyValuePair(null, null);
    }
}
