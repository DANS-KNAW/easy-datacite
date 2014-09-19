package nl.knaw.dans.common.fedora.fox;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;

import org.joda.time.DateTime;

public class AuditRecord extends AbstractJiBXObject<AuditRecord> {
    private static final long serialVersionUID = 8189909610042140432L;

    private String id;
    private String processType;
    private String action;
    private String componentId;
    private String responsibility;
    private DateTime dateTime;
    private String justification;

    public String getId() {
        return id;
    }

    public String getProcessType() {
        return processType;
    }

    public String getAction() {
        return action;
    }

    public String getComponentId() {
        return componentId;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public String getJustification() {
        return justification;
    }

}
