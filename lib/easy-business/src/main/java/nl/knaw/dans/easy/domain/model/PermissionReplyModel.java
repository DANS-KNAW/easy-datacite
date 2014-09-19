package nl.knaw.dans.easy.domain.model;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.model.PermissionSequence.State;

public class PermissionReplyModel implements Serializable {

    private static final long serialVersionUID = -7574506491836970859L;

    public static final String STATE = "state";

    public static final String EXPLANATION = "explanation";

    private final String requesterId;
    private String explanation;
    private State state;

    private String requestLink;
    private String datasetLink;

    public PermissionReplyModel(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public String getRequestLink() {
        return requestLink;
    }

    public void setRequestLink(String requestLink) {
        this.requestLink = requestLink;
    }

    public String getDatasetLink() {
        return datasetLink;
    }

    public void setDatasetLink(String datasetLink) {
        this.datasetLink = datasetLink;
    }

}
