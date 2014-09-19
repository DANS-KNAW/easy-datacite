package nl.knaw.dans.easy.domain.form;

public class TermPanelDefinition extends StandardPanelDefinition {

    private static final long serialVersionUID = 2570576641854058858L;

    private String namespacePrefix;
    private String termName;

    protected TermPanelDefinition() {
        super();
    }

    public TermPanelDefinition(String panelId, String namespacePrefix, String termName) {
        super(panelId);
        this.namespacePrefix = namespacePrefix;
        this.termName = termName;
    }

    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    protected synchronized TermPanelDefinition clone() {
        TermPanelDefinition clone = new TermPanelDefinition(getId(), namespacePrefix, termName);
        super.clone(clone);
        clone.namespacePrefix = namespacePrefix;
        clone.termName = termName;
        return clone;
    }

}
