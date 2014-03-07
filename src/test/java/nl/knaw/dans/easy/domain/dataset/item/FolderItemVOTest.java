package nl.knaw.dans.easy.domain.dataset.item;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.model.AccessibleTo;

import org.junit.Test;

public class FolderItemVOTest
{

    @Test
    public void setAccessibleToes()
    {
        FolderItemVO fovo = new FolderItemVO();
        fovo.setSid("easy-folder:123");
        List<AccessibleTo> accessibleToes = new ArrayList<AccessibleTo>();
        accessibleToes.add(AccessibleTo.RESTRICTED_GROUP);
        accessibleToes.add(AccessibleTo.RESTRICTED_GROUP);

        fovo.setAccessibleToes(accessibleToes);
        assertEquals(1, fovo.getAccessibleToList().size());
    }

}
