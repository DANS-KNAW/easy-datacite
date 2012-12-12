package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.EmdScheme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container for resource properties of category rights.
 *
 * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-rights">dcmi-terms/#terms-rights</a>
 * @author ecco
 */
public class EmdRights extends AbstractEmdContainer
{

    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.RIGHTS, Term.Namespace.DC, BasicString.class),
            new Term(Term.Name.ACCESSRIGHTS, Term.Namespace.DCTERMS, BasicString.class),
            new Term(Term.Name.LICENSE, Term.Namespace.DCTERMS, BasicString.class), new Term(Term.Name.RIGHTSHOLDER, Term.Namespace.DCTERMS, BasicString.class)};

    public static final String RIGHTS = "";
    public static final String ACCESS_RIGHTS = "accessRights";
    public static final String LICENSE = "license";

    public static final String LICENSE_ACCEPT = "accept";
    public static final String SCHEME_LICENSE_ACCEPT_E2V1 = "Easy2 version 1";
    // used for migration corrections
    public static final String SCHEME_LICENSE_ACCEPT_E1V1 = "Easy version 1";

    //TODO: JUnit Test, if this key exist in accessrights.xml
    //public static final String OTHER_ACCESS_KEY = "ar_noAccess";

    /**
     *
     */
    private static final long serialVersionUID = 494785477616915696L;

    private static final Logger logger = LoggerFactory.getLogger(EmdRights.class);

    private List<BasicString> dcRights = new ArrayList<BasicString>();
    private List<BasicString> termsAccessRights = new ArrayList<BasicString>();
    private List<BasicString> termsLicense = new ArrayList<BasicString>();
    private List<BasicString> termsRightsHolder = new ArrayList<BasicString>();

    public Map<String, List<BasicString>> getRights()
    {
        Map<String, List<BasicString>> map = new HashMap<String, List<BasicString>>();
        map.put(RIGHTS, this.getDcRights());
        map.put(ACCESS_RIGHTS, this.getTermsAccessRights());
        map.put(LICENSE, this.getTermsLicense());

        return map;
    }

    public static final String[] LIST_KEYS = {RIGHTS, ACCESS_RIGHTS, LICENSE};

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms()
    {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'rights' in the "http://purl.org/dc/elements/1.1/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-rights">dcmi-terms/#terms-rights</a>
     * @return a list of resource properties
     */
    public List<BasicString> getDcRights()
    {
        return dcRights;
    }

    /**
     * Set a list of resource properties known as 'rights' in the "http://purl.org/dc/elements/1.1/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-rights">dcmi-terms/#terms-rights</a>
     * @param dcRights
     *        a list of resource properties
     */
    public void setDcRights(final List<BasicString> dcRights)
    {
        this.dcRights = dcRights;
    }

    /**
     * Get a list of resource properties known as 'accessRights' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-accessRights">dcmi-terms/#terms-accessRights</a>
     * @return a list of resource properties
     */
    public List<BasicString> getTermsAccessRights()
    {
        return termsAccessRights;
    }

    /**
     * Set a list of resource properties known as 'accessRights' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-accessRights">dcmi-terms/#terms-accessRights</a>
     * @param termsAccessRights
     *        a list of resource properties
     */
    public void setTermsAccessRights(final List<BasicString> termsAccessRights)
    {
        this.termsAccessRights = termsAccessRights;
    }

    /**
     * Get a list of resource properties known as 'license' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-license">dcmi-terms/#terms-license</a>
     * @return a list of resource properties
     */
    public List<BasicString> getTermsLicense()
    {
        if (termsLicense == null)
        {
            termsLicense = new ArrayList<BasicString>();
        }
        return termsLicense;
    }

    /**
     * Set a list of resource properties known as 'license' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-license">dcmi-terms/#terms-license</a>
     * @param termsLicense
     *        a list of resource properties
     */
    public void setTermsLicense(final List<BasicString> termsLicense)
    {
        this.termsLicense = termsLicense;
    }

    // not very strong. 
    // migrated datasets have the form
    // <dcterms:license eas:scheme="EASY version 1">accept</dcterms:license>
    // newly created ones have the form
    // <dcterms:license>accept</dcterms:license>
    // adding a eas:scheme-value would improve reliability.
    public boolean hasAcceptedLicense()
    {
        boolean accepted = false;
        for (BasicString bs : getTermsLicense())
        {
            if (LICENSE_ACCEPT.equals(bs.getValue()))
            {
                accepted = true;
                break;
            }
        }
        return accepted;
    }

    // Attention! this method is not yet (2011-08-03) used in the web-ui.
    public void setAcceptedLicense(boolean accepted)
    {
        setAcceptedLicense(accepted, SCHEME_LICENSE_ACCEPT_E2V1);
    }

    // Attention! only use for migration correction
    public void setAcceptedLicense(boolean accepted, String scheme)
    {
        if (accepted && !hasAcceptedLicense())
        {
            BasicString bs = new BasicString(LICENSE_ACCEPT);
            bs.setScheme(scheme);
            getTermsLicense().add(bs);
        }
        if (!accepted)
        {
            removeAcceptedLicense();
        }
    }

    private boolean removeAcceptedLicense()
    {
        BasicString accept = null;
        for (BasicString bs : getTermsLicense())
        {
            if (LICENSE_ACCEPT.equals(bs.getValue()))
            {
                accept = bs;
                break;
            }
        }
        if (accept != null)
        {
            getTermsLicense().remove(accept);
        }
        return accept != null;
    }

    public List<BasicString> getTermsRightsHolder()
    {
        return termsRightsHolder;
    }

    public void setTermsRightsHolder(final List<BasicString> termsRightsHolder)
    {
        this.termsRightsHolder = termsRightsHolder;
    }

    public AccessCategory getAccessCategory()
    {
        AccessCategory accessCat = null;
        if (!termsAccessRights.isEmpty())
        {
            try
            {
                accessCat = AccessCategory.valueOf(termsAccessRights.get(0).getValue().trim());
            }
            catch (IllegalArgumentException e)
            {
                logger.error("Incorrect metadata, unknown access category: ", e);
            }
        }
        return accessCat;
    }

    public void setAccessCategory(AccessCategory accessCat)
    {
        setAccessCategory(accessCat, EmdScheme.COMMON_DCTERMS_ACCESSRIGHTS.getId());
    }

    public void setAccessCategory(AccessCategory accessCat, String schemeId)
    {
        termsAccessRights.clear();
        BasicString bs = new BasicString(accessCat.toString());
        bs.setSchemeId(schemeId);
        termsAccessRights.add(bs);
    }

}
