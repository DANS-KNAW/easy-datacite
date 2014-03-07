package nl.knaw.dans.common.lang.util;

import java.util.ArrayList;
import java.util.List;

public class StateUtil<T extends Enum<T>>
{

    private final T[] values;

    public StateUtil(T[] values)
    {
        this.values = values;
    }

    public int getBitMask(List<T> states)
    {
        int b = 0;
        if (states != null)
        {
            for (T state : states)
            {
                b |= 1 << state.ordinal();
            }
        }
        return b;
    }

    public int getBitMask(T... states)
    {
        int b = 0;
        if (states != null)
        {
            for (T state : states)
            {
                b |= 1 << state.ordinal();
            }
        }
        return b;
    }

    public List<T> getStates(int bitMask)
    {
        List<T> states = new ArrayList<T>();
        for (T state : values)
        {
            int mask = 1 << state.ordinal();
            if ((mask & bitMask) == mask)
            {
                states.add(state);
            }
        }
        return states;
    }

}
