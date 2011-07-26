package nl.knaw.dans.commons.pid;

import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.*;

public class PidCalculatorTest
{
    @Test
    public void length()
    {
        assertThat(Long.toString(PidCaculator.MODULO, PidConverter.RADIX).length(),
                equalTo(PidConverter.ID_LENGTH));
    }

    @Test
    @Ignore("takes several minutes")
    public void roundTrip()
    {
        long seed = 0;
        long count = 0;
        while ((++count) < PidCaculator.MODULO
                && Long.toString(seed, PidConverter.RADIX).length() <= PidConverter.ID_LENGTH)
        {
            seed = PidCaculator.getNext(seed);
            if (seed == 0)
                break;
            if (count % 5000000 == 0)
            {
                long l = PidCaculator.MODULO-count;
                System.out.println(l+" "+PidCaculator.MODULO+" "+count+" "+seed);
            }
        }
        assertThat(count, equalTo(PidCaculator.MODULO));
    }

    @Test
    @Ignore("larger max throws OutOfMemoryError")
    public void roundTrip2()
    {
        final int max = 1449066;
        final HashSet<Long> ids = new HashSet<Long>();
        long seed = 0;
        while (!ids.contains(seed) && ids.size() < max
                && Long.toString(seed, PidConverter.RADIX).length() <= PidConverter.ID_LENGTH)
        {
            ids.add(seed);
            seed = PidCaculator.getNext(seed);
        }
        assertThat(ids.size(), equalTo(max));
    }
}
