package nl.knaw.dans.easy.domain.model;

import java.net.URI;

public interface DatasetItemContainerMetadata extends DatasetItemMetadata {

    String UNIT_ID = "EASY_ITEM_CONTAINER_MD";

    String UNIT_LABEL = "Metadata for this item container";

    String UNIT_FORMAT = "http://easy.dans.knaw.nl/easy/item-container-md/";

    URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);
}
