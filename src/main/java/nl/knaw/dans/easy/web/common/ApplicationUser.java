/**
 * 
 */
package nl.knaw.dans.easy.web.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * ApplicationUser that is maintained within the session.
 * 
 * @author Herman Suijs
 */
public class ApplicationUser implements Serializable
{
    public static final String USER_ID = "userId";
    public static final String TITLE = "title";
    public static final String INITIALS = "initials";
    public static final String PREFIXES = "prefixes";
    public static final String SURNAME = "surname";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String CONFIRM_PASSWORD = "confirmPassword";
    public static final String FUNCTION = "function";
    public static final String TELEPHONE = "telephone";
    public static final String DISCIPLINE1 = "discipline1";
    public static final String DISCIPLINE2 = "discipline2";
    public static final String DISCIPLINE3 = "discipline3";
    public static final String DAI = "dai";
    public static final String ORGANIZATION = "organization";
    public static final String DEPARTMENT = "department";
    public static final String ADDRESS = "address";
    public static final String POSTAL_CODE = "postalCode";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String OPTS_FOR_NEWSLETTER = "optsForNewsletter";
    public static final String ACCEPT_CONDITIONS = "acceptConditions";
    public static final String LOG_MY_ACTIONS = "logMyActions";

    private static final long serialVersionUID = -6040242842409608573L;

    private String password;
    private String confirmPassword;

    private String dai;

    /**
     * Wrapped user model object.
     */
    private final EasyUser businessUser;

    /**
     * Default constructor.
     */
    public ApplicationUser()
    {
        this(new EasyUserImpl());
    }

    /**
     * Return wrapped business user.
     * 
     * @return business user.
     */
    public EasyUser getBusinessUser()
    {
        return this.businessUser;
    }

    /**
     * Constructor used with model user.
     * 
     * @param user
     *        model user.
     */
    public ApplicationUser(final EasyUser user)
    {
        this.businessUser = user;
    }

    /**
     * Get email.
     * 
     * @return email.
     */
    public String getEmail()
    {
        return this.businessUser.getEmail();
    }

    /**
     * Get user.
     * 
     * @return user
     */
    public String getUserId()
    {
        return this.businessUser.getId();
    }

    /**
     * Set email.
     * 
     * @param email
     *        email
     */
    public void setEmail(final String email)
    {
        this.businessUser.setEmail(email);
    }

    /**
     * Set password.
     * 
     * @param password
     *        password
     */
    public void setPassword(final String password)
    {
        this.businessUser.setPassword(password);
        this.password = password;
    }

    /**
     * Get password only when entered in screen.
     * 
     * @return password.
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * Set user.
     * 
     * @param userId
     *        user
     */
    public void setUserId(final String userId)
    {
        this.businessUser.setId(userId);
    }

    public void setTitle(String title)
    {
        this.businessUser.setTitle(title);
    }

    public String getTitle()
    {
        return businessUser.getTitle();
    }

    public void setInitials(String initials)
    {
        businessUser.setInitials(initials);
    }

    public String getInitials()
    {
        return businessUser.getInitials();
    }

    public void setFirstname(String firstname)
    {
        businessUser.setFirstname(firstname);
    }

    public String getFirstname()
    {
        return businessUser.getFirstname();
    }

    public void setPrefixes(String prefixes)
    {
        businessUser.setPrefixes(prefixes);
    }

    public String getPrefixes()
    {
        return businessUser.getPrefixes();
    }

    public void setSurname(String surname)
    {
        businessUser.setSurname(surname);
    }

    public String getSurname()
    {
        return businessUser.getSurname();
    }

    public void setOptsForNewsletter(boolean opts)
    {
        businessUser.setOptsForNewsletter(opts);
    }

    public boolean getOptsForNewsletter()
    {
        return businessUser.getOptsForNewsletter();
    }

    public void setLogMyActions(boolean logMyActions)
    {
        businessUser.setLogMyActions(logMyActions);
    }

    public boolean getLogMyActions()
    {
        return businessUser.isLogMyActions();
    }

    /**
     * Get name.
     * 
     * @return name
     */
    public String getCommonName()
    {
        return this.businessUser.getCommonName();
    }

    /**
     * Get confirm Password. disciplines
     * 
     * @return confirm password
     */
    public String getConfirmPassword()
    {
        return this.confirmPassword;
    }

    /**
     * Set confirm password.
     * 
     * @param confirmPassword
     *        password
     */
    public void setConfirmPassword(final String confirmPassword)
    {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Accepts the General DANS Conditions of Use at the time of registration.
     * 
     * @return <code>true</code> if accepted, <code>false</code> otherwise
     */
    public boolean getAcceptConditions()
    {
        return businessUser.getAcceptConditionsOfUse();
    }

    /**
     * Accept the General DANS Conditions of Use at the time of registration.
     * 
     * @param accept
     *        <code>true</code> if accepting the conditions of use, <code>false</code> otherwise
     */
    public void setAcceptConditions(boolean accept)
    {
        businessUser.setAcceptConditionsOfUse(accept);
    }

    public String getFunction()
    {
        return businessUser.getFunction();
    }

    public void setFunction(String function)
    {
        businessUser.setFunction(function);
    }

    public String getTelephone()
    {
        return businessUser.getTelephone();
    }

    public void setTelephone(String telephone)
    {
        businessUser.setTelephone(telephone);
    }

    public KeyValuePair getDiscipline1()
    {
        return DisciplineUtils.getDisciplineItemById(businessUser.getDiscipline1());
    }

    public void setDiscipline1(KeyValuePair discipline)
    {
        businessUser.setDiscipline1(discipline == null ? null : discipline.getKey());
    }

    public KeyValuePair getDiscipline2()
    {
        return DisciplineUtils.getDisciplineItemById(businessUser.getDiscipline2());
    }

    public void setDiscipline2(KeyValuePair discipline)
    {
        businessUser.setDiscipline2(discipline == null ? null : discipline.getKey());
    }

    public KeyValuePair getDiscipline3()
    {
        return DisciplineUtils.getDisciplineItemById(businessUser.getDiscipline3());
    }

    public void setDiscipline3(KeyValuePair discipline)
    {
        businessUser.setDiscipline3(discipline == null ? null : discipline.getKey());
    }

    public String getDai()
    {
        return businessUser.getDai();
    }

    public void setDai(String dai)
    {
        businessUser.setDai(dai);
    }

    public String getOrganization()
    {
        return businessUser.getOrganization();
    }

    public void setOrganization(String organization)
    {
        businessUser.setOrganization(organization);
    }

    public String getDepartment()
    {
        return businessUser.getDepartment();
    }

    public void setDepartment(String department)
    {
        businessUser.setDepartment(department);
    }

    public String getAddress()
    {
        return businessUser.getAddress();
    }

    public void setAddress(String address)
    {
        businessUser.setAddress(address);
    }

    public String getPostalCode()
    {
        return businessUser.getPostalCode();
    }

    public void setPostalCode(String postalCode)
    {
        businessUser.setPostalCode(postalCode);
    }

    public String getCity()
    {
        return businessUser.getCity();
    }

    public void setCity(String city)
    {
        businessUser.setCity(city);
    }

    public String getCountry()
    {
        return businessUser.getCountry();
    }

    public void setCountry(String country)
    {
        businessUser.setCountry(country);
    }

    /**
     * Test if equal.
     * 
     * @param obj
     *        object to test
     * @return true if equal
     */
    @Override
    public boolean equals(final Object obj)
    {
        boolean equals = false;
        if (obj != null)
        {
            if (obj == this)
            {
                equals = true;
            }
            else
            {
                if (obj.getClass() == this.getClass())
                {
                    final ApplicationUser otherUser = (ApplicationUser) obj;
                    equals = this.getBusinessUser().equals(otherUser.getBusinessUser());
                }
            }
        }
        return equals;
    }

    /**
     * Return hashcode.
     * 
     * @return hashcode.
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(3, 5).append(getBusinessUser()).toHashCode();
    }

    /**
     * String representation.
     * 
     * @return string representation
     */
    @Override
    public String toString()
    {
        return "Easy Application user: " + getUserId() + ", named: " + getCommonName();
    }

}
