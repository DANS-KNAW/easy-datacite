package nl.knaw.dans.common.lang.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamCatcher extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(StreamCatcher.class);

    private final InputStream is;
    private final Appendable appendable;

    StreamCatcher(InputStream is, Appendable appendable) {
        this.is = is;
        this.appendable = appendable;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                appendable.append(line);
                appendable.append("\n");
            }
        }
        catch (IOException ioe) {
            logger.error("Ending catch stream.", ioe);
        }
    }

}
