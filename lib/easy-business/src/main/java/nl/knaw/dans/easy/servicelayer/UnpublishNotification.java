package nl.knaw.dans.easy.servicelayer;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.model.Dataset;

public final class UnpublishNotification extends DatasetNotification implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public UnpublishNotification(final Dataset dataset)
    {
        super(dataset);
    }

    String getTemplateLocation()
    {
        return "publish/unpublishNotification";
    }
}
