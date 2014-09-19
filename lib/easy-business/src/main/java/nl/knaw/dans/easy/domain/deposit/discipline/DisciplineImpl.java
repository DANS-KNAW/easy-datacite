package nl.knaw.dans.easy.domain.deposit.discipline;

import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

public class DisciplineImpl implements DepositDiscipline {

    private static final long serialVersionUID = -7163192410717007791L;

    private final FormDescriptor emdFromDescriptor;

    public DisciplineImpl(FormDescriptor formDescriptor) {
        this.emdFromDescriptor = formDescriptor;
    }

    public FormDescriptor getEmdFormDescriptor() {
        return emdFromDescriptor;
    }

    public String getDepositDisciplineId() {
        return emdFromDescriptor.getId();
    }

    public MetadataFormat getMetadataFormat() {
        return ApplicationSpecific.formatForName(getDepositDisciplineId());
    }

    public String getLabelResourceKey() {
        return emdFromDescriptor.getLabelResourceKey();
    }

    public String getVersion() {
        return emdFromDescriptor.getVersion();
    }

    public String getOrdinal() {
        return emdFromDescriptor.getOrdinal();
    }

}
