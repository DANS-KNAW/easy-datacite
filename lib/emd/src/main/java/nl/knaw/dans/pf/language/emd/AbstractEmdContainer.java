package nl.knaw.dans.pf.language.emd;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.pf.language.emd.Term.Name;
import nl.knaw.dans.pf.language.emd.exceptions.NoSuchTermException;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;
import nl.knaw.dans.pf.language.emd.util.StringUtil;

/**
 * Abstract EmdContainer.
 * 
 * @author ecco
 */
public abstract class AbstractEmdContainer implements EmdContainer {

    private static final long serialVersionUID = 8797904431054261189L;

    private static String getMethodName(final Term term) {
        final StringBuilder builder = new StringBuilder("get");
        builder.append(StringUtil.firstCharToUpper(term.getNamespace().prefix));
        builder.append(StringUtil.firstCharToUpper(term.getName().termName));
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String toString(final String separator) {
        return toString(separator, false);
    }

    /**
     * {@inheritDoc}
     */
    public String toString(final String separator, final boolean includeTerm) {
        final StringBuilder builder = new StringBuilder();
        for (Term term : getTerms()) {
            final List<?> list = getTermWithNamespace(term);
            if (includeTerm && !list.isEmpty()) {
                builder.append(EasyMetadata.DEFAULT_LINE_SEPERATOR);
                builder.append(term.getName().termName);
                builder.append(separator);
                builder.append(term.getNamespace().uri);
                builder.append(separator);
            } else if (builder.length() > 0) {
                builder.append(separator);
            }
            appendItems(separator, builder, list);
        }
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String toString(final String separator, final Term term) throws NoSuchTermException {
        final StringBuilder builder = new StringBuilder();
        appendItems(separator, builder, get(term));
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String toString(final String separator, final Name termName) throws NoSuchTermException {
        return toString(separator, new Term(termName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toString(EasyMetadata.DEFAULT_ITEM_SEPARATOR);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getValues() {
        final List<String> values = new ArrayList<String>();
        for (Term term : getTerms()) {
            final List<?> data = getTermWithNamespace(term);
            for (Object obj : data) {
                values.add(obj.toString());
            }
        }
        return values;
    }

    /**
     * {@inheritDoc}
     */
    public List<MetadataItem> get(final Term term) throws NoSuchTermException {
        if (term.getNamespace() == null) {
            return get(term.getName());
        } else {
            return getTermWithNamespace(term);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<MetadataItem> get(final Name termName) throws NoSuchTermException {
        final List<MetadataItem> list = new ArrayList<MetadataItem>();
        for (Term.Namespace namespace : Term.Namespace.values()) {
            try {
                list.addAll(getTermWithNamespace(new Term(termName, namespace)));
            }
            // ecco: CHECKSTYLE: OFF
            catch (final NoSuchTermException e) {
                // Do nothing
            }
            // ecco: CHECKSTYLE: ON
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        boolean empty = true;
        for (Term term : getTerms()) {
            final List<?> list = getTermWithNamespace(term);
            if (!list.isEmpty()) {
                empty = false;
                break;
            }
        }
        return empty;
    }

    @Override
    public int size() {
        int size = 0;
        for (Term term : getTerms()) {
            size += getTermWithNamespace(term).size();
        }
        return size;
    }

    private void appendItems(final String separator, final StringBuilder builder, final List<?> list) {
        for (Object obj : list) {
            builder.append(obj.toString());
            builder.append(separator);
        }
        final int length = builder.length();
        if (length > 0) {
            builder.delete(length - separator.length(), length);
        }
    }

    @SuppressWarnings("unchecked")
    private List<MetadataItem> getTermWithNamespace(final Term term) throws NoSuchTermException {
        List<MetadataItem> list = null;
        Method method = null;
        try {
            method = this.getClass().getDeclaredMethod(getMethodName(term));
        }
        catch (final NoSuchMethodException e) {
            final String msg = "Unknown term: " + term == null ? "null" : term.toString();
            throw new NoSuchTermException(msg, e);
        }

        try {
            list = (List<MetadataItem>) method.invoke(this);
        }
        catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (final InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
