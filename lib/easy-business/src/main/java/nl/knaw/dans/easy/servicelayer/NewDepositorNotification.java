package nl.knaw.dans.easy.servicelayer;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class NewDepositorNotification extends DatasetNotification implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final EasyUser newDepositor;
    private final EasyUser oldDepositor;

    public NewDepositorNotification(final Dataset dataset, final EasyUser oldDepositor, final EasyUser newDepositor) {
        super(dataset, newDepositor);
        this.oldDepositor = oldDepositor;
        this.newDepositor = newDepositor;
    }

    public String getOldDisplayName() {
        return oldDepositor.getDisplayName();
    }

    public String getNewDisplayName() {
        return newDepositor.getDisplayName();
    }

    String getTemplateLocation() {
        return "deposit/newDepositorNotification";
    }
}
