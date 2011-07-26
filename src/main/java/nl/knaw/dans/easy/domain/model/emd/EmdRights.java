package nl.knaw.dans.easy.domain.model.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;
import nl.knaw.dans.easy.domain.model.emd.types.EmdConstants;

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
    static final Term[] TERMS =
    {
        new Term(Term.Name.RIGHTS, Term.Namespace.DC, BasicString.class),
        new Term(Term.Name.ACCESSRIGHTS, Term.Namespace.DCTERMS, BasicString.class),
        new Term(Term.Name.LICENSE, Term.Namespace.DCTERMS, BasicString.class),
        new Term(Term.Name.RIGHTSHOLDER, Term.Namespace.DCTERMS, BasicString.class)
    };

    public static final String RIGHTS = "";
    public static final String ACCESS_RIGHTS = "accessRights";
    public static final String LICENSE = "license";
    
    public static final String LICENSE_ACCEPT = "accept";
    
    //TODO: JUnit Test, if this key exist in accessrights.xml
    //public static final String OTHER_ACCESS_KEY = "ar_noAccess";
    
    /**
     *
     */
    private static final long serialVersionUID  = 494785477616915696L;
    
    private static final Logger logger = LoggerFactory.getLogger(EmdRights.class);

    private List<BasicString> dcRights          = new ArrayList<BasicString>();
    private List<BasicString> termsAccessRights = new ArrayList<BasicString>();
    private List<BasicString> termsLicense      = new ArrayList<BasicString>();
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
                accessCat = AccessCategory.valueOf(termsAccessRights.get(0).getValue());
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
        setAccessCategory(accessCat, EmdConstants.SCHEME_ID_COMMON_ACCESSRIGHTS);
    }
    
    public void setAccessCategory(AccessCategory accessCat, String schemeId)
    {
        termsAccessRights.clear();
        BasicString bs = new BasicString(accessCat.toString());
        bs.setSchemeId(schemeId);
        termsAccessRights.add(bs);
    }

}
