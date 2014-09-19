package nl.knaw.dans.easy.web.authn.login;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.BeanWrapperImpl;

public class FederationUserFactory {
    @SpringBean(name = "federationLoginDebugEnabled")
    private Boolean federationLoginDebugEnabled;

    public FederationUserFactory() {
        InjectorHolder.getInjector().inject(FederationUserFactory.this);
    }

    public static interface Factory {
        FederationUser create(HttpServletRequest request);
    }

    public static class DefaultFactory implements Factory {
        @SpringBean(name = "federativeUserService")
        private FederativeUserService federativeUserService;

        public DefaultFactory() {
            InjectorHolder.getInjector().inject(DefaultFactory.this);
        }

        @Override
        public FederationUser create(HttpServletRequest request) {
            FederationUser appUser = new FederationUser();
            getMandatoryAttribute(request, federativeUserService.getPropertyNameShibSessionId());
            appUser.setUserId(getMandatoryAttribute(request, federativeUserService.getPropertyNameRemoteUser()));
            appUser.setEmail(getAttribute(request, federativeUserService.getPropertyNameEmail()));
            appUser.setGivenName(getAttribute(request, federativeUserService.getPropertyNameFirstName()));
            appUser.setSurName(getAttribute(request, federativeUserService.getPropertyNameSurname()));
            appUser.setHomeOrg(getAttribute(request, federativeUserService.getPopertyNameOrganization()));
            return appUser;
        }

        private String getAttribute(HttpServletRequest request, String popertyNameOrganization) {
            String attribute = (String) request.getAttribute(popertyNameOrganization);
            if (attribute != null && attribute.trim().length() == 0)
                attribute = null;
            return attribute;
        }

        private String getMandatoryAttribute(HttpServletRequest request, String attributeName) {
            String attributeValue = getAttribute(request, attributeName);
            if (attributeValue == null)
                throw new IllegalArgumentException(String.format("Attribute %s must be present", attributeName));
            return attributeValue;
        }
    }

    public static class DebugFactory implements Factory {
        @SpringBean(name = "federationLoginDebugUserFile")
        private String federationLoginDebugUserFile;

        public DebugFactory() {
            InjectorHolder.getInjector().inject(DebugFactory.this);
        }

        @Override
        public FederationUser create(HttpServletRequest request) {
            FederationUser user = new FederationUser();
            new BeanWrapperImpl(user).setPropertyValues(loadProperties());
            return user;
        }

        private Properties loadProperties() {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(new File(federationLoginDebugUserFile)));
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException("Could not find the FederationUser debug file", e);
            }
            catch (IOException e) {
                throw new RuntimeException("Could not read the FederationUser debug file", e);
            }
            return properties;
        }
    }

    static Factory factory;

    public static void setFactory(Factory factory) {
        FederationUserFactory.factory = factory;
    }

    public FederationUser create(HttpServletRequest request) {
        if (factory == null) {
            if (federationLoginDebugEnabled)
                factory = new DebugFactory();
            else
                factory = new DefaultFactory();
        }
        return factory.create(request);
    }
}
