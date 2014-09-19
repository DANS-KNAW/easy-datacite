package nl.knaw.dans.common.lang.repo.relations;

import java.io.Serializable;

public abstract class OntologyNamespace implements Serializable {

    private static final long serialVersionUID = 4751925235866844666L;

    public String uri;

    public String prefix;

}
