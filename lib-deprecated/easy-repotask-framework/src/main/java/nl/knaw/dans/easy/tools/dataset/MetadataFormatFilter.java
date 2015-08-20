package nl.knaw.dans.easy.tools.dataset;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

public class MetadataFormatFilter implements DmoFilter<Dataset> {

    private final MetadataFormat[] acceptedFormats;

    public MetadataFormatFilter(MetadataFormat... acceptedFormats) {
        this.acceptedFormats = acceptedFormats;
    }

    @Override
    public boolean accept(Dataset dmo) {
        boolean accepted = false;
        MetadataFormat dmoFormat = dmo.getEasyMetadata().getEmdOther().getEasApplicationSpecific().getMetadataFormat();
        for (MetadataFormat format : acceptedFormats) {
            accepted |= format.equals(dmoFormat);
        }
        return accepted;
    }

}
