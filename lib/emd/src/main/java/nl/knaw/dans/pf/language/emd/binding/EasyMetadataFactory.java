package nl.knaw.dans.pf.language.emd.binding;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

public final class EasyMetadataFactory {
    private EasyMetadataFactory() {
        // never instantiate.
    }

    public static Class<? extends EasyMetadata> getEasyMetadataClass() {
        return EasyMetadataImpl.class;
    }

    public static EasyMetadata newEasyMetadata(MetadataFormat metadataFormat) {
        EasyMetadataImpl emd = new EasyMetadataImpl(metadataFormat);
        return emd;
    }

}
