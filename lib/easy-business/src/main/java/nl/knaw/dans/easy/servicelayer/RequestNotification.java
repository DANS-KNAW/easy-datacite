package nl.knaw.dans.easy.servicelayer;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class RequestNotification extends DatasetNotification implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final PermissionRequestModel request;
    private final EasyUser requester;

    public RequestNotification(Dataset dataset, EasyUser requester, PermissionRequestModel request) {
        // NB: requester after the request as super should not mistake it as the receiver of the message
        super(dataset, request, requester);
        this.requester = requester;
        this.request = request;
    }

    String getTemplateLocation() {
        return "permission/requestNotification";
    }

    public String getDepositorName() {
        return getDataset().getDepositor().getDisplayName();
    }

    public String getRequestLink() {
        return request.getRequestLink();
    }

    public String getPermissionsTabLink() {
        return request.getPermissionsTabLink();
    }

    public String getRequesterName() {
        return requester.getDisplayName();
    }

    public String getDatasetTitle() {
        return getDataset().getPreferredTitle();
    }
}
