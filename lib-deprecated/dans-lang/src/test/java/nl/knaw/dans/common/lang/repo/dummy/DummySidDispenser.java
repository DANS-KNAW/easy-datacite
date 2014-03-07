package nl.knaw.dans.common.lang.repo.dummy;

import java.util.HashMap;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.SidDispenser;

public class DummySidDispenser implements SidDispenser
{
    private HashMap<DmoNamespace, Integer> seq = new HashMap<DmoNamespace, Integer>();

    public String nextSid(DmoNamespace objectNamespace) throws RepositoryException
    {
        Integer no = seq.get(objectNamespace);
        if (no == null)
            no = new Integer(1);
        else
            no++;
        seq.put(objectNamespace, no);
        return objectNamespace.getValue() + ":" + no;
    }

}
