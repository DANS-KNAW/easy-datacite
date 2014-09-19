package nl.knaw.dans.easy.security.authz;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.user.User;

/**
 * Provides implementations of {@link AuthzStrategy}.
 * <p/>
 * To obtain a single AuthzStrategy, use the static method of this class:
 * 
 * <pre>
 * AuthzStrategy strategy = AuthzStrategyProvider.new AuthzStrategy(name, user, target, contextObjects);
 * </pre>
 * <p/>
 * To obtain multiple AuthzStrategies for a given context, instantiate this class for the context, and use the object method:
 * 
 * <pre>
 * AuthzStrategyProvider provider = new AuthzStrategyProvider(user, contextObjects);
 * for (TargetableObject target : targetObjects) {
 *     AuthzStrategy strategy = provider.getAuthzStrategy(name, target);
 *     target.setAuthzStrategy(strategy);
 * }
 * </pre>
 */
public class AuthzStrategyProvider {

    private static Map<String, AuthzStrategy> STRATEGIES;

    private final User user;
    private final Object[] contextObjects;

    private Map<String, AuthzStrategy> loadedStrategies = new HashMap<String, AuthzStrategy>();

    public AuthzStrategyProvider(User user, Object... contextObjects) {
        this.user = user;
        this.contextObjects = contextObjects;
    }

    public AuthzStrategy getAuthzStrategy(String name, Object target) {
        AuthzStrategy strategy = loadedStrategies.get(name);
        if (strategy == null) {
            strategy = newAuthzStrategy(name, user, target, contextObjects);
            loadedStrategies.put(name, strategy);
        }
        return strategy.sameStrategy(target);
    }

    public static AuthzStrategy newAuthzStrategy(String name, User user, Object target, Object... contextObjects) {
        AuthzStrategy aStrategy = getStrategies().get(name);
        if (aStrategy == null) {
            throw new IllegalStateException("Unknown AuthzStrategy: " + name);
        } else {
            return aStrategy.newStrategy(user, target, contextObjects);
        }
    }

    private static Map<String, AuthzStrategy> getStrategies() {
        if (STRATEGIES == null) {
            STRATEGIES = new HashMap<String, AuthzStrategy>();
            addToStrategies(new EasyFileItemAuthzStrategy());
            addToStrategies(new EasyFileItemVOAuthzStrategy());
            addToStrategies(new EasyItemContainerAuthzStrategy());
            addToStrategies(new EasyItemContainerVOAuthzStrategy());
        }
        return STRATEGIES;
    }

    private static void addToStrategies(AuthzStrategy strategy) {
        STRATEGIES.put(strategy.getClass().getName(), strategy);
    }

}
