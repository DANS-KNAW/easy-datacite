package nl.knaw.dans.easy.web.statistics;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for handling statistics logging.
 * 
 * @author GeorgiK
 */

public class StatisticsLogger {
    private static StatisticsLogger instance = null;
    private static final Logger logger = LoggerFactory.getLogger("statistics");
    private static final String SEPARATOR = " ; ";
    private static final String DOUBLEQUOTE = "\"";
    private static final String ESCAPECHAR = "\\";

    public void logEvent(StatisticsEvent eventType, StatisticsModel<?>... models) {
        EasySession session = EasySession.get();
        EasyUser user = null;
        String ip = EasyWicketApplication.getUserIpAddress();
        String userId = "Anonymous";
        String userGroups = "groups: ()";
        String userRoles = "roles: ()";

        if (session != null) {
            user = session.getUser();
        }

        if (user != null && !(user instanceof EasyUserAnonymous)) {
            userId = user.getId();
            userRoles = "roles: (" + commaSeparated(user.getRoles());
            userGroups = "groups: (" + commaSeparated(user.getGroups());
        }

        String log = escape(eventType.toString()) + SEPARATOR + escape(userId) + SEPARATOR + escape(userRoles) + SEPARATOR + escape(userGroups) + SEPARATOR
                + escape(ip) + SEPARATOR;

        if (models != null) {
            for (StatisticsModel<?> model : models) {
                HashMap<String, String> values = model.getLogValues();
                if (values != null) {
                    log += model.getName() + "(";
                    for (Entry<String, String> value : values.entrySet()) {
                        log += value.getKey() + ": \"" + escape(value.getValue()) + "\"" + SEPARATOR;
                    }
                    if (log.endsWith(SEPARATOR)) {
                        log = log.substring(0, log.length() - SEPARATOR.length());
                    }
                    log += ")" + SEPARATOR;
                }
            }
        }

        if (log.endsWith(SEPARATOR)) {
            log = log.substring(0, log.length() - SEPARATOR.length());
        }

        logger.info(log);
    }

    private <T> String commaSeparated(Set<T> elements) {
        String result = "";
        for (T r : elements) {
            result += r.toString() + ", ";
        }
        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2) + ")";
        } else {
            result += ")";
        }
        return result;
    }

    protected String escape(String str) {
        if (str != null)
            return str.replace(SEPARATOR, ESCAPECHAR + SEPARATOR) //
                    .replace(DOUBLEQUOTE, ESCAPECHAR + DOUBLEQUOTE);
        else
            return "";

    }

    public synchronized static StatisticsLogger getInstance() {
        if (instance == null) {
            instance = new StatisticsLogger();
        }
        return instance;
    }

}
