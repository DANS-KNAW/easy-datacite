package nl.knaw.dans.easy.business.authn;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgottenPasswordSpecification
{

    private static Logger logger = LoggerFactory.getLogger(ForgottenPasswordSpecification.class);

    public static boolean isSatisfiedBy(ForgottenPasswordMessenger messenger)
    {
        boolean satisfied = hasSufficientData(messenger) && usersCanBeFound(messenger) && anyQualifiedUsers(messenger);
        return satisfied;
    }

    private static boolean hasSufficientData(ForgottenPasswordMessenger messenger)
    {
        boolean hasSufficientData = true;
        if (StringUtils.isBlank(messenger.getUserId()) && StringUtils.isBlank(messenger.getEmail()))
        {
            hasSufficientData = false;
            logger.debug("Both userId and email are blank.");
            messenger.setState(State.InsufficientData);
        }
        return hasSufficientData;
    }

    // side effect: a list of users maybe loaded onto the messenger
    private static boolean usersCanBeFound(ForgottenPasswordMessenger messenger)
    {
        boolean userFound = false;
        if (StringUtils.isNotBlank(messenger.getUserId()))
        {
            userFound = findUserByUserId(messenger);
        }
        if (!userFound && StringUtils.isNotBlank(messenger.getEmail()))
        {
            userFound = findUserByEmail(messenger);
        }
        return userFound;
    }

    // side effect: a list of users maybe loaded onto the messenger
    private static boolean findUserByEmail(ForgottenPasswordMessenger messenger)
    {
        boolean userFound = false;
        try
        {
            List<EasyUser> users = Data.getUserRepo().findByEmail(messenger.getEmail());
            if (!users.isEmpty())
            {
                userFound = true;
                messenger.getUsers().addAll(users);
            }
            else
            {
                logger.debug("User cannot be found by email: " + messenger.getEmail());
                messenger.setState(State.UserNotFound);
            }
        }
        catch (RepositoryException e)
        {
            logger.error("Could not find user by email: ", e);
            messenger.setState(State.SystemError, e);
        }
        return userFound;
    }

    // side effect: a list of users maybe loaded onto the messenger
    private static boolean findUserByUserId(ForgottenPasswordMessenger messenger)
    {
        boolean userFound = false;
        try
        {
            EasyUser user = Data.getUserRepo().findById(messenger.getUserId());
            if (user != null)
            {
                userFound = true;
                messenger.addUser(user);
            }
        }
        catch (ObjectNotInStoreException e)
        {
            logger.debug("User cannot be found by userId: " + messenger.getUserId());
            messenger.setState(State.UserNotFound, e);
        }
        catch (RepositoryException e)
        {
            logger.error("Could not find user by id: ", e);
            messenger.setState(State.SystemError, e);
        }
        return userFound;
    }

    private static boolean anyQualifiedUsers(ForgottenPasswordMessenger messenger)
    {
        List<EasyUser> qualifiedUsers = new ArrayList<EasyUser>();
        for (EasyUser user : messenger.getUsers())
        {
            if (user.isQualified())
            {
                qualifiedUsers.add(user);
            }
        }
        messenger.getUsers().retainAll(qualifiedUsers);
        boolean anyUsersLeft = qualifiedUsers.size() > 0;
        if (!anyUsersLeft)
        {
            logger.debug("No qualified users. userId=" + messenger.getUserId() + " email=" + messenger.getEmail());
            messenger.setState(State.NoQualifiedUsers);
        }
        return anyUsersLeft;
    }

}
