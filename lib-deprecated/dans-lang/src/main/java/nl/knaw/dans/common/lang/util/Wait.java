package nl.knaw.dans.common.lang.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pause execution.
 */
public class Wait {

    private static final Logger logger = LoggerFactory.getLogger(Wait.class);

    public static void minutes(long m) {
        milliSeconds(m * 60 * 1000);
    }

    public static void seconds(long s) {
        milliSeconds(s * 1000);
    }

    public static void milliSeconds(long ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            logger.warn("Wait time interrupted: ", e);
        }
    }

}
