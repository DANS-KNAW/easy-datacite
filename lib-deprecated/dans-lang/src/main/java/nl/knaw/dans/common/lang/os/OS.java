package nl.knaw.dans.common.lang.os;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OS {

    public static final String PROP_OSNAME = "os.name";

    public static final String OS_LINUX = "Linux";

    public static final String OS_MAC = "Mac OS X";

    private static OsStrategy OS_STRATEGY;

    private static final Logger logger = LoggerFactory.getLogger(OS.class);

    public static boolean isLinux() {
        return OS_LINUX.equals(System.getProperty(PROP_OSNAME));
    }

    public static boolean isMac() {
        return OS_MAC.equals(System.getProperty(PROP_OSNAME));
    }

    public static int setAllRWX(File file) throws IOException {
        return setAllRWX(file.getAbsolutePath());
    }

    public static int setAllRWX(String filename) throws IOException {
        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();
        int exitValue = getStrategy().setAllRWX(filename, out, err);
        String outStr = out.toString();
        String errStr = err.toString();
        if (outStr.length() > 0)
            logger.info(outStr);
        if (errStr.length() > 0)
            logger.error(errStr);
        if (exitValue != 0) {
            throw new IOException("Setting file rights returned with exit value " + exitValue);
        }
        return exitValue;
    }

    public static int setAllRWX(File file, Appendable out, Appendable err) throws IOException {
        return getStrategy().setAllRWX(file, out, err);
    }

    public static int setAllRWX(String filename, Appendable out, Appendable err) throws IOException {
        return getStrategy().setAllRWX(filename, out, err);
    }

    /**
     * Executes the command and waits until the process has finished. No guarantee is given that the <code>cmd</code> will be executable on the current OS.
     * 
     * @param cmd
     *        command to execute
     * @param out
     *        drain out stream of the process
     * @param err
     *        drain error stream of the process
     * @return exit value of the process
     * @throws IOException
     *         if something goes wrong
     */
    public static int execAndWait(String cmd, Appendable out, Appendable err) throws IOException {
        int exitValue = -1;
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(cmd);
        StreamCatcher errorCatcher = new StreamCatcher(process.getErrorStream(), err);
        StreamCatcher outCatcher = new StreamCatcher(process.getInputStream(), out);
        errorCatcher.start();
        outCatcher.start();
        try {
            exitValue = process.waitFor();
        }
        catch (InterruptedException e) {
            throw new IOException("Interrupted: ", e);
        }
        return exitValue;
    }

    /**
     * Executes the command and returns immediately. No guarantee is given that the <code>cmd</code> will be executable on the current OS.
     * <p/>
     * 
     * @param cmd
     * @param out
     * @param err
     * @throws IOException
     */
    public static void exec(String cmd, Appendable out, Appendable err) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(cmd);
        StreamCatcher errorCatcher = new StreamCatcher(process.getErrorStream(), err);
        StreamCatcher outCatcher = new StreamCatcher(process.getInputStream(), out);
        errorCatcher.start();
        outCatcher.start();
    }

    private static OsStrategy getStrategy() {
        if (OS_STRATEGY == null) {
            initStrategy();
        }
        return OS_STRATEGY;
    }

    private static void initStrategy() {
        if (isLinux() || isMac()) {
            OS_STRATEGY = new LinuxOsStrategy();
        } else {
            throw new IllegalStateException("No strategy for os " + System.getProperty(PROP_OSNAME));
        }
    }

}
