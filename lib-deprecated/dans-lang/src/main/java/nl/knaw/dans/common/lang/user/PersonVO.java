package nl.knaw.dans.common.lang.user;

import java.util.regex.Pattern;

import nl.knaw.dans.common.lang.annotations.ldap.LdapAttribute;

import org.apache.commons.lang.StringUtils;

// import nl.knaw.dans.easy.dai.ldap.annotations.LdapAttribute;

public class PersonVO implements Person
{

    /**
     *
     */
    private static final long serialVersionUID = -4419904082045457502L;

    @LdapAttribute(id = "title")
    private String title;

    @LdapAttribute(id = "initials")
    private String initials;

    @LdapAttribute(id = "givenName")
    private String firstname;

    /** used for words that are positioned between a person's first and last name.
     * Surname prefix, in Dutch (tussenvoegsels) like "van den" or "de"
     */
    @LdapAttribute(id = "dansPrefixes")
    private String prefixes;

    @LdapAttribute(id = "sn", required = true)
    private String surname;

    @LdapAttribute(id = "mail")
    private String email;

    @LdapAttribute(id = "o")
    private String organization;

    @LdapAttribute(id = "ou")
    private String department;

    @LdapAttribute(id = "employeeType")
    private String function;

    @LdapAttribute(id = "postalAddress")
    private String address;

    @LdapAttribute(id = "postalCode")
    private String postalCode;

    @LdapAttribute(id = "l")
    private String city;

    @LdapAttribute(id = "st")
    private String country;

    @LdapAttribute(id = "telephoneNumber")
    private String telephone;

    @LdapAttribute(id = "dansAltTel")
    private String alternativeTelephone;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getInitials()
    {
        return initials;
    }

    public void setInitials(String initials)
    {
        this.initials = initials;
    }

    public String getFirstname()
    {
        return firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getPrefixes()
    {
        return prefixes;
    }

    public void setPrefixes(String prefixes)
    {
        this.prefixes = prefixes;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getOrganization()
    {
        return organization;
    }

    public void setOrganization(String organization)
    {
        this.organization = organization;
    }

    public String getDepartment()
    {
        return department;
    }

    public void setDepartment(String department)
    {
        this.department = department;
    }

    public String getFunction()
    {
        return function;
    }

    public void setFunction(String function)
    {
        this.function = function;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public void setTelephone(String telephone)
    {
        if (telephone != null && !Pattern.matches(PATTERN_TELEPHONE, telephone))
        {
            throw new IllegalArgumentException("Invalid syntax for telephone numbers: " + telephone);
        }
        this.telephone = telephone;
    }

    public String getAlternativeTelephone()
    {
        return alternativeTelephone;
    }

    public void setAlternativeTelephone(String alternativeTelephone)
    {
        this.alternativeTelephone = alternativeTelephone;
    }

    /**
     * {@inheritDoc}
     */
    @LdapAttribute(id = "cn", required = true)
    public String getCommonName()
    {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(surname))
        {
            sb.append(surname);
            sb.append(", ");
        }
        if (StringUtils.isNotBlank(title))
        {
            sb.append(title);
            sb.append(" ");
        }
        if (StringUtils.isNotBlank(initials))
        {
            sb.append(initials);
            sb.append(" ");
        }
        if (StringUtils.isNotBlank(firstname))
        {
            if (StringUtils.isNotBlank(initials))
                sb.append("(");
            sb.append(firstname);
            if (StringUtils.isNotBlank(initials))
                sb.append(")");
            sb.append(" ");
        }
        if (StringUtils.isNotBlank(prefixes))
        {
            sb.append(prefixes);

        }
        return sb.toString().trim();
    }

    /**
     * {@inheritDoc}
     */
    @LdapAttribute(id = "displayName")
    public String getDisplayName()
    {
        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotBlank(firstname))
        {
            sb.append(firstname);
            sb.append(" ");
        }
        else if (StringUtils.isNotBlank(initials)) // migrated users have no firstname
        {
            sb.append(initials);
            sb.append(" ");
        }

        if (StringUtils.isNotBlank(prefixes))
        {
            sb.append(prefixes);
            sb.append(" ");
        }
        if (StringUtils.isNotBlank(surname))
        {
            sb.append(surname);
        }
        return sb.toString().trim();
    }

    public void synchronizeOn(Person otherPerson)
    {
        this.setAddress(otherPerson.getAddress());
        this.setCity(otherPerson.getCity());
        this.setCountry(otherPerson.getCountry());
        this.setDepartment(otherPerson.getDepartment());
        this.setEmail(otherPerson.getEmail());
        this.setFirstname(otherPerson.getFirstname());
        this.setFunction(otherPerson.getFunction());
        this.setInitials(otherPerson.getInitials());
        this.setOrganization(otherPerson.getOrganization());
        this.setPostalCode(otherPerson.getPostalCode());
        this.setPrefixes(otherPerson.getPrefixes());
        this.setSurname(otherPerson.getSurname());
        this.setTelephone(otherPerson.getTelephone());
        this.setTitle(otherPerson.getTitle());
    }

}
