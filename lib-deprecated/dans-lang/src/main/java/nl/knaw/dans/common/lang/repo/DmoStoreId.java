package nl.knaw.dans.common.lang.repo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmoStoreId implements Serializable, Comparable<DmoStoreId> {
    private static final Logger log = LoggerFactory.getLogger(DmoStoreId.class);
    private static final long serialVersionUID = 7206006418091467783L;

    public static final String SEPARATOR = ":";

    private static final String REG_EX = "[a-zA-Z0-9-]*";
    private static final Pattern PATTERN = Pattern.compile(REG_EX);

    private final DmoNamespace namespace;

    private final String id;

    private final String storeId;

    public static boolean isValidId(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return PATTERN.matcher(value).matches();
    }

    public static String[] split(String storeId) {
        if (StringUtils.isBlank(storeId)) {
            throw new IllegalArgumentException("StoreId cannot be blank.");
        }
        String[] split = storeId.trim().split(SEPARATOR);
        if (split.length < 2) {
            throw new IllegalArgumentException("Not a valid storeId: " + storeId);
        }
        if (!DmoNamespace.isValidNamespace(split[0]) || !isValidId(split[1])) {
            throw new IllegalArgumentException("Not a valid storeId: " + storeId);
        }
        return split;
    }

    public static String getStoreId(DmoNamespace namespace, String id) {
        if (!isValidId(id)) {
            throw new IllegalArgumentException("Not a valid id: " + id);
        }
        return new StringBuilder(namespace.getValue()).append(SEPARATOR).append(id).toString();
    }

    public static List<String> asStrings(Collection<DmoStoreId> dmoStoreIds) {
        List<String> storeIds = new ArrayList<String>();
        for (DmoStoreId dmoStoreId : dmoStoreIds) {
            storeIds.add(dmoStoreId.getStoreId());
        }
        return storeIds;
    }

    public static DmoNamespace getDmoNamespace(String storeId) {
        return new DmoNamespace(split(storeId)[0]);
    }

    /**
     * Return an instance of DmoStoreId if the given String <code>storeId</code> is a valid storeId, <code>null</code> otherwise.
     * 
     * @param storeId
     *        String-occurrence of storeId
     * @return new DmoStoreId if <code>storeId</code> is valid, <code>null</code> otherwise.
     */
    public static DmoStoreId newDmoStoreId(String storeId) {
        DmoStoreId dmoStoreId = null;
        try {
            dmoStoreId = new DmoStoreId(storeId);
        }
        catch (IllegalArgumentException e) {
            log.warn("Not a valid storeId: {}", storeId);
        }
        return dmoStoreId;
    }

    public DmoStoreId(DmoNamespace namespace, String id) {
        this.namespace = namespace;
        this.id = id;
        this.storeId = getStoreId(this.namespace, this.id);
    }

    public DmoStoreId(String storeId) {
        String[] split = split(storeId);
        this.namespace = new DmoNamespace(split[0]);
        this.id = split[1];
        this.storeId = getStoreId(this.namespace, this.id);
    }

    public DmoNamespace getNamespace() {
        return namespace;
    }

    public boolean isInNamespace(DmoNamespace namspace) {
        return this.namespace.equals(namspace);
    }

    public boolean hasSameNamespace(DmoStoreId other) {
        return this.namespace.equals(other.namespace);
    }

    public String getId() {
        return id;
    }

    public String getStoreId() {
        return storeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DmoStoreId) {
            DmoStoreId other = (DmoStoreId) obj;
            return namespace.equals(other.namespace) && id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode() % 100;
    }

    @Override
    public String toString() {
        return storeId;
    }

    @Override
    public int compareTo(DmoStoreId other) {
        int compare = this.namespace.compareTo(other.namespace);
        if (compare == 0) {
            compare = this.id.compareTo(other.id);
        }
        return compare;
    }

}
