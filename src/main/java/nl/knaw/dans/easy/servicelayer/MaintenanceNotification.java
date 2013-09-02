package nl.knaw.dans.easy.servicelayer;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.model.Dataset;

public final class MaintenanceNotification extends DatasetNotification implements Serializable
{
    public MaintenanceNotification(final Dataset dataset)
    {
        super(dataset);
    }

    String getTemplateLocation()
    {
        return "maintenance/maintenaceNotification";
    }
}
