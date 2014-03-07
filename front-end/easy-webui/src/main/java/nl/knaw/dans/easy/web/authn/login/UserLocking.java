package nl.knaw.dans.easy.web.authn.login;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class UserLocking
{
    private static int MAX_TRIES = 10;
    private static int WAIT_TIME = 1000 * 60 * 30;

    private static Map<String, Date> userLockTime = new HashMap<String, Date>();
    private static Map<String, Integer> userTries = new HashMap<String, Integer>();

    static void addTry(String userId)
    {
        updateTries(userId);
        calculateLockCondition(userId);
    }

    private static void updateTries(String userId)
    {
        if (!userTries.containsKey(userId))
        {
            userTries.put(userId, 0);
        }

        userTries.put(userId, userTries.get(userId) + 1);
    }

    private static void calculateLockCondition(String userId)
    {
        if (userLockTime.containsKey(userId))
        {
            Date d = userLockTime.get(userId);

            if ((new Date().getTime()) - d.getTime() > WAIT_TIME)
            {
                userLockTime.remove(userId);
            }
        }

        if (userTries.containsKey(userId) && userTries.get(userId) > MAX_TRIES)
        {
            userLockTime.put(userId, new Date());
            userTries.remove(userId);
        }
    }

    static boolean isUserLocked(String userId)
    {
        calculateLockCondition(userId);
        return userLockTime.containsKey(userId);
    }
}
