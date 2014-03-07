package nl.knaw.dans.easy.servicelayer.services;

import java.net.URL;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

/**
 * For users that have a federative account. So not a normal UserService because the authentication is
 * done elsewhere. User information specific for EASY is stored in an EasyUser object
 */
public interface FederativeUserService
{
    EasyUser getUserById(EasyUser sessionUser, String fedUserId) throws ObjectNotAvailableException, ServiceException;

    // TODO support adding a federative user, given an EasyUser object and a federative user id
    void addFedUserToEasyUserIdCoupling(String fedUserId, String easyUserId) throws ServiceException;

    boolean isFederationLoginEnabled();

    URL getFederationUrl();

    String getPopertyNameOrganization();

    String getPropertyNameSurname();

    String getPropertyNameFirstName();

    String getPropertyNameEmail();

    String getPropertyNameRemoteUser();

    String getPropertyNameShibSessionId();
}
