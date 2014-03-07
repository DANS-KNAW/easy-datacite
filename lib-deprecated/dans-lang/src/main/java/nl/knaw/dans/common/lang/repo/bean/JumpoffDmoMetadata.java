package nl.knaw.dans.common.lang.repo.bean;

import java.net.URI;

import nl.knaw.dans.common.lang.repo.MetadataUnitXMLBean;

public interface JumpoffDmoMetadata extends MetadataUnitXMLBean
{

    public enum MarkupVersionID
    {
        HTML_MU, TEXT_MU
    }

    String UNIT_ID = "JOMD";

    String UNIT_LABEL = "Descriptive metadata for this markup";

    String UNIT_FORMAT = "http://easy.dans.knaw.nl/easy/jumpoff-metadata/";

    URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);

    MarkupMetadata getDefaultMarkupMetadata();

    MarkupMetadata getHtmlMarkupMetadata();

    MarkupMetadata getTextMarkupMetadata();

    MarkupVersionID getDefaultMarkupVersionID();

    void setDefaultMarkupVersionID(MarkupVersionID mvId);

    void toggleEditorMode();

    boolean isInHtmlMode();

}
