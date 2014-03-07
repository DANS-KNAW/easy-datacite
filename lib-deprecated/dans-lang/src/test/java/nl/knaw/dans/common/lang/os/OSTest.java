package nl.knaw.dans.common.lang.os;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import nl.knaw.dans.common.lang.util.Wait;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSTest
{
    private static final Logger logger = LoggerFactory.getLogger(OSTest.class);

    @Ignore("involves being on linux")
    @Test(expected = IOException.class)
    public void setAllRWXFile() throws Exception
    {
        File file = new File("/home/easy/batch/test/for/set/all/rwx");
        assertFalse(file.exists());
        //file.mkdirs();
        try
        {
            OS.setAllRWX(file);
        }
        catch (IOException e)
        {
            logger.error("Caught error: ", e);
            throw (e);
        }
    }

    @Ignore("involves local environment")
    @Test
    public void move() throws Exception
    {
        String testFolder = "/usr/local/vm-data/SVN/common/trunk/lang/src/test/resources/test-files/os/";

        File os = new File(testFolder);
        File folder1 = new File(os, "folder1");
        File folder3 = new File(os, "folder3");
        folder3.renameTo(folder1);

        //String cmd = "mv " + testFolder + "folder2/ " + testFolder + "folder3";

        //OS.execAndWait(cmd, System.out, System.err);
    }

    @Ignore("involves local environment")
    @Test
    public void execAndWait() throws Exception
    {
        String cmd = "/usr/local/vm-data/SVN/common/trunk/lang/src/test/resources/test-files/os/ostest1.sh";
        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();
        int exitValue = OS.execAndWait(cmd, out, err);

        System.out.println("OSTest > exitValue of test =" + exitValue);

        System.out.println("OSTest > This is the output:");
        System.out.println(out.toString());

        System.out.println("OSTest > This is the errput:");
        System.out.println(err.toString());
    }

    // Keeps running, gives output as long as this vm is up.
    @Ignore("involves local environment")
    @Test
    public void exec() throws Exception
    {
        String cmd = "/usr/local/vm-data/SVN/common/trunk/lang/src/test/resources/test-files/os/ostest1.sh";
        OS.exec(cmd, System.out, System.err);
        Wait.minutes(1);
    }

    // Keeps running, gives output as long as this vm is up.
    @Ignore("involves local environment")
    @Test
    public void execRunnable()
    {
        Thread thread = new Thread(new ExecutableThread());
        thread.setDaemon(false);
        thread.start();
        Wait.minutes(1);
    }

    class ExecutableThread implements Runnable
    {

        @Override
        public void run()
        {
            try
            {
                String cmd = "/usr/local/vm-data/SVN/common/trunk/lang/src/test/resources/test-files/os/ostest1.sh";
                OS.execAndWait(cmd, System.out, System.err);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

}
