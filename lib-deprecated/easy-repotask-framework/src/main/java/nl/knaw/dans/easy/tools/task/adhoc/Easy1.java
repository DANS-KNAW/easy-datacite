package nl.knaw.dans.easy.tools.task.adhoc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollection;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineCollectionImpl;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineMetadata;
import nl.knaw.dans.easy.tools.exceptions.FatalException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Easy1 {

    private static final Logger logger = LoggerFactory.getLogger(Easy1.class);

    private static DisciplineContainer ROOT;
    private static Map<String, DisciplineContainer> OI_DISCIPLINEMAP;
    private static Map<String, DisciplineContainer> CAT_ID_DISCIPLINEMAP;
    private static Set<String> TWIPS_CATS;

    private static DisciplineContainer getRootDiscipline() throws FatalException {
        if (ROOT == null) {
            DisciplineCollection dc = DisciplineCollectionImpl.getInstance();
            try {
                ROOT = dc.getRootDiscipline();
            }
            catch (DomainException e) {
                throw new FatalException(e);
            }
            if (ROOT == null) {
                throw new FatalException("No root. Toeterdetoet.");
            }
        }
        return ROOT;
    }

    // gets a Map: key = OI-code, value = discipline.
    public static Map<String, DisciplineContainer> getOIDisciplineMap() throws FatalException {
        if (OI_DISCIPLINEMAP == null) {
            logger.info("Initializing OI_DISCIPLINEMAP");
            OI_DISCIPLINEMAP = new HashMap<String, DisciplineContainer>();
            try {
                collectOIDisciplines(getRootDiscipline(), OI_DISCIPLINEMAP);
            }
            catch (DomainException e) {
                throw new FatalException(e);
            }
            addStupidKeys(OI_DISCIPLINEMAP);
        }
        return OI_DISCIPLINEMAP;
    }

    private static void addStupidKeys(Map<String, DisciplineContainer> map) {
        DisciplineContainer container = map.get("D34000");
        map.put("history", container);

        container = map.get("D37000");
        map.put("archeology", container);

        container = map.get("D35100");
        map.put("historyofart", container);

        container = map.get("D36000");
        map.put("language", container);

        container = map.get("D33000");
        map.put("theology", container);

        container = map.get("D43000");
        map.put("economicsbusiness", container);

        container = map.get("D42000");
        map.put("publicadmin", container);

        container = map.get("D41000");
        map.put("law", container);

        container = map.get("D50000");
        map.put("behavioral", container);

        container = map.get("D51000");
        map.put("psychology", container);

        container = map.get("D51000");
        map.put("psychology", container);

        container = map.get("D61000");
        map.put("sociology", container);
    }

    private static void collectOIDisciplines(DisciplineContainer container, Map<String, DisciplineContainer> map) throws DomainException {
        DisciplineMetadata dmd = container.getDisciplineMetadata();
        map.put(dmd.getOICode(), container);
        for (DisciplineContainer c : container.getSubDisciplines()) {
            collectOIDisciplines(c, map);
        }
    }

    // get a Map: key = Easy1BranchId, value = disciplineContainer
    public static Map<String, DisciplineContainer> getCatIdDisciplineMap() throws FatalException {
        if (CAT_ID_DISCIPLINEMAP == null) {
            logger.info("Initializing CAT_ID_DISCIPLINEMAP");
            CAT_ID_DISCIPLINEMAP = new HashMap<String, DisciplineContainer>();
            try {
                collectCatIdDisciplines(getRootDiscipline(), CAT_ID_DISCIPLINEMAP);
            }
            catch (DomainException e) {
                throw new FatalException(e);
            }
        }
        return CAT_ID_DISCIPLINEMAP;
    }

    private static void collectCatIdDisciplines(DisciplineContainer container, Map<String, DisciplineContainer> map) throws DomainException {
        DisciplineMetadata dmd = container.getDisciplineMetadata();
        map.put(dmd.getEasy1BranchID(), container);
        for (DisciplineContainer c : container.getSubDisciplines()) {
            collectCatIdDisciplines(c, map);
        }
    }

    public static Set<String> getPublicTwipsCats() throws FatalException {
        if (TWIPS_CATS == null) {
            TWIPS_CATS = new HashSet<String>();
            Map<String, DisciplineContainer> oiMap = getOIDisciplineMap();
            for (String oiCode : oiMap.keySet()) {
                if (!StringUtils.isBlank(oiCode)) {
                    DisciplineContainer dc = oiMap.get(oiCode);
                    TWIPS_CATS.add(dc.getDisciplineMetadata().getEasy1BranchID());
                }
            }
        }
        return TWIPS_CATS;
    }

    public static boolean hasPublicCategory(List<String> assignedCategories) throws FatalException {
        boolean found = false;
        Iterator<String> iter = assignedCategories.iterator();
        while (iter.hasNext() && !found) {
            String twipsId = iter.next();
            found = getPublicTwipsCats().contains(twipsId);
        }
        return found;
    }

}
