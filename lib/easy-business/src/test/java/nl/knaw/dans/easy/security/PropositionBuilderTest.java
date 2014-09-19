package nl.knaw.dans.easy.security;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.security.DatasetStateCheck;
import nl.knaw.dans.easy.security.DmoNamespaceCheck;
import nl.knaw.dans.easy.security.EmbargoFreeCheck;
import nl.knaw.dans.easy.security.PropositionBuilder;

import org.junit.Test;

public class PropositionBuilderTest {

    @Test
    public void creatProposition() {
        String prop = PropositionBuilder.createProposition(" OR ", new DatasetStateCheck(DatasetState.DELETED), new DmoNamespaceCheck(Dataset.NAMESPACE),
                new EmbargoFreeCheck());
        assertEquals("([Dataset state is DELETED] OR [storeId is within namespace easy-dataset] OR [Dataset is not under embargo at current date])", prop);
    }

}
