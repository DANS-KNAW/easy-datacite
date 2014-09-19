package nl.knaw.dans.c.dmo.collections.xml;

import nl.knaw.dans.common.jibx.bean.JiBXRecursiveEntry;
import nl.knaw.dans.common.jibx.bean.JiBXRecursiveList;
import nl.knaw.dans.common.lang.repo.bean.RecursiveEntry;
import nl.knaw.dans.common.lang.repo.bean.RecursiveList;
import nl.knaw.dans.common.lang.repo.bean.RecursiveNode;
import nl.knaw.dans.i.dmo.collections.DmoCollection;

public class RecursiveListConverter {

    private static int ordinal;

    public static synchronized RecursiveList convert(DmoCollection collection) {
        RecursiveList recursiveList = new JiBXRecursiveList(collection.getDmoNamespace().getValue());
        ordinal = 0;
        doConvert(collection, recursiveList);
        return recursiveList;
    }

    private static void doConvert(DmoCollection collection, RecursiveNode node) {
        RecursiveEntry entry = new JiBXRecursiveEntry();
        entry.setKey(collection.getStoreId());
        entry.setName(collection.getLabel());
        entry.setShortname(collection.getShortName());
        entry.setOrdinal(ordinal++);
        node.add(entry);
        for (DmoCollection kid : collection.getChildren()) {
            doConvert(kid, entry);
        }

    }

}
