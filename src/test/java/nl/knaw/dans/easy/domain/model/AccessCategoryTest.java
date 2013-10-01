package nl.knaw.dans.easy.domain.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.util.StateUtil;

import org.junit.Test;

public class AccessCategoryTest
{

    @Test
    public void getBitMask()
    {
        for (int i = 0; i < 64; i++)
        {
            List<AccessCategory> categories = AccessCategory.UTIL.getStates(i);
            // System.out.println(i + " " + categories);
            assertEquals(i, AccessCategory.UTIL.getBitMask(categories));
        }
    }

    @Test
    public void inaccessible()
    {
        for (AccessCategory ac : AccessCategory.values())
        {
            // System.out.println(ac + " " + AccessCategory.isAccessible(ac));
        }
    }

    @Test
    public void stateUtil()
    {
        StateUtil<AccessCategory> su = new StateUtil<AccessCategory>(AccessCategory.values());
        for (int i = 0; i < 64; i++)
        {
            List<AccessCategory> categories = su.getStates(i);
            assertEquals(i, su.getBitMask(categories));
        }
    }
}
