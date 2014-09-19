package nl.knaw.dans.easy.business.authn;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTokenList {
    private static final String TOKEN_SEPARATOR = ">";

    private static Logger logger = LoggerFactory.getLogger(AbstractTokenList.class);

    private Object syncObject = new Object();

    public abstract Map<String, String> getTokenMap();

    /**
     * Put token in token map.
     * 
     * @param userId
     *        id of the user
     * @param requestTime
     *        time of request
     * @param requestToken
     *        generated token
     */
    protected void putTokenInTokenList(final String userId, final String requestTime, final String requestToken) {
        synchronized (syncObject) {
            Map<String, String> tokenMap = getTokenMap();
            final String tokenString = userId + TOKEN_SEPARATOR + requestTime + TOKEN_SEPARATOR + requestToken;
            tokenMap.put(userId, tokenString);
            // TODO store userId and token on persistent medium
            logger.debug(this.getClass().getSimpleName() + ": Added token " + tokenString + ". Pending request count=" + tokenMap.size());
        }
    }

    protected void removeTokenFromList(final String userId) {
        synchronized (syncObject) {
            Map<String, String> tokenMap = getTokenMap();
            final String removed = tokenMap.remove(userId);
            if (removed != null) {
                // TODO remove userId and token from persistent medium
                logger.debug(this.getClass().getSimpleName() + ": Removed token for userId '" + userId + "'. Pending request count=" + tokenMap.size());
            } else {
                logger.debug(this.getClass().getSimpleName() + ": Token for userId '" + userId + "' not found" + ". Pending request count=" + tokenMap.size());
            }
        }
    }

    /**
     * Method to check if a token is valid in the internal tokenMap.
     * 
     * @param userId
     *        UserId for which the token is created.
     * @param requestTime
     *        Time the token was created
     * @param requestToken
     *        Token
     * @return True if token exists for user and is valid.
     */
    public boolean checkToken(final String userId, final String requestTime, final String requestToken) {
        String checkTokenString = userId + TOKEN_SEPARATOR + requestTime + TOKEN_SEPARATOR + requestToken;
        logger.debug("Check token for " + checkTokenString);
        boolean validToken = false;
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(requestTime) || StringUtils.isEmpty(requestToken)) {
            logger.error(this.getClass().getSimpleName() + ": checkToken is called with invalid parameters: " + checkTokenString);
            return false;
        }
        synchronized (syncObject) {
            Map<String, String> tokenMap = getTokenMap();
            if (tokenMap.containsKey(userId)) {
                String tokenString = tokenMap.get(userId);

                if (tokenString.equals(checkTokenString)) {
                    validToken = true;
                }
            } else {
                logger.debug(this.getClass().getSimpleName() + ": No request token found for userId '" + userId + "'");
            }
        }
        return validToken;
    }

    public int pendingRequests() {
        int pendingRequests = 0;
        synchronized (syncObject) {
            pendingRequests = getTokenMap().size();
        }
        return pendingRequests;
    }

    public void reset() {
        synchronized (syncObject) {
            getTokenMap().clear();
        }
    }

}
