package nl.knaw.dans.easy.servicelayer;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.model.Dataset;

public final class RepublishNotification extends DatasetNotification implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public RepublishNotification(final Dataset dataset) {
        super(dataset);
    }

    String getTemplateLocation() {
        return "publish/republishNotification";
    }
}
