package nl.knaw.dans.common.lang;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTest {

    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void testSlf4jLoggerBinding() {
        logger.debug("A debug message");
    }

}
