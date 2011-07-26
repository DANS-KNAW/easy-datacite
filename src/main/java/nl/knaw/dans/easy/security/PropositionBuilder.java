package nl.knaw.dans.easy.security;

import java.util.Arrays;
import java.util.List;

public final class PropositionBuilder
{

    private static final String SMALL_OR = " or ";
    
    private static final String OR       = " OR ";

    private static final String AND      = " AND ";

    private PropositionBuilder()
    {
        // never instantiate
    }

    public static String buildOrProposition(String prefix, Object[] objects)
    {
        return createProposition(prefix, Arrays.asList(objects), SMALL_OR);
    }

    public static String buildOrProposition(String prefix, List<?> list)
    {
        return createProposition(prefix, list, SMALL_OR);
    }

    public static String buildOrProposition(SecurityOfficer...officers)
    {
        return createProposition(OR, officers);
    }

    public static String buildAndProposition(SecurityOfficer...officers)
    {
        return createProposition(AND, officers);
    }
    
    protected static String createProposition(String operatorString, SecurityOfficer...officers)
    {
        StringBuilder sb = new StringBuilder("(");
        int i;
        for (i = 0; i < officers.length - 1; i++)
        {
            sb.append(officers[i].getProposition()).append(operatorString);
        }
        sb.append(officers[i].getProposition()).append(")");
        return sb.toString();
    }

    private static String createProposition(String prefix, List<?> list, String operatorString)
    {
        StringBuilder sb = new StringBuilder("[");
        sb.append(prefix);
        sb.append(" ");
        int l = list.size();
        for (int i = 0; i < l; i++)
        {
            sb.append(list.get(i));
            if (i < l - 1)
            {
                sb.append(operatorString);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Like <code>[Update actions are confined to { DELETE, RENAME } ]</code>.
     * 
     * @param prefix
     *        the prefix
     * @param list
     *        the collection
     * @return formatted proposition
     */
    public static String buildCollectionProposition(String prefix, List<?> list)
    {
        StringBuilder sb = new StringBuilder("[");
        sb.append(prefix);
        sb.append(" { ");
        int l = list.size();
        for (int i = 0; i < l; i++)
        {
            sb.append(list.get(i));
            if (i < l - 1)
            {
                sb.append(", ");
            }
        }
        sb.append(" } ]");
        return sb.toString();
    }

}
