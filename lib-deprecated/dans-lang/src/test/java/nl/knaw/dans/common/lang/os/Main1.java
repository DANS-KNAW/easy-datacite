package nl.knaw.dans.common.lang.os;

import java.io.IOException;

public class Main1
{

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        System.out.println("MAIN1 > Hello, this is " + Main1.class.getName());

        System.out.println("MAIN1 > Now executing Main2");

        String cmd = "/usr/local/vm-data/SVN/common/trunk/lang/src/test/resources/test-files/os/ostest2.sh";
        int exitValue = OS.execAndWait(cmd, System.out, System.err);

        System.out.println("MAIN1 > Main2 has exited with exit value " + exitValue);
        System.exit(exitValue);
    }

}
