package nl.knaw.dans.easy.tools.task.dump;

import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;

public class AmdPrinter {
    private final AdministrativeMetadata metadata;

    AmdPrinter(AdministrativeMetadata metadata) {
        this.metadata = metadata;
    }

    private String property(String key, String value) {
        return String.format("AMD:%s=%s\n", key, new CrlfEscapedString(value));
    }

    public String toString() {
        // @formatter:off
        return property("datasetState", metadata.getAdministrativeState().toString()) + property("depositor", metadata.getDepositorId());
        // @formatter:on
    }
}
