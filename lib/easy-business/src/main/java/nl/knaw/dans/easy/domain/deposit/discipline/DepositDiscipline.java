package nl.knaw.dans.easy.domain.deposit.discipline;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.form.FormDescriptor;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific;

public interface DepositDiscipline extends Serializable {
    public static final String EMD_DEPOSITFORM_WIZARD = "emd-deposit-wizard";

    public static final String EMD_DEPOSITFORM_ARCHIVIST = "emd-archivist-deposit-form";

    public static final String EMD_VIEW_DEFINITION = "emd-view-definition";

    FormDescriptor getEmdFormDescriptor();

    String getVersion();

    String getDepositDisciplineId();

    ApplicationSpecific.MetadataFormat getMetadataFormat();

    String getOrdinal();

}
