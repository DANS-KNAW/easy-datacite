package nl.knaw.dans.common.lang.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RLTestReporter extends Reporter {

    private static final Logger logger = LoggerFactory.getLogger(RLTestReporter.class);

    @Override
    public void info(Event event) {
        logger.info("Recieved event: " + event.toString());
        super.info(event);
        logger.info("Dispatched event: " + event.toString());
    }

}
