package nl.knaw.dans.easy.mock;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.DmoNamespace;

public class StoreIdGenerator {
    private final Map<DmoNamespace, Integer> counters = new HashMap<DmoNamespace, Integer>();

    String getNext(final DmoNamespace namespace) {
        if (counters.containsKey(namespace))
            counters.put(namespace, counters.get(namespace) + 1);
        else
            counters.put(namespace, 1);
        return namespace + ":" + counters.get(namespace);
    }
}
