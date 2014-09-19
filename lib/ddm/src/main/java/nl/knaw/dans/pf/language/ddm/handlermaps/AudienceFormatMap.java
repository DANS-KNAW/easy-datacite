package nl.knaw.dans.pf.language.ddm.handlermaps;

import java.io.IOException;
import java.util.Properties;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

public class AudienceFormatMap {
    private static Properties properties;

    private static Properties getProps() throws IOException, ResourceNotFoundException {
        if (properties == null) {
            properties = new Properties();
            properties.load(ResourceLocator.getInputStream("format.properties"));
        }
        return properties;
    }

    public static MetadataFormat get(final BasicString audience) {
        try {
            if (EmdConstants.SCHEME_ID_DISCIPLINES.equals(audience.getSchemeId())) {
                final String property = getProps().getProperty(audience.getValue(), MetadataFormat.UNSPECIFIED.name());
                return MetadataFormat.valueOf(property);
            }
        }
        catch (final Throwable e) {}
        return MetadataFormat.UNSPECIFIED;
    }
}
