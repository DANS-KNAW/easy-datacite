package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

/**
 * Container for resource properties of category identifier.
 * 
 * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-identifier">dcmi-terms/#terms-identifier</a>
 * @author ecco
 */
public class EmdIdentifier extends AbstractEmdContainer {

    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.IDENTIFIER, Term.Namespace.DC, BasicIdentifier.class)};

    /**
     *
     */
    private static final long serialVersionUID = 3070177959698116392L;

    private List<BasicIdentifier> dcIdentifier = new ArrayList<BasicIdentifier>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms() {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'identifier' in the "http://purl.org/dc/elements/1.1/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-identifier">dcmi-terms/#terms-identifier</a>
     * @return a list of resource properties
     */
    public List<BasicIdentifier> getDcIdentifier() {
        return dcIdentifier;
    }

    /**
     * Set a list of resource properties known as 'identifier' in the "http://purl.org/dc/elements/1.1/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-identifier">dcmi-terms/#terms-identifier</a>
     * @param dcIdentifier
     *        a list of resource properties
     */
    public void setDcIdentifier(final List<BasicIdentifier> dcIdentifier) {
        this.dcIdentifier = dcIdentifier;
    }

    public void add(BasicIdentifier bi) {
        dcIdentifier.add(bi);
    }

    public BasicIdentifier getIdentifier(String scheme) {
        BasicIdentifier bi = null;
        for (BasicIdentifier identifier : dcIdentifier) {
            if (scheme.equals(identifier.getScheme())) {
                bi = identifier;
                break;
            }
        }
        return bi;
    }

    public List<BasicIdentifier> getAllIdentfiers(String scheme) {
        List<BasicIdentifier> biList = new ArrayList<BasicIdentifier>();
        for (BasicIdentifier identifier : dcIdentifier) {
            if (scheme.equals(identifier.getScheme())) {
                biList.add(identifier);
            }
        }
        return biList;
    }

    public void removeIdentifier(String scheme) {
        BasicIdentifier bi = getIdentifier(scheme);
        if (bi != null) {
            dcIdentifier.remove(bi);
        }
    }

    public List<BasicIdentifier> removeAllIdentifiers(String scheme) {
        List<BasicIdentifier> biList = getAllIdentfiers(scheme);
        for (BasicIdentifier bi : biList) {
            dcIdentifier.remove(bi);
        }
        return biList;
    }

    public void setDatasetId(String datasetId) {
        removeAllIdentifiers(EmdConstants.SCHEME_DMO_ID);
        BasicIdentifier bi = new BasicIdentifier(datasetId);
        bi.setScheme(EmdConstants.SCHEME_DMO_ID);
        dcIdentifier.add(bi);

        // removeIdentifier(EmdConstants.SCHEME_OAI_ITEM_ID);
        // BasicIdentifier bioai = new BasicIdentifier(Constants.OAI_IDENTIFIER_PREFIX + datasetId);
        // bioai.setScheme(EmdConstants.SCHEME_OAI_ITEM_ID);
        // dcIdentifier.add(bioai);
    }

    public String getDatasetId() {
        String datasetId = null;
        BasicIdentifier bi = getIdentifier(EmdConstants.SCHEME_DMO_ID);
        if (bi != null) {
            datasetId = bi.getValue();
        }
        return datasetId;
    }

    public String getAipId() {
        String aipId = null;
        BasicIdentifier bi = getIdentifier(EmdConstants.SCHEME_AIP_ID);
        if (bi != null) {
            aipId = bi.getValue();
        }
        return aipId;
    }

    public String getPersistentIdentifier() {
        String pid = null;
        BasicIdentifier bi = getIdentifier(EmdConstants.SCHEME_PID);
        if (bi != null) {
            pid = bi.getValue();
        }
        return pid;
    }

    public String getArchisOnderzoeksMeldingsNummer() {
        String pid = null;
        BasicIdentifier bi = getIdentifier(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);
        if (bi != null) {
            pid = bi.getValue();
        }
        return pid;
    }

}
