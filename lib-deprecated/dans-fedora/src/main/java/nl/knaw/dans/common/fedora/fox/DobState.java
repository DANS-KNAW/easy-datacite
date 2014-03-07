/**
 *
 */
package nl.knaw.dans.common.fedora.fox;

/**
 * Indicates the state of a Digital Object.
 *
 * @author ecco
 */
public enum DobState
{
    /**
     * Indicates a Digital Object is <i>Active</i>.
     */
    Active("A"),
    /**
     * Indicates a Digital Object is <i>Inactive</i>.
     */
    Inactive("I"),
    /**
     * Indicates a Digital Object is <i>Deleted</i>.
     */
    Deleted("D");

    public final String fedoraQuirck;

    private DobState(String fedoraQuirck)
    {
        this.fedoraQuirck = fedoraQuirck;
    }

    // Fedora uses 'Active' and 'A' in unpredictable ways.
    public static DobState valueFor(String s)
    {
        if ("Active".equalsIgnoreCase(s) || "A".equalsIgnoreCase(s))
        {
            return Active;
        }
        else if ("Inactive".equalsIgnoreCase(s) || "I".equalsIgnoreCase(s))
        {
            return Inactive;
        }
        else if ("Deleted".equalsIgnoreCase(s) || "D".equalsIgnoreCase(s))
        {
            return Deleted;
        }
        else
        {
            return null;
        }
    }
}
