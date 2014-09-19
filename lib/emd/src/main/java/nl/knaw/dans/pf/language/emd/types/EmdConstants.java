package nl.knaw.dans.pf.language.emd.types;

/**
 * Constants used in easymetadata types.
 * 
 * @author ecco
 */
public final class EmdConstants {

    // utility class
    private EmdConstants() {

    }

    /**
     * Value of the scheme attribute of elements representing dates, times or periods.
     * 
     * @author ecco
     */
    public enum DateScheme {
        /**
         * Indicates the date value describes a period.
         */
        Period,
        /**
         * Indicates the date value is in compliance with ISO8601.
         * 
         * @see <a href="http://www.w3.org/TR/NOTE-datetime">NOTE-datetime</a>
         */
        W3CDTF
    }

    public static final String SCHEME_DMO_ID = "DMO_ID";
    public static final String SCHEME_OAI_ITEM_ID = "OAI_ITEM_ID";
    public static final String SCHEME_AIP_ID = "AIP_ID";

    /**
     * Value of the scheme attribute of elements representing URI's.
     */
    public static final String SCHEME_URI = "URI";

    /**
     * Value of the scheme attribute of elements representing a file format or medium. IMT, the Internet media type of a resource.
     * 
     * @see <a href="http://www.iana.org/assignments/media-types/">http://www.iana.org/assignments/media-types/</a>
     */
    public static final String SCHEME_IMT = "IMT";

    /**
     * Value of the scheme attribute of elements representing a reference to a book.
     */
    public static final String SCHEME_ISBN = "ISBN";

    /**
     * Value of the scheme attribute of elements representing a persistent identifier that can be resolved by http://persistent-identifier.nl/.
     */
    public static final String SCHEME_PID = "PID";
    public static final String BRI_RESOLVER = "http://www.persistent-identifier.nl";

    /**
     * value of the scheme attribute of elements representing an author that can be identified through the 'Digital Author Identification' (DAI).
     */
    public static final String SCHEME_DAI = "DAI";

    /**
     * value of the scheme attribute of elements representing an identifier that points to Archis OMG_NR.
     */
    public static final String SCHEME_ARCHIS_ONDERZOEK_M_NR = "Archis_onderzoek_m_nr";

    public static final String SCHEME_ID_DISCIPLINES = "custom.disciplines";

}
