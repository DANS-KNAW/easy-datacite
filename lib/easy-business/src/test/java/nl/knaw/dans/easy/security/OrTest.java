package nl.knaw.dans.easy.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.DatasetStateCheck;
import nl.knaw.dans.easy.security.DmoNamespaceCheck;
import nl.knaw.dans.easy.security.EmbargoFreeCheck;
import nl.knaw.dans.easy.security.FileItemContentsAccessCheck;
import nl.knaw.dans.easy.security.HasRoleCheck;
import nl.knaw.dans.easy.security.Or;
import nl.knaw.dans.easy.security.SecurityOfficer;

import org.junit.Test;

public class OrTest {

    @Test()
    public void arrayConstructorOne() {
        SecurityOfficer or = new Or(new DatasetStateCheck(DatasetState.DELETED));
        assertEquals("([Dataset state is DELETED])", or.getProposition());
    }

    @Test
    public void arrayConstructor() {
        SecurityOfficer or = new Or(new DatasetStateCheck(DatasetState.DELETED), new DmoNamespaceCheck(Dataset.NAMESPACE));
        assertTrue(or.getProposition().contains("[Dataset state is DELETED] OR [storeId is within namespace easy-dataset]"));

        or = new Or(new DatasetStateCheck(DatasetState.DELETED), new DmoNamespaceCheck(Dataset.NAMESPACE), new EmbargoFreeCheck());
        assertTrue(or.getProposition().contains("[Dataset state is DELETED] OR [storeId is within namespace easy-dataset]"));

        or = new Or(new DatasetStateCheck(DatasetState.DELETED), new DmoNamespaceCheck(Dataset.NAMESPACE), new EmbargoFreeCheck(),
                new FileItemContentsAccessCheck());
        assertTrue(or.getProposition().contains("[Dataset state is DELETED] OR [storeId is within namespace easy-dataset]"));

        or = new Or(new DatasetStateCheck(DatasetState.DELETED), new DmoNamespaceCheck(Dataset.NAMESPACE), new EmbargoFreeCheck(),
                new FileItemContentsAccessCheck(), new HasRoleCheck(Role.ADMIN));
        assertTrue(or.getProposition().contains("[Dataset state is DELETED] OR [storeId is within namespace easy-dataset]"));
    }

}
