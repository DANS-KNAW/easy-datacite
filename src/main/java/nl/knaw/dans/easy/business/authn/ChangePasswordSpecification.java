package nl.knaw.dans.easy.business.authn;

import nl.knaw.dans.easy.domain.authn.ChangePasswordMessenger;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswordSpecification
{

    private static Logger logger = LoggerFactory.getLogger(ChangePasswordSpecification.class);

    private ChangePasswordSpecification()
    {

    }

    public static boolean isSatisFiedBy(ChangePasswordMessenger messenger)
    {
        boolean satisfied = true;
        satisfied &= hasSufficientData(messenger);
        if (satisfied && !messenger.isMailContext())
        {
            logger.debug("No mail context: checking for authentication.");
            satisfied &= checkAuthentication(messenger);
        }
        else if (satisfied && messenger.isMailContext())
        {
            logger.debug("Mail context: checking for qualifiedState.");
            satisfied &= AuthenticationSpecification.checkUserStateForForgottenPassword(messenger.getUser());
        }
        return satisfied;
    }

    private static boolean checkAuthentication(ChangePasswordMessenger messenger)
    {
        boolean authenticated = false;
        UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication(messenger.getUserId(), messenger.getOldPassword());
        if (AuthenticationSpecification.isSatisfiedBy(authentication))
        {
            authenticated = true;
        }
        else
        {
            logger.debug("ChangePassword did not get past Authentication" + messenger.toString());
            messenger.setState(ChangePasswordMessenger.State.NotAuthenticated);
            messenger.getExceptions().addAll(authentication.getExceptions());
        }
        return authenticated;
    }

    private static boolean hasSufficientData(ChangePasswordMessenger messenger)
    {
        boolean hasSufficientData = true;
        if (StringUtils.isBlank(messenger.getNewPassword()))
        {
            hasSufficientData = false;
            messenger.setState(ChangePasswordMessenger.State.InsufficientData);
        }
        return hasSufficientData;
    }

}
