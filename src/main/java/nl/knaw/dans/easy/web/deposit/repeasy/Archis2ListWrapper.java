package nl.knaw.dans.easy.web.deposit.repeasy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.deposit.discipline.ArchisCollector;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.BasicIdentifier;
import nl.knaw.dans.easy.domain.model.emd.types.EmdConstants;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractListWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class Archis2ListWrapper extends AbstractListWrapper<Archis2ListWrapper.ArchisItemModel>
{

    private static final long serialVersionUID = -6827327637600594816L;

    private final EasyMetadata easyMetadata;

    public Archis2ListWrapper(EasyMetadata easyMetadata)
    {
        this.easyMetadata = easyMetadata;
    }

    @Override
    public List<ArchisItemModel> getInitialItems()
    {
        List<ArchisItemModel> items = new ArrayList<Archis2ListWrapper.ArchisItemModel>();
        for (BasicIdentifier bi : easyMetadata.getEmdIdentifier().getAllIdentfiers(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR))
        {
            items.add(new ArchisItemModel(bi));
        }
        return items;
    }

    @Override
    public int size()
    {
        return getInitialItems().size();
    }

    @Override
    public int synchronize(List<ArchisItemModel> listItems)
    {
        easyMetadata.getEmdIdentifier().removeAllIdentifiers(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);

        for (ArchisItemModel aim : listItems)
        {
            BasicIdentifier bi = aim.getIdentifier();
            if (StringUtils.isNotBlank(ArchisCollector.getDigits(bi.getValue())))
            {
                bi.setScheme(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);
                easyMetadata.getEmdIdentifier().add(bi);
            }
        }

        return 0;
    }

    @Override
    public ArchisItemModel getEmptyValue()
    {
        return new ArchisItemModel(new BasicIdentifier());
    }

    @Override
    public ChoiceRenderer<String> getChoiceRenderer()
    {
        return null;
    }

    public EasyMetadata getEasyMetadata()
    {
        return easyMetadata;
    }

    public static class ArchisItemModel implements Serializable
    {
        private static final long serialVersionUID = -7569278187437901832L;

        private final BasicIdentifier identifier;

        public ArchisItemModel(BasicIdentifier identifier)
        {
            this.identifier = identifier;
        }

        public BasicIdentifier getIdentifier()
        {
            return identifier;
        }

        public String getValue()
        {
            return identifier.getValue();
        }

        public void setValue(String value)
        {
            identifier.setValue(value);
        }
    }

}
