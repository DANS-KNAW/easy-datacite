package nl.knaw.dans.easy.web.deposit.repeasy;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.ArchisCollector;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractListWrapper;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class ArchisListWrapper extends AbstractListWrapper<ArchisListWrapper.ArchisItemModel> {

    private static final long serialVersionUID = -6827327637600594816L;

    public static final URI ARCHIS2_URI = URI.create("http://archis2.archis.nl");
    public static final URI ARCHIS_URI = URI.create("https://archis.cultureelerfgoed.nl");
    public static final String SCHEME_ID_ARCHAEOLOGY_ID = "archaeology.dc.identifier";

    private final EasyMetadata easyMetadata;

    public ArchisListWrapper(EasyMetadata easyMetadata) {
        this.easyMetadata = easyMetadata;
    }

    @Override
    public List<ArchisItemModel> getInitialItems() {
        List<ArchisItemModel> items = new ArrayList<ArchisListWrapper.ArchisItemModel>();
        for (BasicIdentifier bi : easyMetadata.getEmdIdentifier().getAllIdentfiers(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR)) {
            items.add(new ArchisItemModel(bi));
        }
        return items;
    }

    @Override
    public int size() {
        return getInitialItems().size();
    }

    @Override
    public int synchronize(List<ArchisItemModel> listItems) {
        easyMetadata.getEmdIdentifier().removeAllIdentifiers(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);

        for (ArchisItemModel aim : listItems) {
            BasicIdentifier bi = aim.getIdentifier();
            if (StringUtils.isNotBlank(ArchisCollector.getDigits(bi.getValue()))) {
                bi.setScheme(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);

                bi.setSchemeId(SCHEME_ID_ARCHAEOLOGY_ID);
                bi.setIdentificationSystem(IsArchis2OMG(bi.getValue()) ? ARCHIS2_URI : ARCHIS_URI);

                easyMetadata.getEmdIdentifier().add(bi);
            }
        }

        return 0;
    }

    private boolean IsArchis2OMG(String omg) {
        // new Archis 'zaaknummers' are 10 digits, old Archis2 'nummers' are smaller
        // note that the input is textual and can contain multiple numbers and texts
        // to be more safe, split of the first number and inspect its length
        String cleanOmg = StringUtils.normalizeSpace(omg.replaceAll("[^0-9]", " ")).trim();
        String[] omgArr = StringUtils.split(cleanOmg);
        return (omgArr.length > 0 && omgArr[0].length() < 10);
    }

    @Override
    public ArchisItemModel getEmptyValue() {
        return new ArchisItemModel(new BasicIdentifier());
    }

    @Override
    public ChoiceRenderer<String> getChoiceRenderer() {
        return null;
    }

    public EasyMetadata getEasyMetadata() {
        return easyMetadata;
    }

    public static class ArchisItemModel implements Serializable {
        private static final long serialVersionUID = -7569278187437901832L;

        private final BasicIdentifier identifier;

        public ArchisItemModel(BasicIdentifier identifier) {
            this.identifier = identifier;
        }

        public BasicIdentifier getIdentifier() {
            return identifier;
        }

        public String getValue() {
            return identifier.getValue();
        }

        public void setValue(String value) {
            identifier.setValue(value);
        }
    }

}
